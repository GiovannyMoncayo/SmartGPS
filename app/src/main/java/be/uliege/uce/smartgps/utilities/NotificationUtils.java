package be.uliege.uce.smartgps.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import be.uliege.uce.smartgps.R;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "ajvillafuerte.channel";
    public static final String ANDROID_CHANNEL_NAME = "AJVILLAFUERTE CHANNEL";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {

        // create android channel
        NotificationChannel androidChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            getManager().createNotificationChannel(androidChannel);

        }

    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public Notification.Builder getAndroidChannelNotification(String title, String body, Class<?> classOpen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setAutoCancel(true);
        }else{
            NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(getApplicationContext())
                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), classOpen), 0))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setVibrate(new long[] {100, 250, 100, 500})
                    .setAutoCancel(true);

            NotificationManager mNotifyMgr =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(1, mBuilder.build());
        }
        return null;
    }

}