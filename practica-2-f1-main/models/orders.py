from db import db


class OrdersModel(db.Model):
    __tablename__ = 'orders'

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(30), db.ForeignKey('accounts.username'), nullable=False)
    match_id = db.Column(db.Integer, nullable=False)
    tickets_bought = db.Column(db.Integer, nullable=False)

    def __init__(self, match_id, tickets_bought):
        self.match_id = match_id
        self.tickets_bought = tickets_bought

    def json(self):
        return {
            'id': self.id,
            'username': self.username,
            'match_id': self.match_id,
            'tickets_bought': self.tickets_bought
        }

    @classmethod
    def get_all(cls):  # returns all orders
        return cls.query.all()

    @classmethod
    def get_by_username(cls, username):  # returns orders by username
        return cls.query.filter_by(username=username).all()
