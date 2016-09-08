import os
from datetime import datetime

from flask import Flask
from jinja2 import FileSystemLoader, ChoiceLoader, PackageLoader
from models import UserAccount, APIAuthRecord
from database import db

from achat_logging import logger
from admin_web import admin_blueprint, login_manager
from api import api_blueprint

from utilities import generate_hash_key

DB_DIRECTORY = os.path.dirname(os.path.abspath(__file__)) + '/database'
DB_FILE_PATH = DB_DIRECTORY + '/achat_api.db'


def create_app():
    flask_app = Flask(__name__)

    # set configuration parameters...
    flask_app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + DB_FILE_PATH
    flask_app.config['SECRET_KEY'] = '{87735866-EF91-4051-9D42-EF63C2C9E9F9}'
    flask_app.config['WTF_CSRF_KEY'] = '{87735866-EF91-4051-9D42-EF63C2C9E9F9}'

    # configure jinja template folders...
    flask_app.jinja_loader = ChoiceLoader([
        FileSystemLoader(os.path.join(os.getcwd(), 'templates')),
        PackageLoader('admin_web'),
    ])

    # initialize app dependent extensions.
    db.init_app(flask_app)
    login_manager.init_app(flask_app)

    # register view blueprints.
    flask_app.register_blueprint(api_blueprint)
    flask_app.register_blueprint(admin_blueprint)

    return flask_app


def initialize_database(app):
    with app.app_context():
        logger.info('The database is being initialized for the first time.')
        db.create_all()

        logger.info(' A new api key is being created for testing. ')
        test_api_key = APIAuthRecord('L37Iv0xi5JcF349DzEyj49MIFofYUcDAlOLp8HbtiIQ', '0.0.0.0', 'test api key')
        db.session.add(test_api_key)
        db.session.commit()


def initialize_application(app):
    # create the directory for the database if it does not exist. This might happen if the app is being run for the
    # first time.
    if not os.path.exists(DB_DIRECTORY):
        logger.info('Database directory does not exist. Creating...')
        os.makedirs(DB_DIRECTORY)

    # create and initialize the database if it does not exist.
    if not os.path.isfile(app.config['SQLALCHEMY_DATABASE_URI']):
        logger.info("Data base file is missing {}".format(app.config['SQLALCHEMY_DATABASE_URI']))
        initialize_database(app)

    # check if there are any uses in the system. If there are none create at least one admin user to be able to access
    # the system.
    with app.app_context():
        users = UserAccount.query.all()
        if not users:
            logger.info('There are no users in the database. Creating an admin user account with default settings.')
            new_user_guid = generate_hash_key()
            admin_user = UserAccount('it@sensimed.ch', 'Up@8dHgd', new_user_guid, False, datetime.now())
            db.session.add(admin_user)
            db.session.commit()

application = create_app()
logger.info("Application (re)started.")

