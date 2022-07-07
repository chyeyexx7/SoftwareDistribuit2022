from db import db
from sqlalchemy import or_


class MatchesModel(db.Model):
    __tablename__ = 'match'  # This is table name
    __table_args__ = (db.UniqueConstraint('local_id', 'visitor_id', 'date'),)

    # table columns
    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.DateTime, nullable=False)
    price = db.Column(db.Float(8), nullable=False)
    total_available_tickets = db.Column(db.Integer, default=200)

    # foreign keys
    local_id = db.Column(db.Integer, db.ForeignKey("teams.id"))
    visitor_id = db.Column(db.Integer, db.ForeignKey("teams.id"))
    competition_id = db.Column(db.Integer, db.ForeignKey("competition.id"))

    # relationships
    # teams 1-* matches
    local = db.relationship("TeamsModel", foreign_keys=[local_id])
    visitor = db.relationship("TeamsModel", foreign_keys=[visitor_id])
    # competitions 1-* matches
    competition = db.relationship("CompetitionsModel", back_populates='matches')

    def __init__(self, date, price, total_available_tickets):
        self.date = date
        self.price = price
        self.total_available_tickets = total_available_tickets

    def json(self):
        return {
            'id': self.id,
            'local': self.local.json() if self.local else None,
            'visitor': self.visitor.json() if self.visitor else None,
            'competition': self.competition.name if self.competition else None,
            'competition_id': self.competition_id,
            'date': self.date.isoformat(),
            'price': self.price,
            'total_available_tickets': self.total_available_tickets
        }

    def add_teams(self, local, visitor):
        self.local_id = local
        self.visitor_id = visitor

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()

    def get_teams(self):  # returns teams of match
        return [self.local.json(), self.visitor.json()]

    @classmethod
    def get_all(cls):  # returns all matches
        return cls.query.all()

    @classmethod
    def get_by_id(cls, id):  # returns match by id
        return cls.query.get(id)

    @classmethod
    def get_matches(cls, id):  # returns all matches played by team
        return cls.query.filter(or_(cls.local_id == id, cls.visitor_id == id))
