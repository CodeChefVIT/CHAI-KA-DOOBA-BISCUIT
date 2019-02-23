package partlyapp.techpeg.com.partly.WebSocket;



import android.arch.lifecycle.ViewModelProviders;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.MainActivity;
import partlyapp.techpeg.com.partly.ViewModels.PoolViewModel;
import partlyapp.techpeg.com.partly.ViewModels.SocketViewModel;

public class SocketListener extends WebSocketAdapter {

    WebSocketHelper socketHelper = new WebSocketHelper();
    PoolViewModel poolViewModel;

    SocketListener(){
        MainActivity mActivity=MainActivity.getInstance();
        poolViewModel=ViewModelProviders.of(mActivity).get(PoolViewModel.class);

    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
        Log.d("websocketcon", "frameerror -" + cause.getMessage());
        super.onFrameError(websocket, cause, frame);
    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.d("websocketcon", "unexpected err -" + cause.getMessage());
        super.onUnexpectedError(websocket, cause);
    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
        Log.d("websocketcon", "callback err -" + cause.getMessage());
        super.handleCallbackError(websocket, cause);
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        Log.d("websocketcon", "text -" + text);
        //processMessage(text);
        super.onTextMessage(websocket, text);
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        Log.d("websocketcon", "connected");

        super.onConnected(websocket, headers);
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
        Log.d("websocketcon", "connection error-" + exception.getMessage());
        super.onConnectError(websocket, exception);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
        Log.e("websocketcon", "error- " + cause.getMessage());
        cause.printStackTrace();
        super.onError(websocket, cause);
    }


    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.d("websocketcon", "frame- " + frame.toString());
        super.onFrame(websocket, frame);
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        Log.d("websocketcon", "disconnected" + " closed by server-" + closedByServer);


        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
        Log.d("websocketcon", "sending frame-" + frame);
        super.onFrameSent(websocket, frame);
    }


    public void processMessage(String message) {
        try {
            JSONObject object = new JSONObject(message);
            String action_type = object.getString(Constants.ACTION_TYPE);
            String payload = object.getString(Constants.PAYLOAD);
            JSONObject payloadObj = new JSONObject(payload);
            switch (action_type) {
                case Constants.ACTION_CREATE_USER:
                    String token = payloadObj.getString(Constants.KEY_USER_TOKEN);

                    break;
                case Constants.ACTION_NEW_POOL_USER:
                    String newUser = payloadObj.getString(Constants.KEY_NAME);

                    break;
                case Constants.ACTION_CREATE_POOL:
                    String poolId = payloadObj.getString(Constants.KEY_POOL_TOKEN);
                    poolViewModel.getCurrentPool().setPool_token(poolId);
                    break;

            }
        } catch (JSONException e) {
            Log.e("socket_listener", "error-" + e.getMessage());
            e.printStackTrace();
        }

    }
}
