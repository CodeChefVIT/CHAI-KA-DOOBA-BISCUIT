package partlyapp.techpeg.com.partly.Downloads;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static partlyapp.techpeg.com.partly.Activities.MainActivity.TAG;


public class DownloadTask implements Callable<Boolean> {
    String filename;
    long total_size;
    long fileStart;
    InputStream in;
    int progress;
    long downloaded;
    int partNum;
    Progress prog;
    CountDownLatch latch;
    //Ranges ranges;
    int ind;

    public DownloadTask(InputStream in, String filename, long fileStart, long size, Progress prog, CountDownLatch latch) {
        this.in = in;
        this.filename = filename;
        total_size = size;
        this.prog = prog;
        this.prog.setTotalFileSize(size);
        this.latch = latch;
        this.fileStart = fileStart;
        //this.ranges = ranges;
        //ind = ranges.getStartRanges().size();

    }

    /*
    @Override
    public void run() {
        String msg;

        if (downloadFile(in)) {
            msg = "file downloaded successful ";
        } else {
            msg = "failed to download the file ";
        }
        Log.v("Part.ly", msg);

    }
    */

    public boolean downloadFile(InputStream inputStream) {
        boolean err = false;
        long starttime = System.currentTimeMillis();
        FileOutputStream output = null;
        RandomAccessFile raf = null;
        try {

            File downloadPath = new File(Environment.getExternalStorageDirectory(), "Partly");
            if (!downloadPath.exists()) {
                if (downloadPath.mkdirs()) {
                    Log.d(TAG, "path creation failed");
                }
            }
            File file = new File(downloadPath, filename);

            raf = new RandomAccessFile(file, "rw");

            output = new FileOutputStream(file, true);
            byte[] buffer = new byte[8192]; // or other buffer size Math.max(262144,(int)total_size/500)

            int read;

            //download.setTotalFileSize(total_size);
            long downloaded_size = 0;
            int timeCount = 1;

            long startTime = System.currentTimeMillis();
            Log.d(TAG, "Attempting to write to: Downloads/" + "Partly" + "/" + filename);
            while ((read = inputStream.read(buffer)) != -1) {

                if (Thread.interrupted()) {
                    Log.d(TAG, "service_thread_interrupt");

                    ///if (file.exists())
                    /// file.delete();

                    break;
                }
                downloaded_size += read;
                long currentTime = System.currentTimeMillis() - startTime;
                /*
                if(downloaded_size>total_size){
                    downloaded_size=total_size;
                }
                */

                //progress=(int)(100*downloaded_size/total_size);
                double current = downloaded_size;
                downloaded = (long) current;

                prog.setCurrentFileSize((long) current);
                // download.setProgress(progress);
                //Log.d("threadprogress- ", (downloaded_size / (1024)) + "KB/" + total_size / (1024) + "KB");
                //Log.d("threadprogress- ", "val-" + progress);

                if (currentTime > 1000 * timeCount) {


                    //publishProgress(download);
                    //sendNotification(download);
                    timeCount++;
                }
                ////output.write(buffer, 0, read);

                synchronized (raf) {
                    raf.seek(fileStart + downloaded_size - read);
                    raf.write(buffer, 0, read);

                    /*
                    if(ranges.getEndRanges().contains(fileStart+downloaded_size-read)) {
                        int ind=ranges.getStartRanges().indexOf(fileStart+downloaded_size);
                        ranges.getEndRanges().set(ind,fileStart+downloaded_size);
                    }else {
                        ranges.getStartRanges().add(fileStart + downloaded_size - read);
                        ranges.getEndRanges().add(fileStart + downloaded_size);
                    }
                    */

                    /*
                    if (downloaded_size - read == 0) {
                        ranges.getStartRanges().add(fileStart + downloaded_size - read);
                        ranges.getEndRanges().add(fileStart + downloaded_size);
                    } else
                        ranges.getEndRanges().set(ind, fileStart + downloaded_size);
                    //Constants.ranges_start.add();
                    // Constants.ranges_end.add();
                    AppService.getInstance().updateTotal(read);
                    */

                }


            }
            Log.d("download_size", "val-" + downloaded_size + " total-" + total_size);
            Log.d(TAG, "Flushing output stream.");
            output.flush();
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
            err = true;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Output stream closed sucessfully.");
            }
            long endtime = System.currentTimeMillis();
            Log.i("timetaken-", "val-" + (endtime - starttime) / 1000);
            latch.countDown();

        }
        Log.d(TAG, "res return from dt-" + !err + "filename-" + filename);
        return !err;
    }

    @Override
    public Boolean call() throws Exception {
        return downloadFile(in);

    }

}