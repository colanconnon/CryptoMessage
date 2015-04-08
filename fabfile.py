from fabric.contrib.files import append,exists,sed
from fabric.api import env, local, run, sudo
import random

REPO_URL = "https://github.com/mdclark01/CryptoMessage.git"
env.key_filename = "~/.ssh/capstoneekey"
env.host = ['104.131.106.204']
env.user = 'colan'
env.use_ssh_config = True

def deploy():
	site_folder = '/home/%s/site/%s' % (env.user, env.host)
	source_folder = site_folder + '/source/'
	_create_directory_structure_if_necessary(site_folder)
	_get_latest_source(source_folder)
	_update_settings(source_folder, env.host)
	_update_virtualenv(source_folder)
	_update_static_files(source_folder)
	_update_database(source_folder)
	_touch_wsgi()

def _create_directory_structure_if_necessary(site_folder):
	for subfolder in ('database','static','virtualenv','source'):
		run('mkdir -p %s/%s' % (site_folder,subfolder))

def _get_latest_source(source_folder):
	if exists(source_folder + '/.git'):
		run('cd %s && git fetch' % (source_folder))
	else:
		run('git clone %s %s' %(REPO_URL, source_folder))
	current_commit = local('git log -n 1 --format=%H', capture=True)
	run('cd %s && git reset --hard %s' % (source_folder, current_commit))

def _update_settings(source_folder,site_name):
	settings_path = source_folder + '/CryptoMessageDjango/CryptoMessageDjango/settings.py'
	sed(settings_path, "DatabaseTest = True", "DatabaseTest = False")

def _update_virtualenv(source_folder):
	virtualenv_folder = source_folder +'/../virtualenv'
	if not exists(virtualenv_folder + '/bin/pip'):
		run('virtualenv --python=python3.4 %s' %(virtualenv_folder,))
	run('%s/bin/pip install -r %s/CryptoMessageDjango/requirements.txt --allow-all-external' %(virtualenv_folder,source_folder))

def _update_static_files(source_folder):
	run('cd %sCryptoMessageDjango && ../../virtualenv/bin/python3 manage.py collectstatic --noinput' %(source_folder))

def _update_database(source_folder):
	run('cd %sCryptoMessageDjango && ../../virtualenv/bin/python3 manage.py makemigrations --noinput' %(source_folder))
	run('cd %sCryptoMessageDjango && ../../virtualenv/bin/python3 manage.py migrate --noinput' %(source_folder))

def _touch_wsgi():
	run('touch /home/colan/site/104.131.106.204/source/CryptoMessageDjango/CryptoMessageDjango/wsgi.py')

def restart_server():
	sudo("service apache2 restart")