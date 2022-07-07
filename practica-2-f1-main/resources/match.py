from lock import lock
from flask_restful import Resource, reqparse
from http import HTTPStatus
import dateutil.parser
from sqlalchemy import exc

from db import db
from models.matches import MatchesModel
from models.accounts import auth


class Match(Resource):

    def get(self, id):
        match = MatchesModel.get_by_id(id)
        # return match if it exists
        if match is None:
            return {'message': 'match with [{}] does not exist'.format(id)}, HTTPStatus.NOT_FOUND
        return {'match': match.json()}, HTTPStatus.OK

    @auth.login_required(role='admin')
    def post(self, id=None):
        with lock.lock:
            # get the arguments
            data = self.get_data()

            # if id is not None
            if id is not None:
                # return error if id exists
                if MatchesModel.get_by_id(id) is not None:
                    return {'message': "match with id [{}] already exists".format(id)}, HTTPStatus.CONFLICT

            # add new match to db
            new_match = MatchesModel(dateutil.parser.parse(data['date']), data['price'])
            new_match.id = id
            if data['visitor_id'] and data['local_id']:  # add teams to match
                # visitor = TeamsModel.get_by_id(data['visitor_id'])
                # local = TeamsModel.get_by_id(data['local_id'])
                new_match.add_teams(data['visitor_id'], data['local_id'])
            try:  # save new_match to db
                new_match.save_to_db()
                return new_match.json(), HTTPStatus.CREATED
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'an error occurred while inserting the match'}, HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='admin')
    def delete(self, id):
        with lock.lock:
            match = MatchesModel.get_by_id(id)

            # delete the match if it exists
            if match is None:
                return {'message': "match with id [{}] doesn't exists".format(id)}, HTTPStatus.NOT_FOUND
            try:
                match.delete_from_db()
                return {'message': "match with id [{}] has been deleted".format(id)}, HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()   # rollback in case something went wrong
                return {'message': 'error while deleting match with id [{}]'.format(id)}, HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='admin')
    def put(self, id):
        with lock.lock:
            # get the arguments
            data = self.get_data()
            new_match = MatchesModel.get_by_id(id)

            if new_match is None:  # add match if it doesn't exist
                return self.post(id)
            try:  # update match if it exsits
                new_match.date = dateutil.parser.parse(data['date'])
                new_match.price = data['price']
                if data['local_id']:
                    new_match.local_id = data['local_id']
                if data['visitor_id']:
                    new_match.visitor_id = data['visitor_id']
                if data['competition_id']:
                    new_match.competition_id = data['competition_id']
                db.session.commit()
                return new_match.json(), HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while deleting match with id [{}]'.format(id)}, HTTPStatus.INTERNAL_SERVER_ERROR

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        parser.add_argument('date', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('price', type=float, required=True, help="This field cannot be left blank")
        parser.add_argument('total_available_tickets', type=int, required=True, help="This field cannot be left blank")
        parser.add_argument('local_id', type=int, required=False)
        parser.add_argument('visitor_id', type=int, required=False)

        return parser.parse_args()


class MatchesList(Resource):

    # return all matches
    def get(self):
        matches = MatchesModel.get_all()
        return {"Matches List": [x.json() for x in matches]}, HTTPStatus.OK if matches else HTTPStatus.NOT_FOUND


class MatchTeamsList(Resource):

    # return all teams of match
    def get(self, id):
        match = MatchesModel.get_by_id(id)
        # return match if it exists
        if match is None:
            return {'message': 'match with [{}] does not exist'.format(id)}, HTTPStatus.NOT_FOUND
        return {'match': match.get_teams()}, HTTPStatus.OK
