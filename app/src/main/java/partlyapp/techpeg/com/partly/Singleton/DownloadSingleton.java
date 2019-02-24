package partlyapp.techpeg.com.partly.Singleton;

import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.Models.Download;
import partlyapp.techpeg.com.partly.Models.Pool;
import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;

public class DownloadSingleton {
    private static final DownloadSingleton ourInstance = new DownloadSingleton();

    public static DownloadSingleton getInstance() {
        return ourInstance;
    }

    private DownloadSingleton() {
    }

    private Download currDownload;

    public Download getCurrentDownload(){
        if(currDownload==null)
            currDownload=new Download();
        return currDownload;
    }

    public Download createNewDownload(String link){
        return new Download(link);
    }

    public void setCurrDownload(Download currDownload) {
        this.currDownload = currDownload;
    }

    public void startDownload() {
        WebSocketHelper socketHelper = new WebSocketHelper();
        String payload = socketHelper.getStringMsg(Constants.KEY_POOL_TOKEN, PoolSingleton.getInstance().getCurrentPool().getPool_token());
        String frame = socketHelper.getFrame(Constants.ACTION_START_DOWNLOAD, payload);
        WebSocketHelper.getWebSocket().sendText(frame);
    }
}
