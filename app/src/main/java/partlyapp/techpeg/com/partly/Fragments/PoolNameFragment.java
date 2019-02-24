package partlyapp.techpeg.com.partly.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;

import butterknife.BindView;
import butterknife.ButterKnife;
import partlyapp.techpeg.com.partly.Activities.PoolActivity;
import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.R;
import partlyapp.techpeg.com.partly.Singleton.PoolSingleton;
import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;


public class PoolNameFragment extends Fragment {

    @BindView(R.id.btn_name_next)
    Button btn_name_next;
    @BindView(R.id.et_pool_name)
    EditText et_pool_name;
    WebSocket webSocket;
    WebSocketHelper socketHelper;

    public PoolNameFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socketHelper=new WebSocketHelper();
        webSocket=WebSocketHelper.getWebSocket();
    }

    private void swapFragment(){
        FragmentTransaction ft= getFragmentManager().beginTransaction();
        ft.replace(R.id.new_pool_container,new AddMembersFragment());
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_pool_name, container, false);
        ButterKnife.bind(this,v);
        btn_name_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pool_name=et_pool_name.getText().toString();
                if(!pool_name.isEmpty()) {

                    sendPoolNameToServer(pool_name);
                    swapFragment();
                }else{
                    Toast.makeText(getContext(),"Please enter some name",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }


    public void sendPoolNameToServer(String name) {
        PoolSingleton.getInstance().getCurrentPool().setName(name);
        String payload = socketHelper.getStringMsg(Constants.KEY_POOL_NAME, name);
        String frame = socketHelper.getFrame(Constants.ACTION_CREATE_POOL, payload);
        Log.d("websocket", "frame sending-" + frame);
        if (webSocket != null) {
            webSocket.sendText(frame);
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
