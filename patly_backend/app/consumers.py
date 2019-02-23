from channels.generic.websocket import WebsocketConsumer
import json
import requests
import jwt
import time

from app.serializers import *
from app.models import *
from app.serializers import JobSerializer
import libtorrent as lt

ses = lt.session()
ses.listen_on(6881, 6891)
ses.add_extension('ut_metadata')
ses.add_extension('ut_pex')
ses.add_extension('metadata_transfer')
ses.add_dht_router("router.utorrent.com", 6881)
ses.add_dht_router("router.bittorrent.com", 6881)
ses.add_dht_router("dht.transmissionbt.com", 6881)
ses.add_dht_router("dht.aelitis.com", 6881)
ses.start_dht()
ses.start_lsd()
ses.start_upnp()
ses.start_natpmp()

params = {
	'save_path': '/tmp/',
	'flag_auto_managed': False,
	'flag_stop_when_ready': True,
	'upload_mode': True
}

ses.add_dht_router("router.utorrent.com", 6881)
ses.add_dht_router("router.bittorrent.com", 6881)
ses.add_dht_router("dht.transmissionbt.com", 6881)
ses.start_dht()

websocket_connections = dict()

STATIC_JOB_COUNT = 4

def generate_response(action_type,payload):
    return json.dumps({"action_type":action_type,"payload":payload})


def bytes_divide(start,size,count,skip=1):
    division = int((int(size)-start)/count)
    result = []
    for i in range(count):
        end = start + division
        if i == count-skip:
            end = int(size)
        result.append([start,end])
        start = end
    return result

def fx_resume(conn,json_data):
    user_token = json_data['user_token']
    user_id = jwt.decode(user_token, 'secret')['id']
    print("user id:",user_id)
    for pool_id in websocket_connections.keys():
        if user_id in websocket_connections[pool_id].keys():
            websocket_connections[pool_id][user_id] = conn
            user = DLUser.objects.get(id=jwt.decode(user_token,'secret')['id'])
            print("user resumed: ",user)
            pool = Pool.objects.get(id=pool_id)
            print(pool)
            finished_jobs=Jobs.objects.filter(pool=pool,status="COMPLETED")
            for job in finished_jobs:
                response = JobSerializer(job).data
                conn.send(text_data=generate_response('job_finished',response))

            alloted_jobs=Jobs.objects.filter(alloted_user=user,pool=pool,status="ALLOTED")
            if len(alloted_jobs) > 0:
                alloted_job = alloted_jobs[0]
                response = JobSerializer(alloted_job).data
                print("alloted job: ",response)
                conn.send(text_data=generate_response('new_job', response))
            else:
                jobs_left=Jobs.objects.filter(pool=pool,status="RESERVED")
                if len(jobs_left) > 0:
                 job_left = jobs_left[0]
                 job_left.alloted_user=user
                 job_left.status="ALLOTED"
                 job_left.save()
                 response = JobSerializer(job_left).data
                 print("new job assigned: ",response)
                 conn.send(text_data=generate_response('new_job', response))


            print("user jobs:",alloted_jobs)


def fx_new_pool_user(pool_token,user,pool):
    p_user = PoolUsers.objects.create(pool=pool, user=user)
    p_user.save()
    response = {'action_type':'new_pool_user','payload':{'name':user.name}}
    for user_id in websocket_connections[pool.id].keys():
        if user_id == user.id:
            continue
        websocket_connections[pool.id][user_id].send(text_data=json.dumps(response))
    for iuser in PoolUsers.objects.filter(pool=pool):
        response = {'action_type': 'new_pool_user', 'payload': {'name': iuser.user.name}}
        websocket_connections[pool.id][user.id].send(text_data=json.dumps(response))

def start_download_torrent(pool):
    dl = Download.objects.get(pool=pool,is_active=True)
    h = lt.add_magnet_uri(ses,dl.url, params)
    while (not h.has_metadata()):
        time.sleep(1)
        print("No metadata")
    info = h.get_torrent_info()
    total_pieces = info.num_pieces()
    for x in range(info.num_files()):
        h.file_priority(x, 0)

    pool_users = PoolUsers.objects.filter(pool=pool)
    divisions = bytes_divide(0,total_pieces,len(pool_users),0)
    for i in range(len(pool_users)):
        user_alloted = divisions[i]
        job = Jobs.objects.create(pool=pool, alloted_user=pool_users[i].user, range_start=user_alloted[0],
                                          range_end=user_alloted[1], status="ALLOTED",left=0,url=dl.url)
        job.save()
        response = JobSerializer(job).data
        websocket_connections[pool.id][pool_users[i].user.id].send(text_data=generate_response('new_torrent_job',response))


def fx_start_download(conn,json_data):
    pool_token = json_data['pool_token']
    pool = Pool.objects.get(id=jwt.decode(pool_token,'secret')['id'])
    dl = Download.objects.get(pool=pool,is_active=True)
    if "magnet" in dl.url:
        return start_download_torrent(pool)

    file_size = requests.get(dl.url,stream=True).headers['Content-length']
    size_payload = {'file_size':file_size}
    for user_id in websocket_connections[pool.id]:
        websocket_connections[pool.id][user_id].send(text_data=generate_response('file_size',size_payload))

    pool_users = PoolUsers.objects.filter(pool=pool)
    divisions = bytes_divide(0,file_size,len(pool_users))
    for i in range(len(pool_users)):
        user_alloted = divisions[i]
        first_job = True
        for result in bytes_divide(user_alloted[0],user_alloted[1],STATIC_JOB_COUNT):
            if first_job:
                job = Jobs.objects.create(pool=pool, alloted_user=pool_users[i].user, range_start=result[0],
                                          range_end=result[1], status="ALLOTED",left=(result[1]-result[0]),url=dl.url)
                job.save()
                response = JobSerializer(job).data
                websocket_connections[pool.id][pool_users[i].user.id].send(text_data=generate_response('new_job',response))
                first_job = False
            else:
                job = Jobs.objects.create(pool=pool, alloted_user=pool_users[i].user, range_start=result[0],
                                          range_end=result[1], status="RESERVED",left=(result[1]-result[0]),url=dl.url)
                job.save()



def fx_job_finished(conn,json_data):
    job_id = json_data['id']
    job = Jobs.objects.get(id=job_id)
    job.status = 'COMPLETED'
    job.left = 0
    job.save()

    print("alloted_user:",job.alloted_user)
    #new_job = Jobs.objects.filter(pool=job.pool,alloted_user=job.alloted_user,status="RESERVED")
    new_job = Jobs.objects.filter(download=job.download,status="RESERVED")
    user_token = json_data['user_token']
    user = DLUser.objects.get(id=jwt.decode(user_token,'secret')['id'])

    for user_id in websocket_connections[job.download.pool.id].keys():
        websocket_connections[job.download.pool.id][user_id].send(text_data=generate_response('job_finished',json_data))
    if len(new_job) > 0:
        new_job = new_job[0]
        new_job.status = "ALLOTED"
        new_job.alloted_user=user
        new_job.save()
        response = JobSerializer(new_job).data
        #websocket_connections[job.pool.id][job.alloted_user.id].send(text_data=generate_response('new_job', response))
        conn.send(text_data=generate_response('new_job', response))

    else:
        jobs_todo = Jobs.objects.filter(pool=job.download.pool,status="RESERVED")
        if len(jobs_todo) > 0:
         job_todo = jobs_todo[0]
         job_todo.status = "ALLOTED"
         job_todo.alloted_user=user
         job_todo.save()
         response = JobSerializer(job_todo).data
         conn.send(text_data=generate_response('new_job', response))

        jobs_left = Jobs.objects.filter(pool=job.download.pool,status="ALLOTED")
        if len(jobs_left) == 0:
            response = PoolSerializer(job.download.pool).data
            for userid in websocket_connections[job.download.pool.id].keys():
                websocket_connections[job.download.pool.id][userid].send(text_data=generate_response('all_jobs_finished',response))


def fx_create_user(conn,json_data):
    name = json_data['name']
    user = DLUser.objects.create(name=name)
    user.save()
    payload = DLUserSerializer(user)
    token = jwt.encode(payload.data,'secret').decode('utf-8')
    conn.send(text_data=generate_response('create_user',{"user_token":token}))


def fx_create_pool(conn,json_data):
    user_token = json_data['user_token']
    user = DLUser.objects.get(id=jwt.decode(user_token,'secret')['id'])
    pool = Pool.objects.create(admin=user)
    pool.save()
    payload = PoolSerializer(pool)
    pool_token = jwt.encode(payload.data,'secret').decode('utf-8')
    websocket_connections[pool.id] = dict()
    websocket_connections[pool.id][user.id] = conn
    fx_new_pool_user(pool_token, user, pool)
    response = {"pool_token":pool_token}
    conn.send(text_data=generate_response('create_pool',response))

def fx_create_download(conn,json_data):
    user_token = json_data['user_token']
    user = DLUser.objects.get(id=jwt.decode(user_token,'secret')['id'])
    pool_token = json_data['pool_token']
    pool = Pool.objects.get(id=jwt.decode(user_token,'secret')['id'])
    url = json_data['url']
    dl = Download.objects.create(url=json_data['url'],is_active=True,pool=pool)
    dl.save()
    payload = DownloadSerializer(dl)
    dl_token = jwt.encode(payload.data,'secret').decode('utf-8')
    response = {"download_token":dl_token}
    conn.send(text_data=generate_response('create_download',response))




def fx_add_to_pool(conn,json_data):
    pool_token = json_data['pool_token']
    pool = Pool.objects.get(id=jwt.decode(pool_token,'secret')['id'])
    user_token = json_data['user_token']
    user = DLUser.objects.get(id=jwt.decode(user_token,'secret')['id'])
    websocket_connections[pool.id][user.id] = conn
    fx_new_pool_user(pool_token,user,pool)


class AppConsumer(WebsocketConsumer):
    def connect(self):
        self.accept()

    def disconnect(self, close_code):
        print("disconnected: ",close_code)
        if(close_code == 1000):
         for pool_id in websocket_connections.keys():
            for user_id in websocket_connections[pool_id].keys():
                if websocket_connections[pool_id][user_id] == self:
                    websocket_connections[pool_id].pop(user_id)
                    break
     

    def receive(self, text_data):
        print(text_data)
        text_data_json = json.loads(text_data)
        action = text_data_json['action_type']
        globals()['fx_'+action](self,json.loads(text_data_json['payload']))

