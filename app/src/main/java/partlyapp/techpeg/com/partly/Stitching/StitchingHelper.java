package partlyapp.techpeg.com.partly.Stitching;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import static partlyapp.techpeg.com.partly.Activities.MainActivity.TAG;

public class StitchingHelper {

    public static void stitchFiles(final String newName, boolean hidden, int num, long size) {

        File directory = new File(Environment.getExternalStorageDirectory(), "Partly");
        File fileNew;
        fileNew = new File(directory, newName);

        Log.v("filesName", "newname-" + fileNew.getName());
        FileOutputStream output;
        try {
            File[] fList = null;
            if (hidden) {
                fList = directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        String s = "." + newName.substring(0, newName.lastIndexOf('.')) + "_";
                        return file.getName().startsWith(s);
                    }
                });
            } else {
                fList = directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        String s = newName.substring(0, newName.lastIndexOf('.')) + "_";
                        return file.getName().startsWith(s);
                    }
                });
                sortByNumber(fList);
            }

            if (fList != null && (fList.length == num || num == 0)) {
                output = new FileOutputStream(fileNew, false);

                int f = 0;
                for (File file : fList) {
                    if (file.isFile()) {
                        if (size != 0 && file.length() < size - 1) {
                            Log.d(TAG, "size_less-" + file.length() + " orig_size-" + size);
                            fileNew.delete();
                            f = 1;
                            break;
                        }

                        FileInputStream in = new FileInputStream(file);
                        Log.v("filesstartwith", "name-" + file.getName());
                        byte[] buf = new byte[8192];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            output.write(buf, 0, len);
                        }
                        //output.write(in.read(buffer));
                        in.close();
                        //if (hidden)
                        //file.delete();

                    } else if (file.isDirectory()) {
                        stitchFiles(file.getAbsolutePath(), false, num, size);
                    }
                }
                output.close();
                if (f == 0) {
                    for (File file : fList) {
                        file.delete();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ///if (!hidden) {
          ///  Toast.makeText(getApplicationContext(), "Stitching completed", Toast.LENGTH_SHORT).show();
        ///}
    }

    private static void sortByNumber(File[] files) {
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {

                long n1 = extractNumber(o1.getName());
                long n2 = extractNumber(o2.getName());
                Log.d("sortBynumber", "n1-" + n1 + " n2-" + n2);
                return (int) (n1 - n2);
            }


            private long extractNumber(String name) {
                long i = 0;
                try {
                    int s = name.lastIndexOf('_') + 1;
                    int e = name.lastIndexOf('-');
                    String number = name.substring(s, e);
                    Log.d("sortBynumber", "number-" + number);
                    i = Long.parseLong(number);
                } catch (Exception e) {
                    i = 0; // if filename does not match the format
                    // then default to 0
                }
                return i;
            }
        });

        for (File f : files) {
            Log.i("filessort", f.getName());
        }

    }
}
