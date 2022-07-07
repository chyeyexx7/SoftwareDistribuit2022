from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

# import models here
from models.teams import TeamsModel
from models.matches import MatchesModel
from models.competitions import CompetitionsModel
import resources.data as data

import random as rand

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)
db.init_app(app)

teams = []
matches = []
competitions = []

for competition in data.competitions:
    competitionModel = CompetitionsModel(name=competition["name"], category=competition["category"],
                                         sport=competition["sport"])
    competitions.append(competitionModel)

for match in data.matches:
    matchModel = MatchesModel(date=datetime.strptime(match['date'], "%Y-%m-%d"), price=match['price'],
                              total_available_tickets=match['total_available_tickets'])
    matches.append(matchModel)

for team in data.teams:
    teamModel = TeamsModel(name=team['name'], country=team['country'])
    teams.append(teamModel)

# Repeat the process above process for teams and matches!

# Relationships
for match in matches:
    # Choose randomly two different teams from the teams list
    local = rand.choice(teams)
    visitor = rand.choice(teams)
    while visitor.name == local.name:
        visitor = rand.choice(teams)

    # Assign local and visitor for the current match
    match.local = local
    match.visitor = visitor

    # Choose a random competition
    random_competition = rand.choice(competitions)
    # Assign the competition to the match
    match.competition = random_competition

    # Append the match to the competition
    random_competition.matches.append(match)
    # Append local and visitor to the competition list of teams
    random_competition.teams.append(match.local)
    random_competition.teams.append(match.visitor)

db.session.add_all(teams)
db.session.add_all(matches)
db.session.add_all(competitions)
db.session.commit()
