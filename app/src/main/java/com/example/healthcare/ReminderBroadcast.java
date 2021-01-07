package com.example.healthcare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.example.healthcare.addmedicine.AddMedicineFragment;

import static android.content.Context.VIBRATOR_SERVICE;

public class ReminderBroadcast extends BroadcastReceiver {
    Vibrator mVibrator;

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = intent.getIntExtra("notificationId", 0);

//        Intent mainIntent = new Intent(context, ReminderFragment.class);
//        mainIntent.putExtra(ReminderFragment.EXTRA_ID,notificationId);
//        Intent mainIntent = new Intent(context, MedicineFragment.class);
        Intent mainIntent = new Intent(context, AddMedicineFragment.class);
        //mainIntent.putExtra(ReminderFragment.EXTRA_ID,notificationId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        MediaPlayer mediaPlayer=MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        MediaPlayer mediaPlayer=MediaPlayer.create(context, R.raw.cuco_sound);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();

        mVibrator = (Vibrator)context.getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 5000};
        //mVibrator.vibrate(pattern, 0);
        mVibrator.vibrate(pattern, -1);

        NotificationManager myNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Prepare notification.
        //Notification.Builder builder = new Notification.Builder(context);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context, "My Notification");
        builder.setSmallIcon(R.drawable.ic_info)
                .setContentTitle("It's time for your medication/appointment!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);

        // Notify
        myNotificationManager.notify(notificationId, builder.build());
    }
}
