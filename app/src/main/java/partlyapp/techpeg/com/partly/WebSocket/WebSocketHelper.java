package partlyapp.techpeg.com.partly.WebSocket;


import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import partlyapp.techpeg.com.partly.Constants.Constants;

public class WebSocketHelper {

    private static WebSocket webSocket;

    public static WebSocket getWebSocket() {
        return webSocket;
    }

    public void createWebSocket() {
        WebSocketFactory factory = new WebSocketFactory();
        try {
            webSocket = factory.createSocket("http://51.158.72.92:8000/ws/", 5000);
            SocketListener listener = new SocketListener();
            webSocket.addListener(listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectSocket() {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (webSocket.getState() != WebSocketState.CREATED)
                            createWebSocket();
                        webSocket.connect();
                    } catch (WebSocketException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectSocket() {
        if (webSocket != null)
            webSocket.disconnect();
    }


    public String getStringMsg(String key, String value) {
        JSONObject object = new JSONObject();
        try {
            object.put(key, value);
            if (Constants.USER_TOKEN != null)
                object.put(Constants.KEY_USER_TOKEN, Constants.USER_TOKEN);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getStringMsg(String[] keys, Object[] values) {
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < keys.length; i++) {
                object.put(keys[i], values[i]);
            }
            if (Constants.USER_TOKEN != null)
                object.put(Constants.KEY_USER_TOKEN, Constants.USER_TOKEN);

            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getStringMsg(String key, Object value) {
        JSONObject object = new JSONObject();
        try {
            object.put(key, value);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getFrame(String action, String payload) {
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.ACTION_TYPE, action);
            object.put(Constants.PAYLOAD, payload);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFrame(String action, String payload, String token) {
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.ACTION_TYPE, action);
            object.put(Constants.PAYLOAD, payload);
            object.put(Constants.KEY_USER_TOKEN, token);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
