import time
from db import db
from passlib.apps import custom_app_context as pwd_context
from jwt import encode, decode, ExpiredSignatureError, InvalidSignatureError
from flask_httpauth import HTTPBasicAuth
from flask import g, current_app

auth = HTTPBasicAuth()


class AccountsModel(db.Model):
    __tablename__ = 'accounts'

    username = db.Column(db.String(30), primary_key=True, unique=True, nullable=False)
    password = db.Column(db.String(), nullable=False)
    # 0 not admin/ 1 is admin
    is_admin = db.Column(db.Integer, nullable=False)
    available_money = db.Column(db.Integer)
    orders = db.relationship('OrdersModel', backref='orders', lazy=True)

    def __init__(self, username, available_money=200, is_admin=0):
        self.username = username
        self.available_money = available_money
        self.is_admin = is_admin

    def json(self):
        return {'username': self.username,
                'is_admin': self.is_admin,
                'available_money': self.available_money,
                }

    def generate_auth_token(self, expiration=600):
        return encode(
            {"username": self.username, "exp": int(time.time()) + expiration},
            current_app.secret_key,
            algorithm="HS256"
        )

    @classmethod
    def verify_auth_token(cls, token):
        try:
            data = decode(token, current_app.secret_key, algorithms=["HS256"])
        except ExpiredSignatureError:
            return None  # expired token
        except InvalidSignatureError:
            return None  # invalid token

        user = cls.query.filter_by(username=data['username']).first()

        return user

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()

    @classmethod
    def get_all(cls):  # returns all accounts
        return cls.query.all()

    @classmethod
    def get_by_username(cls, username):
        return cls.query.get(username)

    def hash_password(self, password):
        self.password = pwd_context.encrypt(password)

    def verify_password_(self, password):
        return pwd_context.verify(password, self.password)

    @auth.verify_password
    def verify_password(token, password):
        g.user = AccountsModel.verify_auth_token(token)
        return g.user

    @auth.get_user_roles
    def get_user_roles(user):
        return ['admin'] if user.is_admin else ['user']