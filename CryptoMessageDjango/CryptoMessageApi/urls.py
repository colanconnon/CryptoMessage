from django.conf.urls import patterns, include, url
from django.contrib import admin
import CryptoMessageApi.views
from rest_framework.authtoken import views

urlpatterns = patterns('',

    # url(r'^$', 'CryptoMessageApi.views.index'),
    url(r'^messagedetail/', 'CryptoMessageApi.views.message_detail'),
    url(r'^devicedetail/(?P<devicename>\w+)/', 'CryptoMessageApi.views.device_detail'),
    url(r'^createdevice/', 'CryptoMessageApi.views.create_device'),
    url(r'^createuser/', 'CryptoMessageApi.views.create_user'),
    url(r'^messages/(?P<pk>\w+)/', 'CryptoMessageApi.views.get_message_list'),
    url(r'^getdevices/(?P<username>\w+)/', 'CryptoMessageApi.views.get_user_devices'),
    url(r'^getmessages/', 'CryptoMessageApi.views.get_messages_from_conversation'),
    url(r'^createconversation/', 'CryptoMessageApi.views.create_new_conversation'),
    url(r'^login/', 'CryptoMessageApi.views.login'),
)
