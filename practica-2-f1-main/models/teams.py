from db import db


class TeamsModel(db.Model):
    __tablename__ = 'teams'  # This is table name
    __table_args__ = (db.UniqueConstraint('name', 'country'),)  # each team is unique

    # id is the primary key of this table and an Integer
    id = db.Column(db.Integer, primary_key=True)
    # strings with a max length of 30 characters
    name = db.Column(db.String(30), unique=True, nullable=False)
    country = db.Column(db.String(30), nullable=False)

    def __init__(self, name, country):
        self.name = name
        self.country = country

    def json(self):
        return {
            'id': self.id,
            'name': self.name,
            'country': self.country
        }

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()

    @classmethod
    def get_all(cls):  # returns all teams
        return cls.query.all()

    @classmethod
    def get_by_id(cls, id):  # returns team by id
        return cls.query.get(id)
