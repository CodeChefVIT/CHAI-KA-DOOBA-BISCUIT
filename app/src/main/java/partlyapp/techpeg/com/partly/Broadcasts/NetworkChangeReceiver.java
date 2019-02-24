package partlyapp.techpeg.com.partly.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocketState;

import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;


public class NetworkChangeReceiver extends BroadcastReceiver {

    WebSocketHelper socketHelper = new WebSocketHelper();

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                Log.d("network_change_receiver", "network connected");
                Constants.network_connection = true;

                if (Constants.network_conn_open)
                    Constants.network_conn_open = false;
                //else if (Constants.POOL_TOKEN != null)
                    //Constants.resume_conn = true;


                if (WebSocketHelper.getWebSocket().getState() == WebSocketState.CLOSED) {
                    socketHelper.connectSocket();
                }

            } else {
                Constants.network_connection = false;
                Log.d("network_change_receiver", "network disconnected");
            }
        }
    }
}