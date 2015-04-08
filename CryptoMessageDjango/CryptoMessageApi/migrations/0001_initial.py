# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Conversation',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
            ],
            options={
                'db_table': 'Conversation',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Device',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('deviceName', models.CharField(unique=True, max_length=200)),
                ('identifier', models.CharField(unique=True, max_length=255)),
                ('publicKey', models.TextField()),
            ],
            options={
                'db_table': 'Device',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Message',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False)),
                ('text', models.TextField()),
                ('key', models.TextField()),
                ('date', models.DateTimeField(auto_now_add=True)),
                ('conversation', models.ForeignKey(to='CryptoMessageApi.Conversation')),
                ('device', models.ForeignKey(to='CryptoMessageApi.Device')),
            ],
            options={
                'db_table': 'Message',
            },
            bases=(models.Model,),
        ),
    ]
