package com.zeoharlem.frken.musicnotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zeoharlem.frken.musicnotifier.Broadcasters.Alarm;
import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN    = 5000;

    Animation topAnimation, bottomAnimation;
    private Typeface mTypefaceBlack;
    private TextView theNotifier;
    private TextView developerText;
    private ImageView notifierLogo;

    private Handler myHandler;
    private ArrayList<MusicFile> musicFilesArrayList;
    private ProgressBar myProgressBar;
    private TextView checkingRecords;
    private int counterRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        myProgressBar   = findViewById(R.id.progressBar);
        checkingRecords = findViewById(R.id.checkingRecords);

        myHandler       = new Handler();

        topAnimation    = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        setTypefaceTask();

        notifierLogo.setAnimation(topAnimation);
        theNotifier.setAnimation(bottomAnimation);
        developerText.setAnimation(bottomAnimation);

        //Set the database performing action
        performStuff();


        //Call a runnable method
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent   = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, SPLASH_SCREEN);
    }

    private void setTypefaceTask(){
        mTypefaceBlack = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");
        theNotifier    = findViewById(R.id.theNotifier);
        developerText  = findViewById(R.id.developer);
        notifierLogo    = findViewById(R.id.imageView);
        developerText.setTypeface(mTypefaceBlack);
        theNotifier.setTypeface(mTypefaceBlack);
    }

    private void performStuff(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Intent serviceIntent  = new Intent(getApplicationContext(), Alarm.class);
                musicFilesArrayList         = getDatabaseAlarmRecordsAction(MainActivity.this);

                if(musicFilesArrayList.size() > 0) {
                    myProgressBar.setVisibility(View.VISIBLE);
                    checkingRecords.setVisibility(View.VISIBLE);
                    resetAlarmAction(getApplicationContext(), serviceIntent);
                    for (int i = 0; i <= 100; i++) {
                        final int currentProgressCount = i;
                        try {
                            Thread.sleep(50);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                myProgressBar.setProgress(currentProgressCount);
                                counterRow++;
                            }
                        });
                    }
                    if(counterRow == 100){
                        Intent intent   = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    myProgressBar.setVisibility(View.GONE);
                    checkingRecords.setVisibility(View.GONE);
                    Intent intent   = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

    private ArrayList<MusicFile> getDatabaseAlarmRecordsAction(Context context){
        MediaPlayer mediaPlayer         = null;
        DatabaseHelper myDatabaseHelper = new DatabaseHelper(context);
        return myDatabaseHelper.getAllMusicFilesRow();

        //return intent;
    }

    private void resetAlarmAction(Context context, Intent intent){
        if(musicFilesArrayList.size() > 0) {
            for (int i = 0; i < musicFilesArrayList.size(); i++) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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
                //L.l(getApplicationContext(), "Checking Database for Integration");
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
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1);
        }
        return calendar.getTimeInMillis();
    }
}
