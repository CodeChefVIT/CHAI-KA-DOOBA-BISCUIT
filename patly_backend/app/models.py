from django.db import models

# Create your models here.

class DLUser(models.Model):
    name = models.CharField(max_length=127)

    def __str__(self):
        return self.name


class Pool(models.Model):
    admin = models.ForeignKey(DLUser,on_delete=models.CASCADE)

class Download(models.Model):
    url = models.CharField(max_length=1023)
    headers = models.TextField()
    pool = models.ForeignKey(Pool,on_delete=models.CASCADE)
    is_active = models.BooleanField(default=False)
    status = models.CharField(max_length=1024,default='DOWNLOADING')

class PoolUsers(models.Model):
    user = models.ForeignKey(DLUser,on_delete=models.CASCADE)
    pool = models.ForeignKey(Pool,on_delete=models.CASCADE)
    status = models.CharField(max_length=127)

class Jobs(models.Model):
    download = models.ForeignKey(Download,on_delete=models.CASCADE)
    alloted_user = models.ForeignKey(DLUser,on_delete=models.CASCADE)
    range_start = models.IntegerField()
    range_end = models.IntegerField()
    status = models.CharField(max_length=127)
    left = models.IntegerField()
    url = models.CharField(max_length=1023)
