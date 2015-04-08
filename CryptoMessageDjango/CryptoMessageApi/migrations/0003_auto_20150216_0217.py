# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('CryptoMessageApi', '0002_auto_20150216_0215'),
    ]

    operations = [
        migrations.AlterField(
            model_name='message',
            name='conversation',
            field=models.ForeignKey(to='CryptoMessageApi.Conversation', default=None),
            preserve_default=True,
        ),
    ]
