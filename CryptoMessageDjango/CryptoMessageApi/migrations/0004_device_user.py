# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('CryptoMessageApi', '0003_auto_20150216_0217'),
    ]

    operations = [
        migrations.AddField(
            model_name='device',
            name='user',
            field=models.ForeignKey(to=settings.AUTH_USER_MODEL, default=None),
            preserve_default=True,
        ),
    ]
