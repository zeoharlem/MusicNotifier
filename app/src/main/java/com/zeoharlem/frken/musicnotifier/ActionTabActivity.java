package com.zeoharlem.frken.musicnotifier;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeoharlem.frken.musicnotifier.Adapters.MainPageRecyclerAdapter;
import com.zeoharlem.frken.musicnotifier.Broadcasters.Alarm;
import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.DialogBox.MyFragmentDialogBox;
import com.zeoharlem.frken.musicnotifier.DialogBox.MyLoadingAlertDialogFrag;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.Services.AlarmJobIntentService;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ActionTabActivity extends AppCompatActivity implements View.OnClickListener, MyFragmentDialogBox.MyDialogBoxCallBackListener, MainPageRecyclerAdapter.ImainPageRecyclerAdapter {
    private static final int REQ_CODE_PICK_SOUND_FILE = 1001;
    private static final int REQ_CODE_PICK_SOUND_FILE_3 = 3001;
    private static final int REQ_CODE_PICK_SOUND_FILE_6 = 6001;
    private static final int UPDATE_REQUEST_CODE = 2001;

    Typeface mTypeface, mTypefaceBold, mTypefaceBlack;
    Button setDayAlarm, threeOButton;
    LinearLayout twelveOButton;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;
    private DatabaseHelper myDatabaseHelper;
    DatePickerDialog datePickerDialog;
    private MusicFile musicFile;

    RecyclerView recyclerView;
    MainPageRecyclerAdapter recyclerAdapter;
    ArrayList<MusicFile> musicFileArrayList;
    private Button setAlarmForAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_tab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Set Up");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDatabaseHelper            = new DatabaseHelper(this);
        myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();

        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "tabActivityBox");
        //Volley Request should be made here instead of the dismiss() callback
        myLoadingAlertDialogFrag.callAlertLoadingTaskCallback(new MyLoadingAlertDialogFrag.AlertLoadingTaskCallback() {
            @Override
            public void CallbackTask(final MyLoadingAlertDialogFrag myLoadingAlertDialogFrag) {
                myLoadingAlertDialogFrag.dismiss();
                musicFileArrayList  = myDatabaseHelper.getAllMusicFilesRow();
                //Set Contents for the recyclerview
                setRecyclerViewAdapterRow();
            }
        });

        setTypefaceButton();
        //twelveOButton.setOnClickListener(this);
        //threeOButton.setOnClickListener(this);
        //setDayAlarm Button is for 6 oclock
        setDayAlarm.setOnClickListener(this);

        LinearLayout closeApp = findViewById(R.id.closeAppNow);
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    private void setRecyclerViewAdapterRow(){
        recyclerView                        = findViewById(R.id.filesRecyclerView);
        LinearLayoutManager layoutManager   = new LinearLayoutManager(this);
        DividerItemDecoration decoration    = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerAdapter                     = new MainPageRecyclerAdapter(musicFileArrayList, this);

        //Set OnItemEditClick interface
        recyclerAdapter.setImainPageRecyclerAdapter(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(recyclerAdapter);
    }

    //twelveOclockButton and SixOclockButton will use the same file
    private void sixOclockButton(String timeSetter){
        setPrefTokenTimeSetter(timeSetter);
        Intent intent   = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQ_CODE_PICK_SOUND_FILE_6);
    }

    private void clickRemoveClearDtbase(){
        myDatabaseHelper.deleteQueryFilesRow();
        recyclerAdapter.getMusicFileArrayList().clear();
        recyclerAdapter.notifyDataSetChanged();
    }

    private void setPrefTokenTimeSetter(String timeSetter){
        SharedPreferences preferences   = getSharedPreferences("timeSetterPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("timeSetter", timeSetter);
        editor.apply();
    }

    private String getPrefTokenTimeSetter(){
        SharedPreferences preferences   = getSharedPreferences("timeSetterPref", MODE_PRIVATE);
        return preferences.getString("timeSetter", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQ_CODE_PICK_SOUND_FILE_6 && resultCode == RESULT_OK){
            //set for 12 oclock alarm
            startTriggerAlarm(data);
        }
        else if(requestCode == UPDATE_REQUEST_CODE && resultCode == RESULT_OK){
            //Update Key Tablerow with the new time
            if(myDatabaseHelper.updateFileRowTable(musicFile) > 0){
                recyclerAdapter.notifyDataSetChanged();
                Uri uri = Objects.requireNonNull(data).getData();
                createFileProcess(uri, musicFile.getCreatedAt());
                Intent intent               = new Intent(ActionTabActivity.this, Alarm.class);
                intent.putExtra("filepath", musicFile.getFolderPath());
                intent.putExtra("prayerTime", musicFile.getCreatedAt());

                long timeRanger             = getSimpleStringDatetoMilli(musicFile);

                AlarmManager alarmManager   = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(ActionTabActivity.this, musicFile.getIntentReqCode(), intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();

                pendingIntent               = PendingIntent.getBroadcast(ActionTabActivity.this, musicFile.getIntentReqCode(), intent, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeRanger, pendingIntent);
                }
                else{
                    L.l(getApplicationContext(), "Updated Action: "+getPrefTokenTimeSetter()+"=filesource"+musicFile.getFolderPath());
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeRanger, AlarmManager.INTERVAL_DAY, pendingIntent);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startTriggerAlarm(Intent data){
        //L.l(getApplicationContext(),getPrefTokenTimeSetter());
        Uri uri = Objects.requireNonNull(data).getData();
        createFileProcess(uri, getPrefTokenTimeSetter());

        //Kindly Refactor. COuld get the adapater arraylist without setting
        musicFileArrayList  = myDatabaseHelper.getAllMusicFilesRow();
        recyclerAdapter.setMusicFileArrayList(myDatabaseHelper.getAllMusicFilesRow());
        recyclerAdapter.notifyDataSetChanged();
        if(recyclerAdapter.getItemCount() > 0){
            Intent intent               = new Intent(ActionTabActivity.this, Alarm.class);
            intent.putExtra("filepath", musicFile.getFolderPath());
            intent.putExtra("prayerTime", getPrefTokenTimeSetter());
            //intent.setAction("MN_ACTION_"+musicFile.getId());

            //int prepForRequestCode      = Integer.parseInt(musicFile.getId());//
            String filesRemoveString    = musicFile.getFolderPath().substring(
                    musicFile.getFolderPath().lastIndexOf("-")+1,
                    musicFile.getFolderPath().lastIndexOf("."));

            int prepForRequestCode  = musicFile.getIntentReqCode();

            intent.putExtra("requestCode", prepForRequestCode);

            //L.l(getApplicationContext(), String.valueOf(prepForRequestCode));

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            long timeRanger             = getSimpleStringDatetoMilli(musicFile);

            AlarmManager alarmManager   = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ActionTabActivity.this, prepForRequestCode, intent,0);

            //AlarmJobIntentService.enqueueWork(ActionTabActivity.this, prepForRequestCode, intent);

            L.l(getApplicationContext(), "time taken"+getPrefTokenTimeSetter()+"=filesource"+musicFile.getFolderPath());
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeRanger, AlarmManager.INTERVAL_DAY, pendingIntent);

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

    private boolean setMusicFilesRow(String ct, String fn, String fp, int iReq){
        musicFile   = new MusicFile();
        musicFile.setCreatedAt(ct);
        musicFile.setFilename(fn);
        musicFile.setFolderPath(fp);
        musicFile.setStatus("active");
        musicFile.setIntentReqCode(iReq);
        if(myDatabaseHelper.getMusicFileTableCount(ct) == 0) {
            return myDatabaseHelper.insertMusicFiles(musicFile);
        }
        return false;
    }

    /**
     * On the 12 o'clock click
     * run this method and the file is renamed
     * @param uri
     */
    private void createFileProcess(Uri uri, String timer){
        String uriString    = getFileName(uri);
        long systemMilli    = getSystemMilli();
        File root           = Environment.getExternalStorageDirectory();
        String getFolder    = getIntent().getStringExtra("folderName");
        String getExt       = uriString.substring(uriString.lastIndexOf("."));
        String destiString  = root+"/MusicNotifier/"+getFolder+"/MN-AUDIO-"+systemMilli+getExt;
        File sourceRow      = new File(getRealPathFromURI(this, uri));
        File destination    = new File(destiString);
        try {
            copyFileAct(sourceRow, destination);
            String cutSystemMilli   = String.valueOf(systemMilli).substring(5);
            if(!setMusicFilesRow(timer, getFolder, destiString, Integer.parseInt(cutSystemMilli))){
                throw new Exception("Database cannot be uploaded");
            }
            L.l(getApplicationContext(), "confirm reqcode: "+Integer.parseInt(cutSystemMilli));
        }
        catch (Exception e) {
            e.printStackTrace();
            L.l(getApplicationContext(), e.getMessage());
        }
    }

    /**
     * Get the real path to the URI
     * @param context this
     * @param contentUri Uri
     * @return String
     */
    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Copy a file to another destination
     * @param src File
     * @param dst File
     * @throws IOException
     */
    private void copyFileAct(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /**
     * get the name of the file chosen using the intent method
     * @param uri Uri
     * @return String
     */
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //Set the buttn and the edittext fields
    private void setTypefaceButton(){
        mTypefaceBlack  = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Black.ttf");
        mTypefaceBold   = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");
        TextView fName  = findViewById(R.id.activities);
        TextView topText= findViewById(R.id.totalText);
        String nFolder  = getIntent().getStringExtra("folderName");
        String tStorage = getIntent().getStringExtra("storageType");
        setDayAlarm     = findViewById(R.id.sixOButton);
        //twelveOButton   = findViewById(R.id.twelveOButton);
//        threeOButton    = findViewById(R.id.threeOButton);


        fName.setText(nFolder);
        topText.setText(tStorage);

        //SetDayAlarm is for 6 oclock
        setDayAlarm.setTypeface(mTypefaceBlack);
        //twelveOButton.setTypeface(mTypefaceBlack);
//        threeOButton.setTypeface(mTypefaceBlack);


        fName.setTypeface(mTypefaceBold);
        topText.setTypeface(mTypefaceBold);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.twelveOButton:
//                showDialogBox(v);
//                break;
//            case R.id.threeOButton:
//                threeOclockButton();
//                break;
            case R.id.sixOButton:
                timePickerShow();
                break;
        }
    }

    private void timePickerShow(){
        TimePickerDialog mTimePicker;
        Calendar mcurrentTime   = Calendar.getInstance();
        int hour                = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute              = mcurrentTime.get(Calendar.MINUTE);

        mTimePicker = new TimePickerDialog(ActionTabActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                sixOclockButton(selectedHour + ":" + selectedMinute);
                //L.l(getApplicationContext(), selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void showDialogBox(View view){
        MyFragmentDialogBox dialogBox   = new MyFragmentDialogBox();
        dialogBox.show(getSupportFragmentManager(), "myDialogBox");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            Intent intent = new Intent(this, MainPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        else if(id == R.id.menu_main_add){
            timePickerShow();
        }
        else if(id == R.id.menu_main_close_app){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private String getDateTime() {
        Date date                   = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(date);
    }

    private long getSystemMilli(){
        return System.currentTimeMillis();
    }

    @Override
    public void onDialogMessageClick(String message) {
        if(message.equalsIgnoreCase("Yes")){
            clickRemoveClearDtbase();

            Intent intent               = new Intent(ActionTabActivity.this, Alarm.class);
            AlarmManager alarmManager   = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ActionTabActivity.this, 1005, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            //pendingIntent.cancel();
        }
    }

    @Override
    public void onItemEditClick(int position) {
        Calendar mcurrentTime   = Calendar.getInstance();
        int hour                = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute              = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog   = new TimePickerDialog(ActionTabActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Intent intent   = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Update Audio"), UPDATE_REQUEST_CODE);
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Update Alarm Time");
        timePickerDialog.show();
        //L.l(getApplicationContext(), "Item position: "+position);
    }
}
