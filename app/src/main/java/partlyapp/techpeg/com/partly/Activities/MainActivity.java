package partlyapp.techpeg.com.partly.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketState;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import partlyapp.techpeg.com.partly.Broadcasts.NetworkChangeReceiver;
import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.Helpers.PreferenceHelper;
import partlyapp.techpeg.com.partly.Models.Pool;
import partlyapp.techpeg.com.partly.R;
import partlyapp.techpeg.com.partly.RecyclerView.RecentFilesRecyclerViewAdapter;
import partlyapp.techpeg.com.partly.RecyclerView.RecentPoolRecyclerViewAdapter;
import partlyapp.techpeg.com.partly.RecyclerView.UsersRecyclerViewAdapter;
import partlyapp.techpeg.com.partly.Singleton.DownloadSingleton;
import partlyapp.techpeg.com.partly.Singleton.PoolSingleton;
import partlyapp.techpeg.com.partly.Singleton.SocketSingleton;
import partlyapp.techpeg.com.partly.Singleton.UserSingleton;
import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;

import static partlyapp.techpeg.com.partly.Constants.Constants.ACTION_CREATE_USER;
import static partlyapp.techpeg.com.partly.Constants.Constants.KEY_NAME;
import static partlyapp.techpeg.com.partly.Constants.Constants.NAME_STRING_EXTRA;
import static partlyapp.techpeg.com.partly.Constants.Constants.NEW_USER_ACTIVITY_ID;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.btn_new_pool)
    Button btn_new_pool;
    @BindView(R.id.btn_your_pools)
    Button btn_your_pools;
    @BindView(R.id.btn_join_pool)
    Button btn_join_pool;

    public PreferenceHelper preferenceHelper;
    NetworkChangeReceiver networkChangeReceiver;
    WebSocketHelper socketHelper;
    WebSocket webSocket;
    public static final String TAG = "part.ly";
    RecyclerView mRecyclerView,m2;
    RecentPoolRecyclerViewAdapter adapter;
    RecentFilesRecyclerViewAdapter ada2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSocket();
        SugarContext.init(getApplicationContext());
        preferenceHelper = new PreferenceHelper(this);
        if (preferenceHelper.isFirstTimeLaunch()) {
            launchNewUSerScreen();
        }
        setContentView(R.layout.activity_main);
        checkPermission();
        loadUserToken();
        ButterKnife.bind(MainActivity.this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_recent_pool);
        m2 = (RecyclerView) findViewById(R.id.recycler_view);
        m2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ArrayList<String> s  = new ArrayList<String>();
        s.add("dfdsf");
        s.add("dsfsdfdsfs");
        ada2 = new RecentFilesRecyclerViewAdapter(getApplicationContext(), s );
        m2.setAdapter(ada2);
        loadRecentPool();
    }

    protected void loadRecentPool() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        ArrayList<Pool> pool_list = new ArrayList<Pool>();
        pool_list.add(new Pool("Chai Biscuit"));
        pool_list.add(new Pool("Bread Butter"));
        adapter = new RecentPoolRecyclerViewAdapter(getApplicationContext(),pool_list);
        mRecyclerView.setAdapter(adapter);

    }

    private void loadUserToken() {
        UserSingleton.getInstance().loadUserToken(getApplicationContext());
    }

    private void initializeSocket(){
        socketHelper=new WebSocketHelper();
        socketHelper.createWebSocket();
        socketHelper.connectSocket();
        webSocket=WebSocketHelper.getWebSocket();
    }

    private void launchNewUSerScreen() {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivityForResult(intent, NEW_USER_ACTIVITY_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                try {
                    Log.i(TAG, "result: " +result.getContents());
                    JSONObject obj = new JSONObject(result.getContents());

                    String pool_token = obj.getString(Constants.KEY_POOL_TOKEN);
                    Log.i(TAG, "scanned: " + pool_token);

                    joinPool(pool_token);


                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == NEW_USER_ACTIVITY_ID) {
            if (data != null) {
                String name = data.getStringExtra(NAME_STRING_EXTRA);
                preferenceHelper.setFirstTimeLaunch(false);
                Log.i("username", "name: " + name);
                WebSocketHelper socketHelper = new WebSocketHelper();
                WebSocket webSocket = WebSocketHelper.getWebSocket();
                if (webSocket != null) {
                    String payload = socketHelper.getStringMsg(KEY_NAME, name);
                    webSocket.sendText(socketHelper.getFrame(ACTION_CREATE_USER, payload));
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            UserSingleton.getInstance().saveUserToken(getApplicationContext(),UserSingleton.getInstance().getUser_token());
                        }
                    },3000);
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void joinPool(String pool_token) {

        PoolSingleton.getInstance().getCurrentPool().setPool_token(pool_token);
        String poolName=PoolSingleton.getInstance().extractPoolName(pool_token);
        /*
        if (Constants.network_connection) {
           // startDownloadFragment();
        } else {
            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
        }
        */
        SocketSingleton.addToPool(pool_token);
        Intent intent=new Intent(this,PoolActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Pool: "+poolName, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_join_pool)
    public void startQRScan() {
        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }

    @OnClick(R.id.btn_new_pool)
    public void startCreatePoolActivity(){
        Intent myIntent = new Intent(this, CreatePoolActivity.class);
        startActivity(myIntent);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            setupActivity();
            // permission is already granted

        } else {

            //persmission is not granted yet
            //Asking for permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);

            setupActivity();

        }
        boolean permitted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permitted = Settings.System.canWrite(this);
            Log.v("permission", "val-" + permitted);

            if (!permitted) {
                modifyPermissionDialog();
            }
        }

    }

    private void setupActivity() {
        Intent intent = getIntent();
        handleIntent(intent);
        registerNetworkChangeReceiver();
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            try {
                String action = intent.getAction();
                String type = intent.getType();
                String extra = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d("deeplink-", "action-" + action + " type-" + type + " extra-" + extra);
                if (action.equals(Intent.ACTION_VIEW)) {
                    Uri data = intent.getData();
                    if (data != null) {
                        Log.d("deeplink-", "action-" + action + " data-" + data);
                        if (data.toString().contains("part.ly/pool/")) {
                            String pool_token = data.getLastPathSegment();
                            Log.d("deeplink-", "pool token: " + pool_token);
                            handleConnection(pool_token, null);
                        } else {
                            String url = data.toString();
                            handleConnection(null, url);
                        }
                    }
                } else if (action.equals(Intent.ACTION_SEND)) {
                    handleConnection(null, extra);
                }

            } catch (Exception e) {
                //
            }
        }
    }

    private void handleConnection(final String pool_token, final String url) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog dialog1 = null;
        if (inflater != null) {
            final View v = inflater.inflate(R.layout.waiting_dialog, null, false);
            dialog.setView(v);
            dialog.setCancelable(false);
            dialog1 = dialog.create();
            dialog1.show();
        }


        final Handler handler = new Handler();
        final AlertDialog finalDialog = dialog1;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (WebSocketHelper.getWebSocket().getState() == WebSocketState.OPEN) {
                    if (finalDialog != null)
                        finalDialog.dismiss();
                    if (pool_token != null)
                        joinPool(pool_token);
                    else {
                        if (url.contains("magnet")) {
                            SocketSingleton.sendMagnetToServer(url);
                        } else {
                            SocketSingleton.sendUrlToServer(url);
                        }
                        Intent intent=new Intent(MainActivity.this,PoolActivity.class);
                        startActivity(intent);
                    }
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.postDelayed(runnable, 500);
    }

    private void registerNetworkChangeReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void modifyPermissionDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        //builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.dialog_btn_allow), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                }
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_btn_deny), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "modify settings permission denied");
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    //setupActivity();

                } else {

                    checkPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void unregisterNetworkChangeReceiver() {
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChangeReceiver();
        UserSingleton.getInstance().saveUserToken(getApplicationContext(),UserSingleton.getInstance().getUser_token());
    }
}
