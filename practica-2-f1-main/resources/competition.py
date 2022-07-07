from flask_restful import Resource, reqparse
from http import HTTPStatus
import dateutil.parser
from sqlalchemy import exc
from lock import lock

from db import db
from models.competitions import CompetitionsModel
from models.matches import MatchesModel
from models.teams import TeamsModel
from models.accounts import g, auth


class Competition(Resource):

    def get(self, id):
        competition = CompetitionsModel.get_by_id(id)
        # return competition if it exists
        if competition is None:  # return error message if competition doesn't exist
            return {'message': 'The competition with [{}] does not exist'.format(id)}, HTTPStatus.NOT_FOUND
        return {'competition': competition.json()}, HTTPStatus.OK

    @auth.login_required(role='admin')
    def post(self, id=None):
        with lock.lock:
            # get the arguments
            data = self.get_data()

            # if id is not None
            if id is not None:
                # return error if id exists
                if CompetitionsModel.get_by_id(id) is not None:
                    return {'message': "competition with id [{}] already exists".format(id)}, HTTPStatus.CONFLICT

            # add new competition to db
            new_competition = CompetitionsModel(data['name'], data['category'], data['sport'])
            new_competition.id = id
            if data['match_id']:
                match = MatchesModel.get_by_id(data['match_id'])
                if match:
                    new_competition.matches.append(match)
                    new_competition.teams.append(match.visitor)
                    new_competition.teams.append(match.local)
            try:  # save new_competition to db
                new_competition.save_to_db()
                return new_competition.json(), HTTPStatus.CREATED
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'an error occurred while inserting the competition'}, \
                    HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='admin')
    def delete(self, id):
        with lock.lock:
            competition = CompetitionsModel.get_by_id(id)

            # delete the competition if it exists
            if competition is None:
                return {'message': "competition with id [{}] doesn't exists".format(id)}, HTTPStatus.NOT_FOUND
            try:
                competition.delete_from_db()
                return {'message': "competition with id [{}] has been deleted".format(id)}, HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while deleting competition with id [{}]'.format(id)}, \
                    HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='admin')
    def put(self, id):
        with lock.lock:
            # get the arguments
            data = self.get_data()
            new_competition = CompetitionsModel.get_by_id(id)

            if new_competition is None:  # add competition if it doesn't exist
                return self.post(id)

            try:  # update competition if it exsits
                new_competition.name = data['name']
                new_competition.country = data['category']
                new_competition.sport = data['sport']
                match = MatchesModel.get_by_id(id)
                if match:
                    new_competition.matches.append(match)
                    new_competition.teams.append(match.visitor)
                    new_competition.teams.append(match.local)
                db.session.commit()
                return new_competition.json(), HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()
                return {'message': 'error while deleting competition with id [{}]'.format(id)},\
                    HTTPStatus.INTERNAL_SERVER_ERROR

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('category', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('sport', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('matches_id', type=int, required=False)

        return parser.parse_args()


class CompetitionsList(Resource):

    # return all competitions
    def get(self):
        competitions = CompetitionsModel.get_all()
        return {"Competitions List": [x.json() for x in competitions]},\
            HTTPStatus.OK if competitions else HTTPStatus.NOT_FOUND


class CompetitionTeamsList(Resource):

    # return all teams in competition
    def get(self, id):
        competition = CompetitionsModel.get_by_id(id)
        if competition is None:  # return error message if competition doesn't exist
            return {'message': 'The competition with [{}] does not exist'.format(id)}, HTTPStatus.NOT_FOUND
        teams_list = competition.get_all_teams()
        return {"Orders Teams List": teams_list}, HTTPStatus.OK if teams_list else HTTPStatus.NOT_FOUND


class CompetitionMatch(Resource):

    # return all matches in competition
    def get(self, competition_id, match_id):
        competition = CompetitionsModel.get_by_id(competition_id)
        if competition is None:  # return error message if competition doesn't exist
            return {'message': 'The competition with [{}] does not exist'.format(competition_id)}, HTTPStatus.NOT_FOUND
        match = competition.get_match(match_id)
        return {"Orders Match": match.json()}, HTTPStatus.OK if match else HTTPStatus.NOT_FOUND

    @auth.login_required(role='admin')
    def post(self, competition_id, match_id=None):
        with lock.lock:
            competition = CompetitionsModel.get_by_id(competition_id)
            if competition is None:  # return error message if competition doesn't exist
                return {'message': 'The competition with [{}] does not exist'.format(competition_id)}, HTTPStatus.NOT_FOUND

            # if match_id is none, read data and create match
            if match_id is None:
                data = self.get_data()
                # add new match to db
                new_match = MatchesModel(dateutil.parser.parse(data['date']), data['price'])
                new_match.id = match_id
                # add teams to match if visitor and local aren't null and teams to competition
                if data['visitor_id'] and data['local_id']:
                    local = TeamsModel.get_by_id(data['local_id'])
                    visitor = TeamsModel.get_by_id(data['visitor_id'])
                    if visitor and local:
                        new_match.add_teams(visitor, local)
                        competition.add_teams(local, visitor)
                    else:
                        return {'message': 'One or both of the teams does not exist'}, HTTPStatus.NOT_FOUND
                competition.matches.append(new_match)  # add new match to competition
            else:
                # if match_id is not none, check if match exists
                new_match = MatchesModel.get_by_id(match_id)
                if new_match is not None and new_match not in competition.matches:
                    competition.matches.append(new_match)  # add new match to competition
                # if match is already in competition or the match doesn't exist, return error message
                elif new_match and new_match in competition.matches:
                    return {'message': 'The competition with id [{}] already has match with id[{}] '.format(
                        competition_id, match_id)}, HTTPStatus.NOT_FOUND
                elif new_match is None:
                    return {'message': "The match with id [{}] does not exist".format(match_id)}, HTTPStatus.NOT_FOUND

            try:  # save competition
                competition.save_to_db()
                return {'competition': competition.json()}, HTTPStatus.CREATED
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while deleting match from competition with id [{}]'.format(id)}, \
                    HTTPStatus.INTERNAL_SERVER_ERROR

    @auth.login_required(role='admin')
    def delete(self, competition_id, match_id):
        with lock.lock:
            competition = CompetitionsModel.get_by_id(competition_id)
            if competition is None:  # return error message if competition doesn't exist
                return {'message': "competition with id [{}] doesn't exists".format(competition_id)}, HTTPStatus.NOT_FOUND

            # check if competition contains match
            match = MatchesModel.get_by_id(match_id)
            # return error message if competition doesn't contain match to be deleted
            if match is None or match not in competition.matches:
                return {'message': "competition with id [{}] does not have match with id [{}]".format(
                    competition_id, match_id)}, HTTPStatus.NOT_FOUND
            # delete match
            try:  # save competition
                competition.matches.remove(match)
                competition.save_to_db()
                return {'competition': competition.json()}, HTTPStatus.OK
            except exc.SQLAlchemyError:
                db.session.rollback()  # rollback in case something went wrong
                return {'message': 'error while deleting match from competition with id [{}]'.format(id)},\
                    HTTPStatus.INTERNAL_SERVER_ERROR

    def get_data(self):
        parser = reqparse.RequestParser()  # create parameters parser from request

        # define all input parameters need and its type
        parser.add_argument('date', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('price', type=float, required=True, help="This field cannot be left blank")
        parser.add_argument('local_id', type=int, required=False)
        parser.add_argument('visitor_id', type=int, required=False)

        return parser.parse_args()
