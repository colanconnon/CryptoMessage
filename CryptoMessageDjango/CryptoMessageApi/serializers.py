from rest_framework import serializers
from CryptoMessageApi.models import Message, Device, Conversation
from django.contrib.auth.models import User


class DeviceSerializer(serializers.ModelSerializer):

    class Meta:
        model = Device
        fields = ['deviceName', 'publicKey']


class DeviceDetailSerializer(serializers.ModelSerializer):
    user = serializers.PrimaryKeyRelatedField(queryset=User.objects.all())

    class Meta:
        model = Device
        fields = ['id', 'publicKey', 'user']


class ConversationSerializer(serializers.ModelSerializer):

    class Meta:
        model = Conversation
        fields = ['id', 'name']



class UserSerializerNoPass(serializers.ModelSerializer):

    class Meta:
        model = User
        fields = ['id', 'username']


class UserSerializer(serializers.ModelSerializer):

    class Meta:
        model = User
        fields = ['id', 'username', 'password']


class MessageSerializer(serializers.ModelSerializer):
    messageSender = UserSerializerNoPass(many=False, read_only=False)
    conversation = ConversationSerializer(many=False, read_only=False)

    class Meta:
        model = Message
        fields = ['id', 'text', 'key', 'date', 'owner', 'messageSender', 'device', 'conversation']


class MessagePostSerializer (serializers.ModelSerializer):
    messageSender = serializers.PrimaryKeyRelatedField(queryset=User.objects.all())
    conversation = serializers.PrimaryKeyRelatedField(queryset=Conversation.objects.all())

    class Meta:
        model = Message
        fields = ['id', 'text', 'key', 'date', 'owner', 'messageSender', 'device', 'conversation']
