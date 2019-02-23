from .models import Jobs, DLUser, Pool

from rest_framework import serializers


class JobSerializer(serializers.ModelSerializer):
    class Meta:
        model = Jobs
        fields = ('id','range_start','range_end','alloted_user','url')

class DLUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = DLUser
        fields = '__all__'

class PoolSerializer(serializers.ModelSerializer):
    class Meta:
        model = Pool
        fields = '__all__'

class DownloadSerializer(serializers.ModelSerializer):
    class Meta:
        model = Download
        fields = '__all__'