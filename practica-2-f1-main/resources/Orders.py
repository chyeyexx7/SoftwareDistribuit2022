from lock import lock
from flask_restful import Resource, reqparse
from http import HTTPStatus
from sqlalchemy import exc

from models.accounts import *
from models.matches import MatchesModel
from models.orders import OrdersModel


class Orders(Resource):

    @auth.login_required()
    def get(self, username):
        order = OrdersModel.get_by_username(username)
        # return order if it exists
        if order is None:  # return error message if order doesn't exist
            return {'message': 'The order with username [{}] does not exist'.format(username)}, HTTPStatus.NOT_FOUND
        return {'order': order.json()}, HTTPStatus.OK

    @auth.login_required(role='user')
    def post(self, username):
        with lock.lock:
            # get the arguments
            data = self.get_data()

            account = AccountsModel.get_by_username(username)
            match = MatchesModel.get_by_id(data['match_id'])
            # return error if account doesn't exist
            if account is None:
                return {'message': "account with username [{}] doesn't exist".format(username)}, HTTPStatus.CONFLICT

            # return error if the account username doesn't match
            if account.username != g.user.username:
                return {'message': "Bad authorization user"}, HTTPStatus.BAD_REQUEST

            # return error if match doesn't exist
            if match is None:
                return {'message': "match with id [{}] doesn't exist".format(data['match_id'])}, HTTPStatus.CONFLICT

            # check if the account has enough money to buy the tickets
            tickets_bought = data['tickets_bought']
            if tickets_bought < 0:
                return {'message': "not possible to buy negative tickets"}, HTTPStatus.BAD_REQUEST
            if account.available_money < (match.price * tickets_bought):
                return {'message': "account with username [{}] doesn't have enough money".format(username)}, \
                    HTTPStatus.CONFLICT

            # check if the match has enough tickets for sale
            if match.total_available_tickets < tickets_bought:
                return {'message': "match with id [{}] doesn't have enough tickets".format(data['match_id'])}, \
                    HTTPStatus.CONFLICT

            # update DB
            try:
                match.total_available_tickets -= tickets_bought
                account.available_money -= match.price * tickets_bought
                new_order = OrdersModel(data['match_id'], tickets_bought)
                account.orders.append(new_order)
                db.session.commit()
                return new_order.json(), HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while creating new order'}, HTTPStatus.INTERNAL_SERVER_ERROR

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        parser.add_argument('match_id', type=int, required=True, help="This field cannot be left blank")
        parser.add_argument('tickets_bought', type=int, required=True, help="This field cannot be left blank")

        return parser.parse_args()


class OrdersList(Resource):

    @auth.login_required(role='admin')
    def get(self):
        # return all orders
        orders = OrdersModel.get_all()
        return {"Orders List": [x.json() for x in orders]},\
            HTTPStatus.OK if orders else HTTPStatus.NOT_FOUND

    @auth.login_required(role='user')
    def post(self, username):
        with lock.lock:
            # get the arguments
            data = self.get_data()

            account = AccountsModel.get_by_username(username)
            # if account doesn't exist
            if account is None:
                return {'message': "account with username [{}] doesn't exist".format(username)}, \
                    HTTPStatus.CONFLICT

            if account.username != g.user.username:
                return {'message': "token mismatch"}, HTTPStatus.UNAUTHORIZED

            # iterate over the orders and update DB
            for order in data['orders']:
                match = MatchesModel.get_by_id(order['match_id'])
                # return error if match doesn't exist
                if match is None:
                    return {'message': "match with id [{}] doesn't exist".format(order['match_id'])}, \
                        HTTPStatus.CONFLICT
                # check if the account has enough money to buy the tickets
                tickets_bought = order['tickets_bought']
                if tickets_bought < 0:
                    return {'message': "not possible to buy negative tickets"}, HTTPStatus.BAD_REQUEST
                if account.available_money < (match.price * tickets_bought):
                    return {'message': "account with username [{}] doesn't have enough money".format(username)}, \
                        HTTPStatus.CONFLICT

                # check if the match has enough tickets for sale
                if match.total_available_tickets < tickets_bought:
                    return {'message': "match with id [{}] doesn't have enough tickets".format(match['match_id'])}, \
                        HTTPStatus.CONFLICT

                # update the values of the Models if all the conditions are met
                match.total_available_tickets -= tickets_bought
                account.available_money -= match.price * tickets_bought
                new_order = OrdersModel(order['match_id'], tickets_bought)
                account.orders.append(new_order)

            try:
                db.session.commit()
                return new_order.json(), HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while creating new order'}, HTTPStatus.INTERNAL_SERVER_ERROR

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        # use action='append' so we can get a list of dicts
        parser.add_argument('orders', type=dict, action='append', required=True, help="This field cannot be left blank")

        return parser.parse_args()
