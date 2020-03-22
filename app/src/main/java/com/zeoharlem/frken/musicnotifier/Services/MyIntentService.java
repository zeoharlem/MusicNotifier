package com.zeoharlem.frken.musicnotifier.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.annotation.Nullable;

import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.io.IOException;
import java.util.Objects;

public class MyIntentService extends IntentService implements MediaPlayer.OnPreparedListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String fileSource   = Objects.requireNonNull(intent).getStringExtra("filepath");
        Vibrator vibrator   = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
        Uri myUri           = Uri.parse(fileSource);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
            L.l(getApplicationContext(), "SertviceItent "+intent.getStringExtra("prayerTime"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
