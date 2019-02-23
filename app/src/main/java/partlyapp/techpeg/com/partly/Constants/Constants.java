package partlyapp.techpeg.com.partly.Constants;

public class Constants {

    public static final String ACTION_SERVICE_UNBIND = "action_service_unbind";
    public static final String ACTION_APP_EXIT = "action_app_exit";
    public static final String ACTION_DOWNLOAD_CANCEL = "action_download_cancel";

    /* Web Socket Keys */
    public static final String ACTION_TYPE = "action_type";
    public static final String PAYLOAD = "payload";
    public static final String KEY_USER_TOKEN = "user_token";
    public static final String KEY_NAME = "name";
    public static final String KEY_POOL_NAME = "pool_name";
    public static final String KEY_JOBID = "id";
    public static final String KEY_ALLOTED_USER = "alloted_user";
    public static final String KEY_RANGE_START = "range_start";
    public static final String KEY_RANGE_END = "range_end";
    public static final String KEY_URL = "url";
    public static final String KEY_POOL_TOKEN = "pool_token";
    public static final String KEY_FILE_SIZE = "file_size";

    /* Web Socket Actions */
    public static final String ACTION_CREATE_USER = "create_user";
    public static final String ACTION_CREATE_POOL = "create_pool";
    public static final String ACTION_CREATE_DOWNLOAD="create_download";
    public static final String ACTION_START_DOWNLOAD = "start_download";
    public static final String ACTION_FILE_SIZE = "file_size";
    public static final String ACTION_ADD_TO_POOL = "add_to_pool";
    public static final String ACTION_NEW_POOL_USER = "new_pool_user";
    public static final String ACTION_NEW_JOB = "new_job";
    public static final String ACTION_NEW_TORRENT_JOB = "new_torrent_job";
    public static final String ACTION_JOB_FINISHED = "job_finished";
    public static final String ACTION_ALL_JOBS_FINISHED = "all_jobs_finished";
    public static final String ACTION_RESUME = "resume";

    /* Sockets Actions */
    public static final String ACTION_SEND_JOBS = "send_jobs";
    public static final String ACTION_SENDING_JOBS = "sending_jobs";
    public static final String ACTION_SEND_FILE = "send_file";
    public static final String ACTION_SENDING_FILE = "sending_file";
    public static final String ACTION_FILE_SENT = "file_sent";


    public static final String NAME_STRING_EXTRA = "user_name";
    public static final int NOTIFICATION_ID = 101;
    public static final int NEW_USER_ACTIVITY_ID = 99;

    public static String USER_TOKEN = null;
    public static String POOL_TOKEN = null;


    public static String filename;
    public static long content_length = 0;
    public static boolean resume_conn = false;
    public static boolean network_connection = true;
    public static boolean network_conn_open = true;
    public static boolean app_exit = false;
    public static long current_jobId = 0;
    public static boolean resume_pool = false;

    public static final String KEY_SSID = "ssid";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_LAST_POOL = "last_pool";


    public static boolean isTorrent = false;


    public static long startTime = 0;
    public static long stopTime = 0;

}
