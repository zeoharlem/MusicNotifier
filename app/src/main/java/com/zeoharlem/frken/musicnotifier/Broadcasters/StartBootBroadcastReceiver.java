package com.zeoharlem.frken.musicnotifier.Broadcasters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.core.content.ContextCompat;

import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.Services.MyRebootAlarmService;
import com.zeoharlem.frken.musicnotifier.Services.MyServiceAlarm;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class StartBootBroadcastReceiver extends BroadcastReceiver {

    ArrayList<MusicFile> musicFilesArrayList    = new ArrayList<>();

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_BOOT_COMPLETED)){
            final Intent serviceIntent    = new Intent(context, Alarm.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            HandlerThread handlerThread = new HandlerThread("database_retry");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    L.l(context, "MusicNotifier Restarts Successfully, Next App time will trigger");
                    getDatabaseAlarmRecordsAction(context, serviceIntent);
                }
            });
        }
    }

    private void getDatabaseAlarmRecordsAction(Context context, Intent intent){
        MediaPlayer mediaPlayer         = null;
        DatabaseHelper myDatabaseHelper = new DatabaseHelper(context);
        musicFilesArrayList             = myDatabaseHelper.getAllMusicFilesRow();
        if(musicFilesArrayList.size() > 0) {
            for (int i = 0; i < musicFilesArrayList.size(); i++) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                String fileSource   = musicFilesArrayList.get(i).getFolderPath();
                String prayerTime   = musicFilesArrayList.get(i).getCreatedAt();
                int requestCode     = musicFilesArrayList.get(i).getIntentReqCode();

                long timeRanger = getSimpleStringDatetoMilli(musicFilesArrayList.get(i));
                int prepForRequestCode = musicFilesArrayList.get(i).getIntentReqCode();

                intent.putExtra("filepath", fileSource);
                intent.putExtra("prayerTime", prayerTime);
                intent.putExtra("requestCode", requestCode);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, prepForRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeRanger, AlarmManager.INTERVAL_DAY, pendingIntent);
                L.l(context, "filesource " + musicFilesArrayList.get(i).getIntentReqCode());
                L.l(context, "prayerTime" + prayerTime);
            }
        }
        //return intent;
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
