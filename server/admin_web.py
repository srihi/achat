import base64
import hashlib
from datetime import datetime

from flask import Blueprint, render_template, redirect, url_for
from flask_login import LoginManager, login_user, login_required, current_user, logout_user
from flask_wtf import Form
from wtforms import StringField, PasswordField
from wtforms.validators import DataRequired

from achat_logging import logger
from database import db
from models import UserAccount, APIAuthRecord, UserProfile

admin_blueprint = Blueprint('admin', __name__, url_prefix='/admin', template_folder='templates')

login_manager = LoginManager()
login_manager.login_view = 'admin.login'


class LoginForm(Form):
    email = StringField('email', validators=[DataRequired()], render_kw={"placeholder": "email"})
    password = PasswordField('password', validators=[DataRequired()], render_kw={"placeholder": "password"})


@login_manager.user_loader
def user_loader(user_id):
    return UserAccount.query.filter_by(user_email=user_id).first()


@admin_blueprint.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()
    if form.validate_on_submit():

        user = UserAccount.query.filter_by(user_email=form.email.data).first()
        if user:
            # generate the password hash and compare to the one in the database

            hash_algo = hashlib.sha512()
            hash_algo.update(base64.b64encode(form.password.data) + user.password_salt)
            password_digest = hash_algo.hexdigest()

            if password_digest == user.password_digest:
                user.authenticated = True
                db.session.add(user)
                db.session.commit()
                login_user(user, remember=True)
                logger.warn('User {} successfully logged into the admin interface on {}'.format(user.user_email,
                                                                                                datetime.now()))
                return redirect(url_for('admin.get_api_keys'))
            else:
                logger.error('wrong password, failed to log into the admin interface.')
        else:
            logger.error('The user email passed does not exist in the user database.')

    return render_template('login.html', form=form)


@admin_blueprint.route("/logout", methods=["GET"])
@login_required
def logout():
    """
    Logout the current user.
    """
    logger.info('User {} logged out of the admin interface on {}'.format(current_user.user_email, datetime.now()))
    logout_user()
    return render_template("logout.html")


@admin_blueprint.route('/manage_api_keys', methods=['GET'])
@login_required
def get_api_keys():
    api_key_list = APIAuthRecord.query.all()
    return render_template(
        'manage_api_keys.html',
        api_key_list=api_key_list
    )


@admin_blueprint.route('/manage_users', methods=['GET'])
@login_required
def list_users():
    user_list = UserAccount.query.all()
    user_profile_list = UserProfile.query.all()

    return render_template(
        'manage_users.html',
        user_list=user_list
    )
