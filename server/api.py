import base64
import hashlib
import json
from datetime import datetime

from flask import Blueprint, jsonify, request, Response
from flask_restplus import Api, Resource
from webargs import fields
from webargs.flaskparser import parser

from achat_logging import logger
from database import db
from decorators import require_api_key, require_user_auth_token
from models import UserAccount, AuthTokens, UserProfile, ShoppingList, ShoppingItem, ListShareMatrix
from utilities import get_client_ip, generate_hash_key, random_with_n_digits

api_blueprint = Blueprint('api', __name__, url_prefix='/api')
api = Api(api_blueprint, version='0.1', title='[achat] shopping list application api',
          description='Shopping list for the masses...')


@api.route('/echo')
class Echo(Resource):
    def get(self):
        logger.info("Echo called with some ip")
        return jsonify(status=202, message='Everything ok.', caller_ip=get_client_ip())

    def post(self):
        return self.get()


@api.route('/echo/with_api_key')
class EchoWithApi(Resource):
    @require_api_key
    def get(self):
        logger.info("Echo called with some ip")
        return jsonify(status=202, message='Everything ok.', caller_ip=get_client_ip())

    def post(self):
        return self.get()


@api.route('/user/register')
class UserRegister(Resource):
    @require_api_key
    def post(self):

        register_args = {
            'userName': fields.Str(required=True),
            'password': fields.Str(required=True),
            'firstName': fields.Str(required=False),
            'lastName': fields.Str(required=False)
        }

        request_args = parser.parse(register_args, request)

        userEmail = request_args['userName']
        password = request_args['password']
        firstName = request_args['firstName']
        lastName = request_args['lastName']

        # check if the users already exists
        user = UserAccount.query.filter_by(user_email=userEmail).first()
        if user:
            logger.error("User with email {} already exists. Registration failed.".format(userEmail))
            return jsonify(authToken="",
                           isRegistrationSuccessful=False,
                           registrationErrorMessage="User with email {} already exists. Registration failed.".format(
                               userEmail))
        else:
            authToken = generate_hash_key()

            new_user_guid = generate_hash_key()
            user_account = UserAccount(userEmail, password, new_user_guid, False, datetime.now())
            user_profile = UserProfile(new_user_guid, firstName, lastName)
            user_auth_token = AuthTokens(new_user_guid, authToken, -1)

            db.session.add(user_account)
            db.session.add(user_profile)
            db.session.add(user_auth_token)

            db.session.commit()

            logger.info("New user registered with email {}.".format(userEmail))

            return jsonify(authToken=authToken,
                           isRegistrationSuccessful=True,
                           registrationErrorMessage="")


@api.route('/user/login')
class UserLogin(Resource):
    @require_api_key
    def post(self):
        login_args = {
            'userEmail': fields.Str(required=True),
            'password': fields.Str(required=True)
        }

        request_args = parser.parse(login_args, request)

        userEmail = request_args['userEmail']
        password = request_args['password']

        if userEmail is None or password is None:
            logger.error("User name or password is empty. Login cannot continue.")
            return jsonify(isLoginSuccessful=False, access_token="")

        user = UserAccount.query.filter_by(user_email=userEmail).first()
        if user:
            # generate the password hash and compare to the one in the database
            hash_algo = hashlib.sha512()
            hash_algo.update(base64.b64encode(password) + user.password_salt)
            password_digest = hash_algo.hexdigest()

            if password_digest == user.password_digest:
                auth = AuthTokens.query.filter_by(user_guid=user.user_guid).first()

                if auth:
                    user.last_login = datetime.now()
                    db.session.add(user)
                    db.session.commit()
                    logger.info('User {} logged in.'.format(userEmail))
                    return jsonify(isLoginSuccessful=True, access_token=auth.auth_key)
                else:
                    logger.error("A user with this email exists but does not have auth token. Login cannot proceed.")
                    return jsonify(isLoginSuccessful=False, access_token="")
            else:
                logger.error("Incorrect password. Login failed.")
                return jsonify(isLoginSuccessful=False, access_token="")
        else:
            logger.error("No user found by the user email {}. Login failed.".format(userEmail))
            return jsonify(isLoginSuccessful=False, access_token="")


@api.route('/user/profile')
class GetUserProfile(Resource):
    @require_api_key
    @require_user_auth_token
    def get(self):
        req_args_dict = {'user_auth_token': fields.Str(required=True)}
        request_args = parser.parse(req_args_dict, request)
        user_auth_token = request_args['user_auth_token']
        auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token)

        if auth_record:
            user_profile = UserProfile.query.filter_by(user_guid=auth_record.user_guid).first()
            user = UserAccount.query.filter_by(user_guid=auth_record.user_guid).first()
            if user_profile:
                return jsonify(isProfileFound=True,
                               userName=user.user_email,
                               firstName=user_profile.first_name,
                               lastName=user_profile.last_name)
            else:
                return jsonify(isProfileFound=False,
                               userName="",
                               firstName="",
                               lastName="")
        else:
            return jsonify(isProfileFound=False,
                           userName="",
                           firstName="",
                           lastName="")


@api.route('/shoppinglist/list')
class GetAllList(Resource):
    @require_api_key
    @require_user_auth_token
    def get(self):
        try:
            user_args = {'user_auth_token': fields.Str()}
            request_args = parser.parse(user_args, request)
            user_auth_token = request_args['user_auth_token']
            user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
            lists = ShoppingList.query.filter_by(user_guid=user_auth_record.user_guid).all()
            return_list = []
            if lists:
                for l in lists:
                    return_list.append(l.serialize())
                return Response(json.dumps(return_list), mimetype='application/json')
            else:
                return None
        except Exception as e:
            logger.error(e)


@api.route('/shoppinglist/add')
class AddListForUser(Resource):
    @require_api_key
    @require_user_auth_token
    def post(self):
        try:
            list_add_args = {
                'user_auth_token': fields.Str(required=True),
                'listName': fields.Str(required=True),
                'description': fields.Str(required=False)
            }

            request_args = parser.parse(list_add_args, request)

            user_auth_token = request_args['user_auth_token']
            listName = request_args['listName']
            description = request_args['description']

            user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
            list_guid = generate_hash_key()
            new_list = ShoppingList(user_auth_record.user_guid, list_guid, listName, description)
            db.session.add(new_list)
            db.session.commit()

            return jsonify(isListAddSuccessful=True,
                           listAddErrorMessage="")

        except Exception as e:
            logger.error(str(e))
            return jsonify(isListAddSuccessful=False,
                           listAddErrorMessage="An exception occurred while adding list to database.")


@api.route('/shoppinglist/items/add')
class AddListForUser(Resource):
    @require_api_key
    @require_user_auth_token
    def post(self):
        try:
            list_add_args = {
                'user_auth_token': fields.Str(required=True),
                'list_guid': fields.Str(required=True),
                'item_name': fields.Str(required=True),
                'item_description': fields.Str(required=False),
                'item_quantity': fields.Int(missing=1)
            }

            request_args = parser.parse(list_add_args, request)

            user_auth_token = request_args['user_auth_token']
            list_guid = request_args['list_guid']
            item_name = request_args['item_name']
            item_desc = request_args['item_description']
            item_quantity = request_args['item_quantity']

            user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
            shopping_list = ShoppingList.query.filter_by(list_guid=list_guid,
                                                         user_guid=user_auth_record.user_guid).first()
            if shopping_list:
                new_item = ShoppingItem(list_guid, item_name, item_desc, "", item_quantity, "")
                db.session.add(new_item)
                db.session.commit()

            return jsonify(isItemAddSuccessful=True,
                           itemAddErrorMessage="")

        except Exception as e:
            logger.error(e)
            return jsonify(isItemAddSuccessful=True,
                           itemAddErrorMessage=e)


@api.route('/shoppinglist/items/list')
class GetAllList(Resource):
    @require_api_key
    @require_user_auth_token
    def get(self):
        try:
            request_args_dict = {
                'user_auth_token': fields.Str(required=True),
                'list_guid': fields.Str(required=True)
            }
            request_args = parser.parse(request_args_dict, request)
            user_auth_token = request_args['user_auth_token']
            list_guid = request_args['list_guid']

            user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
            shopping_list = ShoppingList.query.filter_by(user_guid=user_auth_record.user_guid,
                                                         list_guid=list_guid).first()
            return_list_items = []
            if shopping_list:
                list_items = ShoppingItem.query.filter_by(list_guid=list_guid).all()

                for item in list_items:
                    return_list_items.append(item.serialize())
                return Response(json.dumps(return_list_items), mimetype='application/json')
            else:
                return Response(json.dumps(return_list_items), mimetype='application/json')
        except Exception as e:
            logger.error(e)


@api.route('/shoppingitems/list')
class GetAllList(Resource):
    @require_api_key
    @require_user_auth_token
    def get(self):
        try:
            request_args_dict = {
                'user_auth_token': fields.Str(required=True)
            }
            request_args = parser.parse(request_args_dict, request)
            user_auth_token = request_args['user_auth_token']

            user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
            shopping_lists = ShoppingList.query.filter_by(user_guid=user_auth_record.user_guid).all()
            return_list_items = []
            if shopping_lists:
                for s in shopping_lists:
                    list_items = ShoppingItem.query.filter_by(list_guid=s.list_guid).all()
                    for item in list_items:
                        return_list_items.append(item.serialize())
                return Response(json.dumps(return_list_items), mimetype='application/json')
            else:
                return Response(json.dumps(return_list_items), mimetype='application/json')
        except Exception as e:
            logger.error(e)


@api.route('/share/initShare')
class CreateShareCode(Resource):
    @require_api_key
    @require_api_key
    def post(self):

        request_args_dict = {
            'user_auth_token': fields.Str(required=True),
            'list_guid': fields.Str(required=True)
        }

        request_args = parser.parse(request_args_dict, request)
        user_auth_token = request_args['user_auth_token']
        list_guid = request_args['list_guid']

        user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
        shopping_list = ShoppingList.query.filter_by(user_guid=user_auth_record.user_guid,
                                                     list_guid=list_guid).first()
        if shopping_list:
            # take this occasion to delete old share inits that are older than 24 hours
            current_time = datetime.datetime.utcnow()
            twenty_four_hours_ago = current_time - datetime.timedelta(hours=24)
            old_session_deleted_count = db.session.query(ListShareMatrix) \
                .filter(date_created < twenty_four_hours_ago, shared_to_guid="") \
                .delete()

            if old_session_deleted_count > 0:
                logger.info("{} old share inits have been deleted.".format(old_session_deleted_count))

            share_code = random_with_n_digits(8)

            share = ListShareMatrix(share_code, user_auth_token, shopping_list)
            db.session.add(share)
            db.session.commit()

            return jsonify(share_code=share.share_code,
                           is_share_successful=True,
                           share_init_error_message="")
        else:
            return jsonify(share_code="",
                           is_share_successful=False,
                           share_init_error_message="The shopping list was not found on the server.")


@api.route('/share/completeShare')
class CreateShareCode(Resource):
    @require_api_key
    @require_api_key
    def post(self):
        request_args_dict = {
            'user_auth_token': fields.Str(required=True),
            'share_code': fields.Str(required=True)
        }
        request_args = parser.parse(request_args_dict, request)
        user_auth_token = request_args['user_auth_token']
        share_code = request_args['share_code']

        user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
        share = ListShareMatrix.query.filter_by(share_code=share_code).first()
        if share and user_auth_record:
            # take this occasion to delete old share inits that are older than 24 hours
            # clean old shares that have not been completed.
            current_time = datetime.datetime.utcnow()
            twenty_four_hours_ago = current_time - datetime.timedelta(hours=24)
            old_session_deleted_count = db.session.query(ListShareMatrix) \
                .filter(date_created < twenty_four_hours_ago, shared_to_guid="") \
                .delete()

            if old_session_deleted_count > 0:
                logger.info("{} old share inits have been deleted.".format(old_session_deleted_count))

            ListShareMatrix \
                .query \
                .where(share_code=share_code) \
                .update(shared_to_guid=user_auth_token)
            ListShareMatrix \
                .query \
                .where(shared_to_guid=user_auth_token) \
                .update(share_code="00000000")

            return jsonify(share_code=share.share_code,
                           is_share_successful=True,
                           share_init_error_message="")
        else:
            return jsonify(share_code="",
                           is_share_successful=False,
                           share_init_error_message="The shopping list was not found on the server.")


@api.route('/share/remove')
class CreateShareCode(Resource):
    @require_api_key
    @require_api_key
    def post(self):
        request_args_dict = {
            'user_auth_token': fields.Str(required=True),
            'shared_to_guid': fields.Str(required=True),
        }
        request_args = parser.parse(request_args_dict, request)
        user_auth_token = request_args['user_auth_token']
        shared_to_guid = request_args['shared_to_guid']

        user_auth_record = AuthTokens.query.filter_by(auth_key=user_auth_token).first()
        share = ListShareMatrix.query.filter_by(shared_by_guid=user_auth_token,
                                                shared_to_guid=shared_to_guid).first()
        if share and user_auth_record:
            # take this occasion to delete old share inits that are older than 24 hours
            # clean old shares that have not been completed.
            current_time = datetime.datetime.utcnow()
            twenty_four_hours_ago = current_time - datetime.timedelta(hours=24)
            old_session_deleted_count = db.session.query(ListShareMatrix) \
                .filter(date_created < twenty_four_hours_ago, shared_to_guid="") \
                .delete()

            if old_session_deleted_count > 0:
                logger.info("{} old share inits have been deleted.".format(old_session_deleted_count))

            ListShareMatrix.query.where(shared_by_guid=user_auth_token, shared_to_guid=user_auth_token).delete()

            return jsonify(share_code=share.share_code,
                           is_share_successful=True,
                           share_init_error_message="")
        else:
            return jsonify(share_code="",
                           is_share_successful=False,
                           share_init_error_message="The shopping list was not found on the server.")


def file_ext(filename):
    """
    Return the extension of a file name with extension.
    :param filename: the file name to search extension from.
    :return: a file name extension without the .
    """
    if '.' not in filename:
        return ''
    else:
        return filename.rsplit('.', 1)[1]
