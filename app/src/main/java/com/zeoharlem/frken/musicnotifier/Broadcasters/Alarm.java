package com.zeoharlem.frken.musicnotifier.Broadcasters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.zeoharlem.frken.musicnotifier.R;
import com.zeoharlem.frken.musicnotifier.Services.AlarmJobIntentService;
import com.zeoharlem.frken.musicnotifier.Services.MyIntentService;
import com.zeoharlem.frken.musicnotifier.Services.MyServiceAlarm;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String prayerTime   = intent.getStringExtra("prayerTime");
        int requestCode     = intent.getIntExtra("requestCode", 0);
        String fileSource   = Objects.requireNonNull(intent).getStringExtra("filepath");
        Vibrator vibrator   = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);

        Intent serviceIntent    = new Intent(context, MyServiceAlarm.class);
        serviceIntent.putExtra("prayerTime", prayerTime);
        serviceIntent.putExtra("requestCode", requestCode);
        serviceIntent.putExtra("filepath", fileSource);

        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
