import base64
import hashlib
from datetime import datetime

import utilities
from database import db


class BaseModel(db.Model):
    """
    a base model for other database tables to inherit
    """
    __abstract__ = True
    id = db.Column(db.Integer, primary_key=True)
    date_created = db.Column(db.DateTime, default=db.func.current_timestamp())
    date_modified = db.Column(db.DateTime, default=db.func.current_timestamp(), onupdate=db.func.current_timestamp())


class APIAuthRecord(BaseModel):
    """
    Model for API KEYs.
    """
    __tablename__ = 'api_keys'

    api_key = db.Column(db.String(120), unique=True)
    ip = db.Column(db.String(120), unique=False)
    description = db.Column(db.String(255), unique=False)

    def __init__(self, key, ip, description):
        self.api_key = key
        self.ip = ip
        self.description = description

    def serialize(self):
        return {
            'ip': self.ip,
            'api_key': self.api_key,
            'desc': self.description,
        }


class AuthTokens(BaseModel):
    """
    Model that represents the auth token that the API gives out every time a client
    authenticates with a valid user name and password.
    """
    __tablename__ = 'auth_tokens'

    user_guid = db.Column(db.String(120), unique=True)
    auth_key = db.Column(db.String(120), unique=True)
    valid_for = db.Column(db.Integer, default=-1)

    def __init__(self, user_guid, auth_key, valid_for=-1):
        self.user_guid = user_guid
        self.auth_key = auth_key
        self.valid_for = valid_for


class UserAccount(BaseModel):
    """
    Model representing a valid user account
    """
    __tablename__ = 'user_account'

    user_email = db.Column(db.String(120), unique=True)
    user_guid = db.Column(db.String(120), unique=True)
    password_salt = db.Column(db.String(120))
    password_digest = db.Column(db.String)
    account_status = db.Column(db.Integer, default=0)
    account_expires = db.Column(db.Boolean, default=False)
    valid_until = db.Column(db.DateTime)
    authenticated = db.Column(db.Boolean, default=False)
    last_login = db.Column(db.DateTime)

    def __init__(self, user_email, password, user_guid, account_expires, valid_until):
        self.user_email = user_email
        self.user_guid = user_guid
        self.password_salt = utilities.generate_hash_key()
        hash_algo = hashlib.sha512()
        hash_algo.update(base64.b64encode(password) + self.password_salt)
        self.password_digest = hash_algo.hexdigest()
        self.account_status = 0
        self.account_expires = account_expires
        if account_expires:
            self.valid_until = valid_until

    def serialize(self):
        return {
            'id': self.id,
            'user_email': self.user_email,
            'user_guid': self.user_guid,
            'account_status': self.account_status,
            'account_expires': self.account_expires,
            'valid_until': self.valid_until,
            'last_login': self.last_login
        }

    def is_active(self):
        return self.account_status == 1 or (self.account_expires and self.valid_until > datetime.now())

    def get_id(self):
        return self.user_email

    def is_authenticated(self):
        return self.authenticated

    def is_anonymous(self):
        return False

    def insert_new_user(self):
        db.session.add(self)
        db.session.commit()


class UserProfile(BaseModel):
    __tablename__ = 'user_profile'

    user_guid = db.Column(db.String(120), unique=True)
    first_name = db.Column(db.String(255))
    last_name = db.Column(db.String(255))

    def __init__(self, user_guid, first_name, last_name):
        self.user_guid = user_guid
        self.first_name = first_name
        self.last_name = last_name

    def serialize(self):
        return {
            'id': self.id,
            'user_guid': self.user_email,
            'first_name': self.first_name,
            'last_name': self.last_name
        }


class ListShareMatrix(BaseModel):
    __tablename__ = 'list_share_matrix'

    share_code = db.Column(db.String(24), default="00000000")
    shared_by_guid = db.Column(db.String(120))
    shared_to_guid = db.column(db.String(120))
    shared_list_guid = db.Column(db.String(120))

    def __init__(self, share_code, shared_by, shared_list):
        self.share_code = share_code
        self.shared_by_guid = shared_by
        self.shared_list_guid = shared_list

    def serialize(self):
        return {
            'id': self.id,
            'share_code': self.share_code,
            'shred_by_guid': self.shared_by_guid,
            'shared_to_guid': self.shared_to_guid,
            'shared_list_guid': self.shared_list_guid
        }


class ShoppingList(BaseModel):
    __tablename__ = 'shopping_list'

    user_guid = db.Column(db.String(120))
    list_guid = db.Column(db.String(120), unique=True)
    list_title = db.Column(db.String(255))
    list_description = db.Column(db.String(255))
    share_status = db.Column(db.Boolean, default=False)

    def __init__(self, user_guid, list_guid, list_title, list_desc):
        self.user_guid = user_guid
        self.list_guid = list_guid
        self.list_title = list_title
        self.list_description = list_desc
        self.share_status = False

    def serialize(self):
        return {
            'id': self.id,
            'user_guid': self.user_guid,
            'list_guid': self.list_guid,
            'list_title': self.list_title,
            'list_description': self.list_description,
            'share_status': self.share_status,
            'created_on': str(self.date_created)
        }


class ShoppingItem(BaseModel):
    __tablename__ = 'shopping_item'

    list_guid = db.Column(db.String(120))
    item_title = db.Column(db.String(255))
    item_description = db.Column(db.String(255))
    item_bar_code = db.Column(db.String(255))
    item_quantity = db.Column(db.Integer)
    item_group = db.Column(db.String(255))
    item_checked = db.Column(db.Boolean, default=False)

    def __init__(self, list_guid, item_title, item_desc, item_bar_code, item_quantity, item_group):
        self.list_guid = list_guid
        self.item_title = item_title
        self.item_description = item_desc
        self.item_bar_code = item_bar_code
        self.item_quantity = item_quantity
        self.item_group = item_group
        self.item_checked = False

    def serialize(self):
        return {
            'id': self.id,
            'list_guid': self.list_guid,
            'item_title': self.item_title,
            'item_description': self.item_description,
            'item_bar_code': self.item_bar_code,
            'item_quantity': self.item_quantity,
            'item_group': self.item_group,
            'item_created_on': str(self.date_created),
            'item_checked': self.item_checked,
            'item_updated_on': self.date_modified
        }
