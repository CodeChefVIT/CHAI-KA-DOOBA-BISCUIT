package partlyapp.techpeg.com.partly.Helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import partlyapp.techpeg.com.partly.Activities.MainActivity;
import partlyapp.techpeg.com.partly.R;
import partlyapp.techpeg.com.partly.Services.DownloadService;

import static partlyapp.techpeg.com.partly.Constants.Constants.ACTION_APP_EXIT;
import static partlyapp.techpeg.com.partly.Constants.Constants.ACTION_DOWNLOAD_CANCEL;


public class NotificationHelper {

    private static final String TAG = "part.ly";
    android.app.NotificationManager mNotificationManager;
    DownloadService mService;

    public NotificationHelper(DownloadService service) {
        mService = service;

        mNotificationManager =
                (android.app.NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager.getNotificationChannel(mService.getString(R.string.notification_channelId)) == null) {
                CharSequence name = mService.getString(R.string.notification_channelName);
                String description = mService.getString(R.string.notification_channelDesc);
                int importance = android.app.NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(mService.getString(R.string.notification_channelId), name, importance);
                mChannel.setDescription(description);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mChannel.setShowBadge(false);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(
                        new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);
                Log.d(TAG, "createChannel: New channel created");
            } else {
                Log.d(TAG, "createChannel: Existing channel reused");
            }
        }
    }

    private NotificationCompat.Action cancelIntent() {

        Intent intent = new Intent(mService, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_CANCEL);
        PendingIntent pendingIntent = PendingIntent.getService(mService, 11, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action(android.R.id.icon, "cancel", pendingIntent);
        return action;
    }

    private NotificationCompat.Action exitIntent() {
        Intent intent = new Intent(mService, DownloadService.class);
        intent.setAction(ACTION_APP_EXIT);
        PendingIntent pendingIntent = PendingIntent.getService(mService, 13, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action(android.R.id.icon, "Exit", pendingIntent);
        return action;
    }


    public NotificationCompat.Builder buildNotification() {
        createNotificationChannel();
        Intent intent = new Intent(mService, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(mService, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService, mService.getString(R.string.notification_channelId));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder
                    .setColor(
                            mService.getApplication().getResources().getColor(R.color.colorPrimary))
                    .setSmallIcon(android.R.drawable.arrow_down_float)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentTitle(mService.getString(R.string.notification_contentTitle))
                    .setContentText(mService.getString(R.string.notification_contentDesc))
                    .setContentIntent(contentIntent);
        } else {
            notificationBuilder
                    .setColor(
                            mService.getApplication().getResources().getColor(R.color.colorPrimary))
                    .setSmallIcon(android.R.drawable.arrow_down_float)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentTitle(mService.getString(R.string.notification_contentTitle))
                    .setContentText(mService.getString(R.string.notification_contentDesc))
                    .setContentIntent(contentIntent)
                    .addAction(cancelIntent())
                    .addAction(exitIntent());
        }



        return notificationBuilder;

    }
}
