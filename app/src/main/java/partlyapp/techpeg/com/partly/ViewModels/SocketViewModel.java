package partlyapp.techpeg.com.partly.ViewModels;

import android.arch.lifecycle.ViewModel;

import com.neovisionaries.ws.client.WebSocket;

import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;

public class SocketViewModel extends ViewModel {

    private WebSocketHelper socketHelper;
    private WebSocket webSocket;

    public void initializeHelper() {
        if(socketHelper==null) {
            socketHelper = new WebSocketHelper();
            socketHelper.createWebSocket();
            socketHelper.connectSocket();
        }
    }

    public WebSocket getWebSocket(){
        if(webSocket==null){
            initializeHelper();
        }
        return webSocket;
    }
}
