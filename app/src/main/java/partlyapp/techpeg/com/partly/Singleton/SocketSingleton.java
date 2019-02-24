package partlyapp.techpeg.com.partly.Singleton;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketState;

import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;

public class SocketSingleton {
    private static final SocketSingleton ourInstance = new SocketSingleton();

    public static SocketSingleton getInstance() {
        return ourInstance;
    }
    static WebSocketHelper socketHelper;
    static WebSocket webSocket;
    private SocketSingleton() {
        socketHelper=new WebSocketHelper();
        webSocket=WebSocketHelper.getWebSocket();
    }



    public static void addToPool(String pool_token) {
        String payload = socketHelper.getStringMsg(Constants.KEY_POOL_TOKEN, pool_token);
        String frame = socketHelper.getFrame(Constants.ACTION_ADD_TO_POOL, payload);
        if (webSocket != null) {
            if (webSocket.getState() == WebSocketState.CLOSED)
                webSocket = WebSocketHelper.getWebSocket();
            webSocket.sendText(frame);
        }
    }

    public static void sendUrlToServer(String url) {
        String poolToken=PoolSingleton.getInstance().getCurrentPool().getPool_token();
        String payload = socketHelper.getStringMsg(new String[]{Constants.KEY_URL,Constants.KEY_POOL_TOKEN}, new Object[]{url,poolToken});
        String frame = socketHelper.getFrame(Constants.ACTION_CREATE_DOWNLOAD, payload);
        Log.d("websocket", "frame sending-" + frame);
        WebSocket webSocket = WebSocketHelper.getWebSocket();
        if (webSocket != null) {
            webSocket.sendText(frame);
        }
    }

    public static void sendMagnetToServer(String magnet) {
        String payload = socketHelper.getStringMsg(Constants.KEY_URL, magnet);
        String frame = socketHelper.getFrame(Constants.ACTION_CREATE_DOWNLOAD, payload);
        Log.d("websocket", "frame sending-" + frame);
        WebSocket webSocket = WebSocketHelper.getWebSocket();
        if (webSocket != null) {
            webSocket.sendText(frame);
        }
    }
}
