package partlyapp.techpeg.com.partly.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.Models.Download;
import partlyapp.techpeg.com.partly.Models.Pool;
import partlyapp.techpeg.com.partly.R;
import partlyapp.techpeg.com.partly.RecyclerView.UsersRecyclerViewAdapter;
import partlyapp.techpeg.com.partly.Singleton.DownloadSingleton;
import partlyapp.techpeg.com.partly.Singleton.PoolSingleton;
import partlyapp.techpeg.com.partly.Singleton.SocketSingleton;

public class PoolActivity extends AppCompatActivity {

    private static final String TAG = "part.ly";
    EditText et_link;
    EditText et_magnetUrl;

    RecyclerView mRecyclerView;
    UsersRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool);
        ButterKnife.bind(this);
        loadMembers();
    }

    private void loadMembers() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_pool_members);
       mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new UsersRecyclerViewAdapter(getApplicationContext(), PoolSingleton.getInstance().getCurrentPool().getMembers());
        mRecyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.btn_new_download)
    public void newDownload(){
        final Download download=new Download();
        download.setActive(true);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.download_dialog, null, false);
        dialog.setView(v);
        dialog.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                et_link = v.findViewById(R.id.link_editText);
                et_magnetUrl = v.findViewById(R.id.et_magnetUrl);
                String link = et_link.getText().toString();
                if (!link.isEmpty()) {
                    if (URLUtil.isValidUrl(link)) {
                        if (Constants.network_connection) {
                            download.setLink(link);
                            currDownload(download);
                            SocketSingleton.sendUrlToServer(link);

                            Log.v(TAG, "link- " + link);
                            ;
                        } else {
                            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String magnet = et_magnetUrl.getText().toString();
                    if (!magnet.isEmpty()) {
                        if (Constants.network_connection) {
                            download.setLink(magnet);
                            download.setTorrent(true);
                            currDownload(download);
                            SocketSingleton.sendMagnetToServer(magnet);
                            Log.d(TAG, "magnet- " + magnet);
                        } else {
                            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        dialog.setCancelable(true);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    public void currDownload(Download download){
        DownloadSingleton.getInstance().setCurrDownload(download);
    }
}
