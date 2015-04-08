from django.shortcuts import render
from django.http import HttpResponse

from rest_framework import status
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.response import Response
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

from CryptoMessageApi.models import Message, Conversation, Device, User
from CryptoMessageApi.serializers import MessageSerializer, ConversationSerializer, DeviceSerializer, UserSerializer, \
    DeviceDetailSerializer, MessagePostSerializer

from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.authtoken.models import Token

# FOR AUTHENTICAITON PASS username and password variables via post or json to
#  url(r'^api-token-auth/', views.obtain_auth_token)
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)


# Create your views here.
@csrf_exempt
@api_view(['POST', 'DELETE'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def message_detail(request):
    """
    This function will return all the messages from one conversation belonging to a device
    """
    if request.method == "POST":
        user = request.user
        if user is not None:
            serializer = MessagePostSerializer(data=request.DATA)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status=status.HTTP_201_CREATED)
            else:
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    elif request.method == "DELETE":
        user = User.objects.get(username=request.data['username'], password=request.data['password'])
        if user is not None:
            messageID = request.data['messageID']
            message = Message.objects.get(pk=messageID)
            message.delete()
            return Response(status.HTTP_202_ACCEPTED)


@csrf_exempt
@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def get_message_list(request, pk):
    """
    get the username and password to authenticate the user, and then return all the messages for that user

    """
    if request.method == "GET":
        user = request.user
        if user is not None:
            device = Device.objects.get(user=user)
            messages = None
            pk = int(pk)
            if pk == 0:
                messages = Message.objects.filter(device=device)
            elif pk > 0:
                messages = Message.objects.filter(device=device, pk__gt=pk)
            serializer = MessageSerializer(messages, many=True)
            return Response(serializer.data)


@csrf_exempt
@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def get_user_id(request):
    """
    this function takes a username and password and returns the primary key for
    that user

    """
    if request.method == "POST":
        user = request.user
        if user is not None:
            return JsonResponse({"id": user.id})
        else:
            return Response(status.HTTP_400_BAD_REQUEST)


@csrf_exempt
@api_view(['POST'])
def create_user(request):
    """
        This function takes in username, and password as parameters
        then creates a new user and returns that data
    """
    if request.method == "POST":
        serializer = UserSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@csrf_exempt
@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def create_device(request):
    """
    This method creates a new device and takes
    id, deviceName, identifier, publicKey, and user(id) as params

    Creates a new device and returns the data

    """
    if request.method == "POST":
        user = request.user
        devices = Device.objects.all().filter(user=user)
        devices.delete()
        request.data['user'] = user.pk
        if user is not None:
            serializer = DeviceDetailSerializer(data=request.data)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status.HTTP_201_CREATED)
            else:
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)


@csrf_exempt
@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def device_detail(request, devicename):
    """
    This function allows somebody to retrieve a devices publicKey
    takes username as parameter

    """
    try:
        device = Device.objects.get(deviceName=devicename)
    except Device.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == "GET":
        serializer = DeviceSerializer(device, many=False)
        return Response(serializer.data)


@csrf_exempt
@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def get_user_devices(request, username):
    """
    Takes the username as a parameter, and returns all that users devices
    """
    try:
        user = request.user
    except User.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == "GET":
        user = User.objects.get(username=username)
        device = Device.objects.get(user=user)
        serializer = DeviceDetailSerializer(device, many=False)
        return Response(serializer.data)


@csrf_exempt
@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def create_new_conversation(request):
    """
    creates a new conversation

    """
    if request.method == "POST":
        user = request.user
        if user is not None:
            serializer = ConversationSerializer(data=request.data)
            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status=status.HTTP_201_CREATED)
            else:
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@csrf_exempt
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
@api_view(['GET'])
def get_messages_from_conversation(request):
    """
    get messages from a conversation ID

    """
    if request.method == "GET":
        user = request.user
        if user is not None:
            device = Device.objects.get(user=user)
            messages = Message.objects.all().filter(conversation=request.data['conversationID'],
                                                    device=device)
            serializer = MessageSerializer(messages, many=True)
            return Response(serializer.data)


@csrf_exempt
@api_view(['POST'])
def login(request):
    if request.method == "POST":
        user = User.objects.get(username=request.data['username'], password=request.data['password'])
        token = Token.objects.get(user=user)
        if not token:
            return Response(status=status.HTTP_400_BAD_REQUEST)
        JsonResponseDict = dict()
        JsonResponseDict['token'] = token.key
        JsonResponseDict['success'] = 1
        JsonResponseDict['userID'] = user.pk
        return JsonResponse(JsonResponseDict)
