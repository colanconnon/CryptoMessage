from django.contrib import admin
from CryptoMessageApi.models import Message, Conversation, Device, User


# Register your models here.
admin.site.register(Device)
admin.site.register(Message)
admin.site.register(Conversation)
