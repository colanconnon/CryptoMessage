# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('CryptoMessageApi', '0005_auto_20150304_1837'),
    ]

    operations = [
        migrations.AddField(
            model_name='message',
            name='messageSender',
            field=models.ForeignKey(default=None, to=settings.AUTH_USER_MODEL),
            preserve_default=True,
        ),
        migrations.AddField(
            model_name='message',
            name='owner',
            field=models.BooleanField(default=False),
            preserve_default=True,
        ),
    ]
