import logging
from logging.handlers import RotatingFileHandler

logger = logging.getLogger('achat-api-logger')
logger.setLevel(logging.DEBUG)
log_formatter = logging.Formatter('|%(asctime)s| %(levelname)-8s| %(message)s', "%Y-%m-%d[%H:%M:%S]")
handler = RotatingFileHandler('achat_api.log', maxBytes=10000, backupCount=1)
handler.setLevel(logging.INFO)
handler.setFormatter(log_formatter)
logger.addHandler(handler)
