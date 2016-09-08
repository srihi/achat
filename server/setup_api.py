# the lines below are only used for debugging when we start the app manually using python xxxx.py
# command. If the application has never been initialized use the lines below to setup the database.
from achat_logging import logger
from wsgi import initialize_application, application

if __name__ == '__main__':
    initialize_application(application)
    logger.info("Application directly started by calling from command line.")
    application.run(host='0.0.0.0', debug=True)
