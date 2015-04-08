from django.db import models
from django.contrib.auth.models import User
from django.conf import settings
from django.contrib.auth import get_user_model
from django.db.models.signals import post_save
from django.dispatch import receiver
from rest_framework.authtoken.models import Token

# Create your models here.


class Message(models.Model):
    id = models.AutoField(primary_key=True)
    text = models.TextField()
    key = models.TextField()
    date = models.DateTimeField(auto_now_add=True)
    owner = models.BooleanField(default=False)
    messageSender = models.ForeignKey('auth.User')
    device = models.ForeignKey("Device")
    conversation = models.ForeignKey("Conversation", default=None)

    class Meta:
        db_table = "Message"


class Device(models.Model):
    id = models.AutoField(primary_key=True)
    # the public key for the device
    publicKey = models.TextField()
    user = models.ForeignKey('auth.User')

    class Meta:
        db_table = "Device"


class Conversation(models.Model):
    id = models.AutoField(primary_key=True)
    name = models.TextField(default=None)

    class Meta:
        db_table = "Conversation"



@receiver(post_save, sender=settings.AUTH_USER_MODEL)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    if created:
        Token.objects.create(user=instance)
