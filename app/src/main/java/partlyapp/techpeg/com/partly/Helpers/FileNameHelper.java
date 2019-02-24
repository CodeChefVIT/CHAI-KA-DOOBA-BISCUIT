package partlyapp.techpeg.com.partly.Helpers;

import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.Headers;
import partlyapp.techpeg.com.partly.Retrofit.RestAPI;
import partlyapp.techpeg.com.partly.Retrofit.RestAPIClient;

import static partlyapp.techpeg.com.partly.Activities.MainActivity.TAG;

public class FileNameHelper {

    public static String getFileName(String url) {

        String filename;
        try {
            RestAPI service = RestAPIClient.getClient(url);
            Headers headers = service.getHeaders(url).execute().headers();
            String disposition = headers.get("Content-Disposition");
            String mimetype = headers.get("Content-Type");
            filename = URLUtil.guessFileName(url, disposition, mimetype);
            filename = URLDecoder.decode(filename, "utf-8");
            Log.d("filenamedis", "dis-" + disposition + " mime-" + mimetype + " name-" + filename);
        } catch (IOException e) {
            e.printStackTrace();

            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }

            String names[] = url.split("/");
            String original_filename = names[names.length - 1];
            int index = original_filename.lastIndexOf('.');
            String ext = original_filename.substring(index);
            String new_name = original_filename.substring(0, index);
            filename = new_name + "_Partly" + ext;
            Log.d(TAG, "getfilename-" + filename);
        }
        return filename;
    }

    public static String getOriginalName(String name) {
        int index = name.lastIndexOf('.');
        String ext = name.substring(index);
        String new_name = name.substring(0, name.lastIndexOf('_'));
        return new_name + ext;
    }

    public static String getCustomFilename(String name, String append) {
        int index = name.lastIndexOf('.');
        String ext = name.substring(index);
        String new_name = name.substring(0, index);
        String newName = new_name + append + ext;
        Log.v("customfilename", "name-" + newName);
        return newName;
    }

    public static long fileExists(String filename) {
        String appDirectoryName = "Partly";
        File downloadPath = new File(Environment.getExternalStorageDirectory(), appDirectoryName);
        File file = new File(downloadPath, filename);
        if (file.exists()) {
            return file.length();
        }
        return -1;
    }
}
