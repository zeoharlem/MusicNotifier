package com.zeoharlem.frken.musicnotifier.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;

import com.zeoharlem.frken.musicnotifier.R;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.io.IOException;
import java.util.Objects;

public class MyServiceAlarm extends Service{
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String CHANNEL_ID  = "MyServiceAlarmNotifier";
    MediaPlayer mediaPlayer                 = null;

    public MyServiceAlarm() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.l(getApplicationContext(), ": Destroyed Now");
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Notification notification;
        String fileSource   = Objects.requireNonNull(intent).getStringExtra("filepath");
        String prayerTime   = intent.getStringExtra("prayerTime");
        Vibrator vibrator   = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle("Praying at "+prayerTime)
                    .setContentText("A call to prayer made")
                    .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .build();
        }
        else{
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Praying at "+prayerTime)
                    .setContentText("A call to prayer made")
                    .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build();
        }

        final NotificationManager notificationManager   = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //notification.flags          = Notification.FLAG_AUTO_CANCEL;
        //manager.notify(1212, notification);

        Uri myUri                   = Uri.parse(fileSource);
        mediaPlayer                 = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    L.l(getApplicationContext(), "Audio File Completed");
                    //mp.release();
                    notificationManager.cancelAll();
                }
            });
            mediaPlayer.start();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        startForeground(1021, notification);
        return START_REDELIVER_INTENT;
    }

}
