package com.zeoharlem.frken.musicnotifier.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.util.ArrayList;
import java.util.Calendar;

public class MyRebootAlarmService extends Service {
    private static final String ACTION_PLAY = "com.example.action.REBOOT_PLAY";
    private static final String CHANNEL_ID  = "MyServiceAlarmNotifier";
    ArrayList<MusicFile> musicFilesArrayList    = new ArrayList<>();
    MediaPlayer mediaPlayer                 = null;
    public MyRebootAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.l(getApplicationContext(), ": Application Started Now");
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

        DatabaseHelper myDatabaseHelper = new DatabaseHelper(getApplicationContext());
        musicFilesArrayList             = myDatabaseHelper.getAllMusicFilesRow();
        if(musicFilesArrayList.size() > 0) {
            Intent serviceAlarmIntent  = getDatabaseAlarmRecordsAction();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceAlarmIntent);
            } else {
                startService(serviceAlarmIntent);
            }
            //stopSelf();
        }
        return START_NOT_STICKY;
    }

    private Intent getDatabaseAlarmRecordsAction(){
        Intent intent = null;
        for (int i = 0; i < musicFilesArrayList.size(); i++) {
            intent   = new Intent(getApplicationContext(), MyServiceAlarm.class);
            AlarmManager alarmManager       = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            String fileSource = musicFilesArrayList.get(i).getFolderPath();
            String prayerTime = musicFilesArrayList.get(i).getCreatedAt();
            int requestCode = musicFilesArrayList.get(i).getIntentReqCode();

            long timeRanger = getSimpleStringDatetoMilli(musicFilesArrayList.get(i));
            int prepForRequestCode = musicFilesArrayList.get(i).getIntentReqCode();

            intent.putExtra("filepath", fileSource);
            intent.putExtra("prayerTime", prayerTime);
            intent.putExtra("requestCode", requestCode);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), prepForRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeRanger, AlarmManager.INTERVAL_DAY, pendingIntent);
            L.l(getApplicationContext(), "filesource " + musicFilesArrayList.get(i).getIntentReqCode());
            L.l(getApplicationContext(), "prayerTime" + prayerTime);
        }
        return intent;
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
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1);
        }
        return calendar.getTimeInMillis();
    }
}
