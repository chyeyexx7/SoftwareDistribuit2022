from flask import Flask
from flask_restful import Api
from flask_migrate import Migrate
from db import db
from flask_cors import CORS
from flask import render_template

from resources.team import Team, TeamsList, TeamMatchesList
from resources.competition import Competition, CompetitionsList, CompetitionMatch, CompetitionTeamsList
from resources.match import Match, MatchesList, MatchTeamsList
from resources.Orders import Orders, OrdersList
from resources.accounts import Accounts, AccountsList
from resources.login import Login
from models.teams import TeamsModel
from models.matches import MatchesModel
from models.competitions import CompetitionsModel

from decouple import config as config_decouple
from config import config

app = Flask(__name__)
environment = config['development']
if config_decouple('PRODUCTION', cast=bool, default=False):
    environment = config['production']

app.config.from_object(environment)

CORS(app, resources={r'/*': {'origins': '*'}})

migrate = Migrate(app, db)
db.init_app(app)
api = Api(app)

# teams
api.add_resource(Team, '/team/<int:id>', '/team')
api.add_resource(TeamsList, '/teams')
api.add_resource(TeamMatchesList, '/team/<int:id>/matches')
# competitions
api.add_resource(Competition, '/competition/<int:id>', '/competition')
api.add_resource(CompetitionsList, '/competitions')
api.add_resource(CompetitionTeamsList, '/competition/<int:id>/teams')
api.add_resource(CompetitionMatch, '/competition/<int:competition_id>/match',
                 "/competition/<int:competition_id>/match/<int:match_id>")
# matches
api.add_resource(Match, '/match/<int:id>', '/match')
api.add_resource(MatchesList, '/matches')
api.add_resource(MatchTeamsList, '/match/<int:id>/teams')

# orders
api.add_resource(Orders, '/order/<string:username>')
api.add_resource(OrdersList, '/orders', '/orders/<string:username>')
# accounts
api.add_resource(Accounts, '/account/<string:username>', '/account')
api.add_resource(AccountsList, '/accounts')
# login
api.add_resource(Login, '/login')


@app.route('/')
def render_vue():
    return render_template("index.html")


if __name__ == '__main__':
    app.run(port=5000, debug=True)
