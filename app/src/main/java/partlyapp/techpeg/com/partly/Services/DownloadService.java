package partlyapp.techpeg.com.partly.Services;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.Downloads.DownloadTask;
import partlyapp.techpeg.com.partly.Downloads.Progress;
import partlyapp.techpeg.com.partly.Helpers.FileNameHelper;
import partlyapp.techpeg.com.partly.Helpers.NotificationHelper;
import partlyapp.techpeg.com.partly.Models.Download;
import partlyapp.techpeg.com.partly.Retrofit.RestAPI;
import partlyapp.techpeg.com.partly.Retrofit.RestAPIClient;
import partlyapp.techpeg.com.partly.Singleton.DownloadSingleton;
import partlyapp.techpeg.com.partly.Stitching.StitchingHelper;
import partlyapp.techpeg.com.partly.WebSocket.WebSocketHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static partlyapp.techpeg.com.partly.Activities.MainActivity.TAG;
import static partlyapp.techpeg.com.partly.Constants.Constants.NOTIFICATION_ID;
import static partlyapp.techpeg.com.partly.Constants.Constants.network_connection;
import static partlyapp.techpeg.com.partly.Helpers.FileNameHelper.fileExists;

public class DownloadService extends Service {

    ArrayList<Future> futureArrayList = new ArrayList<>();
    private ExecutorService threadPool;
    private WebSocketHelper socketHelper;
    public WebSocket webSocket;
    Thread jobThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service","on create");
        socketHelper=new WebSocketHelper();
        webSocket=WebSocketHelper.getWebSocket();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationHelper notificationHelper = new NotificationHelper(this);
        NotificationCompat.Builder notificationBuilder = notificationHelper.buildNotification();
        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }



    public void initializeJob(final long jobId, final long start, final long end, final String url) {
        jobThread = new Thread(new Runnable() {
            @Override
            public void run() {

                socketHelper=new WebSocketHelper();
                webSocket=WebSocketHelper.getWebSocket();

                String fileName = FileNameHelper.getFileName(url);

                DownloadSingleton.getInstance().getCurrentDownload().setFileName(fileName);
                String file_name = FileNameHelper.getCustomFilename(fileName, "_" + (start) + "-" + end);

                File directory = new File(Environment.getExternalStorageDirectory(), "Partly");
                File file = new File(directory, file_name);


                ////pool.getJobsMap().put(jobId, file);
                if (FileNameHelper.fileExists(file_name) < end - start) {
                    int res = -1;
                    while (res == -1) {
                        Log.d(TAG, "retrying");
                        if (network_connection) {
                            //result_recursive = 0;
                            Log.d(TAG, "starting job- " + jobId);
                        /*
                        if (end - start < 512 * 1024)
                            res = recursiveRange(url, start, end, 1, 2, "", fileName);
                        else if (end - start < 2 * 1024 * 1024)
                            res = recursiveRange(url, start, end, 1, 4, "", fileName);
                        else if (end - start < 5 * 1024 * 1024)
                            res = recursiveRange(url, start, end, 2, 4, "", fileName);
                        else
                            res = recursiveRange(url, start, end, 8, 8, "", fileName);
                            */
                            res = downloadRange(url, "", start, end, 8, file_name);
                            //Log.d("initializejob end","res-"+res);
                        }
                    }
                }

                /// Constants.currentPool.setCurrent_jobId(0);
                ///current_jobId = 0;


                //stitchFiles(fileName, false);

                if (webSocket != null) {
                    Log.d("socket","not null");
                    if (webSocket.getState() == WebSocketState.CLOSED) {
                        Log.d("socket","closed");
                        socketHelper.connectSocket();
                    }
                    String payload = socketHelper.getStringMsg(new String[]{Constants.KEY_JOBID
                                    , Constants.KEY_RANGE_START,
                                    Constants.KEY_RANGE_END
                                    , Constants.KEY_URL},
                            new Object[]{jobId, start, end, url});
                    String frame = socketHelper.getFrame(Constants.ACTION_JOB_FINISHED, payload);
                    Log.d("websocket", "sending job finish frame- " + frame);
                    webSocket.sendText(frame);
                }else{
                    Log.d("socket","null");
                }
            }
        });
        jobThread.start();

    }

    public int downloadRange(String url, String base64encoded, long start, long end, int num, String name) {
        if (start != 0)
            start += 1;
        int res = 0;
        RestAPI service = RestAPIClient.getClient(url);
        ArrayList<Long> parts = new ArrayList<>();
        parts.add(start);
        long part = (end - start) / num;
        for (int i = 1; i < num; i++) {
            parts.add(start + i * part);
        }
        parts.add(end);
        Log.v("downloading", "parts-" + parts.toString());
        threadPool = Executors.newFixedThreadPool(num);
        long starttime = System.currentTimeMillis();
        futureArrayList.clear();
        long fileStart = 0;
        //Ranges ranges = new Ranges();
        //rangesArrayList.add(ranges);
        //ranges.setFileName(name);
        CountDownLatch latch = new CountDownLatch(num);
        for (int i = 0; i < num; i++) {

            try {
                String file_name = "." + FileNameHelper.getCustomFilename(name, "_part" + (i + 1));
                file_name = name;


                /***
                 if (fileExists(file_name) != -1) {
                 Log.d("fileexists", "file-" + file_name + "size-" + fileExists(file_name) + "parts-" + parts.toString());

                 parts.set(i, parts.get(i) + fileExists(file_name));
                 Log.d("fileexists", "parts after-" + parts.toString());
                 }
                 ***/

                /*
                if (fileExists(file_name) != -1) {
                    Pool lastPool = Constants.currentPool;
                    for (int l = 0; l < lastPool.getRangesArrayList().size(); l++) {
                        if (lastPool.getRangesArrayList().get(l).getFileName().equals(file_name)) {
                            Log.d("fileexists", "file-" + file_name + "size-" + fileExists(file_name) + "parts-" + parts.toString());
                            ArrayList<Long> startRange = lastPool.getRangesArrayList().get(l).getStartRanges();
                            ArrayList<Long> endRange = lastPool.getRangesArrayList().get(l).getEndRanges();
                            if (endRange.size() > i) {
                                Log.d("fileexists", "start-" + startRange.toString() + " \nend-" + endRange.toString());
                                parts.set(i, parts.get(i) + endRange.get(i) - startRange.get(i));
                                fileStart += endRange.get(i) - startRange.get(i);
                                Log.d("fileexists", "parts after-" + parts.toString());
                            }
                        }
                    }
                }
                */

                String range = null;
                long fileSize = 0;
                if (parts.get(i) >= (parts.get(i + 1))) {
                    latch.countDown();
                    continue;
                }
                if (i == 0) {
                    range = String.valueOf(parts.get(i)) + "-" + String.valueOf(parts.get(i + 1));
                    fileSize = parts.get(i + 1) - parts.get(i);
                    Log.d(TAG, "filesizeif-" + fileSize);
                } else {
                    range = String.valueOf(parts.get(i) + 1) + "-" + String.valueOf(parts.get(i + 1));
                    fileSize = parts.get(i + 1) - parts.get(i);
                    Log.d(TAG, "filesizeelse-" + fileSize);

                }
                Log.d("file-downloading", "filename-" + file_name + " range-" + range + " filesize-" + fileSize + " filestart-" + fileStart);

                ResponseBody response = null;



                if (fileSize > 0) {
                    if (base64encoded.isEmpty()) {
                        try {

                            /*

                            Call<ResponseBody> call = service.downloadFile(url,"","bytes="+range);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Log.d("download_range",  "call-"+call+"response-"+response);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.d("download_range",  "response-"+t.getMessage());
                                }
                            });
                            */

                                    //String headers = service.downloadFile(url, "bytes=" + range).execute().headers().toString();
                            //Log.d("headers-", "val-" + headers + " request-" + service.downloadFile(url,  "bytes=" + range).request().headers().toString());
                            response = service.downloadFile(url,  "","bytes=" + range).execute().body();
                            Log.d("download_range", "val-" + range+" response-"+response);
                        } catch (Exception e) {
                            Log.e(TAG, "response err-" + e.getMessage() + "range-" + range);
                            e.printStackTrace();
                            res = -1;
                        }
                    } else {
                        response = service.downloadFileWithAuth(url, "bytes=" + range, "Basic " + base64encoded).execute().body();
                    }
                }


                Progress progress = new Progress();
                if (fileSize <= 0) {
                    Log.v("Part.ly", "file size 0");
                    progress.setCurrentFileSize(fileSize);
                }
                //downloadArrayList.add(download);


                if (response != null) {

                    final DownloadTask downloadTask = new DownloadTask(response.byteStream(), file_name, fileStart, fileSize, progress, latch);


                    Future future = threadPool.submit(downloadTask);
                    futureArrayList.add(future);


                } else {
                    latch.countDown();
                }

                fileStart += fileSize;
                if (i == 0)
                    fileStart += 1;

                //handler();
            } catch (Exception e) {
                Log.e("download_error", e.getMessage());
                e.printStackTrace();
                res = -1;
            }


        }

        try {
            Log.d(TAG, "latch_countdown:" + latch.getCount());
            latch.await();
            long endtime = System.currentTimeMillis();
            //calculateRanges(ranges);
            for (int j = 0; j < futureArrayList.size(); j++) {
                try {
                    if (!futureArrayList.get(j).isCancelled()) {
                        if (futureArrayList.get(j).get() != null && futureArrayList.get(j).get().equals(false)) {
                            Log.d(TAG, "future get false");
                            res = -1;
                        }
                    }
                } catch (ExecutionException e) {
                    Log.e(TAG, "future err-" + e.getMessage());
                    e.printStackTrace();
                }
            }
            Log.i("totaltime-", "threads-" + num + " val-" + (endtime - starttime) / 1000 + "s");
        } catch (InterruptedException E) {
            E.printStackTrace();
            // handle
        } finally {
            StitchingHelper.stitchFiles(name, true, num, part);
        }

        /*
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            long endtime = System.currentTimeMillis();
            stitchFiles(name, true);
            Log.i("totaltime-", "threads-" + num + " val-" + (endtime - starttime) / 1000 + "s");
            threadPool = null;

        } catch (InterruptedException e) {
            Log.e(TAG,"threadPool termination error-"+e.getMessage());
            e.printStackTrace();
        }
        */
        Log.d(TAG, "res_return from down-range" + res + "filename-" + name);
        return res;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (threadPool != null) {
            threadPool.shutdownNow();

            for (int i = 0; i < futureArrayList.size(); i++) {
                futureArrayList.get(i).cancel(true);
            }
            //threadPool.shutdownNow();

            if (!threadPool.isTerminated()) {
                Log.d(TAG, "service_pool_not_terminated");
                try {
                    threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "service_pool_terminated");
                }
            }
        }
    }
}
