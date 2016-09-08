from functools import wraps
from flask import request, abort

from achat_logging import logger
from utilities import get_client_ip
from database import get_apiauth_object_by_key, get_user_auth_object_by_keyid

from webargs import fields
from webargs.flaskparser import parser

api_key_name = 'api_key'
user_args = {'user_auth_token': fields.Str()}


def match_api_keys(key, ip):
    """
    Match API keys and discard ip
    @param key: API key from request
    @param ip: remote host IP to match the key.
    @return: boolean
    """
    if key is None or ip is None:
        logger.error('Either the api key or ip address is missing. This is an unauthorized attempt to access services.')
        return False
    api_key = get_apiauth_object_by_key(key)
    if api_key is None:
        logger.error(
            'Someone from ip address {} is attempting to access the service without a valid api key.'.format(ip))
        return False
    elif api_key.ip == "0.0.0.0":  # 0.0.0.0 means all IPs.
        return True
    elif api_key.key == key and api_key.ip == ip:
        return True

    logger.error(
        'Someone from ip address {} is trying to access the service with a valid api key that is not assigned to this ip.'.format(
            ip))
    return False


def match_user_auth_token(key):
    """
    Match API keys and discard ip
    @param key: API key from request
    @param ip: remote host IP to match the key.
    @return: boolean
    """
    if key is None:
        logger.error('Either the user auth key is missing. This is an unauthorized attempt to access services.')
        return False
    auth = get_user_auth_object_by_keyid(key)
    if auth is None:
        logger.error("The auth record for the auth_key_token {} is not found.".format(key))
        return False
    else:
        return True


def require_api_key(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        if match_api_keys(request.args.get(api_key_name), get_client_ip()):
            return f(*args, **kwargs)
        else:
            abort(401)
    return decorated


def require_user_auth_token(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        try:
            request_args = parser.parse(user_args, request)
            if request_args is None:
                logger.error("Cannot parse request to get the required arg.")
                abort(401)
            token = request_args['user_auth_token']
            if token is None:
                logger.error("Request does not contain any arg user_auth_token")
                abort(401)
            if match_user_auth_token(token):
                return f(*args, **kwargs)
            else:
                logger.error("Method does not qualify the required conditions. Could not find it in db.")
                abort(401)
        except Exception as e:
            logger.error(e)
            abort(401)
    return decorated
