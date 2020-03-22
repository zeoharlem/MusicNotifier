package com.zeoharlem.frken.musicnotifier.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.zeoharlem.frken.musicnotifier.Broadcasters.Alarm;
import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.R;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AlarmJobIntentService extends JobIntentService {

    private MediaPlayer mediaPlayer;
    private static final String TAG = "AlarmJobIntentService";
    ArrayList<MusicFile> musicFilesArrayList    = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        L.l(getApplicationContext(), TAG + ": Created Now");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        DatabaseHelper myDatabaseHelper = new DatabaseHelper(getApplicationContext());
        musicFilesArrayList             = myDatabaseHelper.getAllMusicFilesRow();


        for(int i=0; i < musicFilesArrayList.size(); i++){
            Intent alarmIntent              = new Intent(getApplicationContext(), MyServiceAlarm.class);
            AlarmManager alarmManager       = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmIntent.putExtra("filepath", musicFilesArrayList.get(i).getFolderPath());
            alarmIntent.putExtra("prayerTime", musicFilesArrayList.get(i).getCreatedAt());
            alarmIntent.putExtra("requestCode", musicFilesArrayList.get(i).getIntentReqCode());

            long timeRanger             = getSimpleStringDatetoMilli(musicFilesArrayList.get(i));
            int prepForRequestCode      = musicFilesArrayList.get(i).getIntentReqCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), prepForRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeRanger, AlarmManager.INTERVAL_DAY, pendingIntent);
            L.l(getApplicationContext(), "filesource "+musicFilesArrayList.get(i).getIntentReqCode());
            try {
                Thread.sleep(i * 10000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private long getSimpleStringDatetoMilli(MusicFile musicFile){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String rawCreateAt  = musicFile.getCreatedAt();
        String[] splitted   = rawCreateAt.split("\\:");

        int hourDay         = Integer.parseInt(splitted[0]);
        int minuteDay       = Integer.parseInt(splitted[1]);
        int secondsDay      = 0;

        calendar.set(Calendar.HOUR_OF_DAY, hourDay);
        calendar.set(Calendar.MINUTE, minuteDay);
        calendar.set(Calendar.SECOND, secondsDay);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.l(getApplicationContext(), TAG + ": Destroyed Now");
    }

    @Override
    public boolean onStopCurrentWork() {
        return super.onStopCurrentWork();
    }

    public static void enqueueWork(Context context, int jobId, Intent intent){
        enqueueWork(context, AlarmJobIntentService.class, jobId, intent);
    }

    //Not necessary after mediaplayer has duration already
    public static Long getMilliSecondsAudioFile(String mediaPath){
        MediaMetadataRetriever metadataRetriever    = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(mediaPath);
        String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        metadataRetriever.release();
        return Long.parseLong(duration);
    }
}
