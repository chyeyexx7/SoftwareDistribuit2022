from lock import lock
from flask_restful import Resource, reqparse
from http import HTTPStatus
from sqlalchemy import exc

from db import db
from models.teams import TeamsModel
from models.matches import MatchesModel
from models.accounts import auth


class Team(Resource):

    def get(self, id):
        team = TeamsModel.get_by_id(id)
        # return team if it exists
        if team is None:
            return {'message': 'team with [{}] does not exist'.format(id)}, HTTPStatus.NOT_FOUND
        return {'team': team.json()}, HTTPStatus.OK

    @auth.login_required(role='admin')
    def post(self, id=None):
        with lock.lock:
            # get the arguments
            data = self.get_data()

            # if id is not None
            if id is not None:
                # return error if id exists
                if TeamsModel.get_by_id(id) is not None:
                    return {'message': "team with id [{}] already exists".format(id)}, HTTPStatus.CONFLICT

            # add new team to db
            new_team = TeamsModel(data['name'], data['country'])
            new_team.id = id
            try:  # save new_team to db
                new_team.save_to_db()
                return new_team.json(), HTTPStatus.CREATED
            except exc.SQLAlchemyError:
                db.session.rollback()   # rollback in case something went wrong
                return {'message': 'an error occurred while inserting the team'}, HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='admin')
    def delete(self, id):
        with lock.lock:
            team = TeamsModel.get_by_id(id)

            # delete the team if it exists
            if team is None:
                return {'message': "team with id [{}] doesn't exists".format(id)}, HTTPStatus.NOT_FOUND
            try:
                team.delete_from_db()
                return {'message': "team with id [{}] has been deleted".format(id)}, HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while deleting team with id [{}]'.format(id)}, HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='admin')
    def put(self, id):
        with lock.lock:
            # get the arguments
            data = self.get_data()
            new_team = TeamsModel.get_by_id(id)

            if new_team is None:  # add team if it doesn't exist
                return self.post(id)

            try:  # update competition if it exists
                new_team.name = data['name']
                new_team.country = data['country']
                db.session.commit()
                return new_team.json(), HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while deleting match with id [{}]'.format(id)}, HTTPStatus.INTERNAL_SERVER_ERROR

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('country', type=str, required=True, help="This field cannot be left blank")

        return parser.parse_args()


class TeamsList(Resource):

    # return all matches
    def get(self):
        teams = TeamsModel.get_all()
        return {"Teams List": [x.json() for x in teams]}, HTTPStatus.OK if teams else HTTPStatus.NOT_FOUND


class TeamMatchesList(Resource):

    # return all matches played by team
    def get(self, id):
        matches = MatchesModel.get_matches(id)
        if matches is None:
            return {'message': "the team with id [{}] doesn't have any matches".format(id)}, HTTPStatus.NOT_FOUND
        return {'Matches List': [x.json() for x in matches]}
