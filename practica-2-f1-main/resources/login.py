from lock import lock
from http import HTTPStatus
from flask_restful import Resource, reqparse
from models.accounts import AccountsModel


class Login(Resource):
    def post(self):
        with lock.lock:
            data = self.get_data()
            account = AccountsModel.get_by_username(data['username'])

            # return error if account doesn't exist
            if account is None:
                return {'message': "Account with username [{}] doesn't exist".format(data['username'])}, HTTPStatus.NOT_FOUND
            # return error if account doesn't exist
            if (data['password']) is None:
                return {'message': "Password [{}] doesn't exist".format(data['password'])}, HTTPStatus.CONFLICT
            # return error if password doesn't match with username
            if not account.verify_password_(data['password']):
                return {'message': "Password [{}] doesn't match with username".format(data['password'])}, HTTPStatus.BAD_REQUEST

            token = account.generate_auth_token()
            return {'token': token.decode('ascii')}, 200

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        parser.add_argument('username', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('password', type=str, required=True, help="This field cannot be left blank")

        return parser.parse_args()

