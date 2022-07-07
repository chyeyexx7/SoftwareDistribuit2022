from lock import lock
from flask_restful import Resource, reqparse
from http import HTTPStatus
from sqlalchemy import exc

from db import db
from models.accounts import AccountsModel, auth, g


class Accounts(Resource):

    @auth.login_required()
    def get(self, username):
        account = AccountsModel.get_by_username(username)
        # return account if it exists
        if account is None:  # return error message if order doesn't exist
            return {'message': 'This username [{}] does not exist'.format(username)}, HTTPStatus.NOT_FOUND
        # return error if the account username doesn't match
        if account.username != g.user.username:
            return {'message': "Bad authorization user"}, HTTPStatus.BAD_REQUEST

        return {'account': account.json()}, HTTPStatus.OK

    def post(self):
        with lock.lock:
            data = self.get_data()
            account = AccountsModel.get_by_username(data['username'])

            # return error if account doesn't exist
            if account is not None:
                return {'message': "Account with username [{}] already exists".format(data['username'])}, \
                       HTTPStatus.CONFLICT

            # return error if account doesn't exist
            if data['password'] is None:
                return {'message': "Password [{}] is necessary to register"}, HTTPStatus.CONFLICT

            new_account = AccountsModel(data['username'])
            # Assign the hashed password to the user
            new_account.hash_password(data['password'])  # For now password with no conditions, mayus, symbol, etc
            # update DB
            try:
                new_account.save_to_db()
                return new_account.json(), HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'Error while creating new account'}, HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='user')
    def delete(self, username):
        with lock.lock:
            account = AccountsModel.get_by_username(username)
            if account is None:
                return {'message': "account with username [{}] doesn't exists".format(username)}, HTTPStatus.NOT_FOUND
            # return error if the account username doesn't match
            if account.username != g.user.username:
                return {'message': "Bad authorization user"}, HTTPStatus.BAD_REQUEST
            del account.orders
            account.delete_from_db()
            return {'message': 'account deleted successfully'}, HTTPStatus.OK

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        parser.add_argument('username', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('password', type=str, required=True, help="This field cannot be left blank")

        return parser.parse_args()


class AccountsList(Resource):

    @auth.login_required(role='admin')
    def get(self):  # Return all accounts
        accounts = AccountsModel.get_all()
        return {"Accounts List": [x.json() for x in accounts]}, \
               HTTPStatus.OK if accounts else HTTPStatus.NOT_FOUND


