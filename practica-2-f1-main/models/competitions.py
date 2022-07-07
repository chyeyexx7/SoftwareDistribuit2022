from db import db

categories_list = ("Senior", "Junior")
sports_list = ("Volleyball", "Football", "Basketball", "Futsal")

teams_in_competitions = db.Table("teams_in_competitions",
                                 db.Column("id", db.Integer, primary_key=True),
                                 db.Column("team_id", db.Integer, db.ForeignKey("teams.id")),
                                 db.Column("competition_id", db.Integer, db.ForeignKey("competition.id")))


class CompetitionsModel(db.Model):
    __tablename__ = 'competition'  # This is table name
    __table_args__ = (db.UniqueConstraint('name', 'category', 'sport'),)  # each competition is unique

    # table columns
    id = db.Column(db.Integer, primary_key=True)  # primary key
    name = db.Column(db.String(30), unique=True, nullable=False)  # string with a max length of 30
    # validate_string=True raises an error if the value is not inside enum
    category = db.Column(db.Enum(*categories_list, name='categories_types', validate_strings=True), nullable=False)
    sport = db.Column(db.Enum(*sports_list, name='sports_types', validate_strings=True), nullable=False)

    # relationships
    # team *-* competition
    teams = db.relationship("TeamsModel", secondary=teams_in_competitions, backref=db.backref("competition"))
    # match *-1 competition
    matches = db.relationship("MatchesModel", back_populates='competition')  # lazy=dynamic to query

    def __init__(self, name, category, sport):
        self.name = name
        self.category = category
        self.sport = sport

    def json(self):
        return {
            'id': self.id,
            'name': self.name,
            'category': self.category,
            'sport': self.sport,
            'teams': self.get_all_teams(),
            'matches': [x.json() for x in self.matches]
        }

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()

    def get_all_teams(self):  # returns all teams of this competition
        return [x.json() for x in self.teams]

    def get_match(self, id):  # returns match with id inside competition
        return self.query.filter(CompetitionsModel.matches.any(id=id)).first()

    def add_teams(self, local, visitor):
        self.teams.append(visitor)
        self.teams.append(local)

    @classmethod
    def get_by_id(cls, id):  # returns competition by id
        return cls.query.get(id)

    @classmethod
    def get_all(cls):
        return cls.query.all()
