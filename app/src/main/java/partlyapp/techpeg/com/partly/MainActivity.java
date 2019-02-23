package partlyapp.techpeg.com.partly;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.neovisionaries.ws.client.WebSocket;

import butterknife.BindView;
import butterknife.ButterKnife;
import partlyapp.techpeg.com.partly.Activities.CreatePoolActivity;
import partlyapp.techpeg.com.partly.Activities.NewUserActivity;
import partlyapp.techpeg.com.partly.Helpers.PreferenceHelper;
import partlyapp.techpeg.com.partly.ViewModels.SocketViewModel;
import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;

import static partlyapp.techpeg.com.partly.Constants.Constants.ACTION_CREATE_USER;
import static partlyapp.techpeg.com.partly.Constants.Constants.KEY_NAME;
import static partlyapp.techpeg.com.partly.Constants.Constants.NAME_STRING_EXTRA;
import static partlyapp.techpeg.com.partly.Constants.Constants.NEW_USER_ACTIVITY_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.btn_new_pool)
    Button btn_new_pool;
    @BindView(R.id.btn_your_pools)
    Button btn_your_pools;

    public PreferenceHelper preferenceHelper;
    private static MainActivity sActivity;

    public static MainActivity getInstance() {
        return sActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity=this;
        preferenceHelper = new PreferenceHelper(this);
        if (preferenceHelper.isFirstTimeLaunch()) {
            launchNewUSerScreen();
        }
        setContentView(R.layout.activity_main);

        ButterKnife.bind(MainActivity.this);
        SocketViewModel socketViewModel =
                ViewModelProviders.of(this).get(SocketViewModel.class);
        socketViewModel.initializeHelper();
        btn_new_pool.setOnClickListener(this);
        btn_your_pools.setOnClickListener(this);
    }

    private void launchNewUSerScreen() {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivityForResult(intent, NEW_USER_ACTIVITY_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_new_pool:
                Intent myIntent = new Intent(MainActivity.this, CreatePoolActivity.class);
                startActivity(myIntent);
                break;
            case R.id.btn_your_pools:

                break;
        }
    }
}
