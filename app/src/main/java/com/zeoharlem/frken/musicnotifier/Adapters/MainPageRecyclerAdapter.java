package com.zeoharlem.frken.musicnotifier.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeoharlem.frken.musicnotifier.ActionTabActivity;
import com.zeoharlem.frken.musicnotifier.Broadcasters.Alarm;
import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.R;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class MainPageRecyclerAdapter extends RecyclerView.Adapter<MainPageRecyclerAdapter.MainPageViewHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<MusicFile> musicFileArrayList;
    DatabaseHelper myDatabaseHelper;
    private Context context;
    ImainPageRecyclerAdapter imainPageRecyclerAdapter;

    public MainPageRecyclerAdapter(ArrayList<MusicFile> musicFileArrayList, Context context) {
        this.musicFileArrayList = musicFileArrayList;
        this.context            = context;
        layoutInflater          = LayoutInflater.from(context);
        myDatabaseHelper        = new DatabaseHelper(context);
    }

    public ImainPageRecyclerAdapter getImainPageRecyclerAdapter() {
        return imainPageRecyclerAdapter;
    }

    public void setImainPageRecyclerAdapter(ImainPageRecyclerAdapter imainPageRecyclerAdapter) {
        this.imainPageRecyclerAdapter   = imainPageRecyclerAdapter;
    }

    @NonNull
    @Override
    public MainPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = layoutInflater.inflate(R.layout.item_rows, parent, false);
        return new MainPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainPageViewHolder holder, final int position) {
        final MusicFile musicFile   = musicFileArrayList.get(position);
        final String nFileName      = musicFile.getFolderPath().substring(musicFile.getFolderPath().lastIndexOf("/")+1);
        String prayerMsgTimer       = "Prayer & Message";

        holder.timerAlarm.setText(prayerMsgTimer);
        holder.mainAlarm.setText(musicFile.getCreatedAt());
        holder.filename.setText(nFileName);

        holder.triggerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent               = new Intent(context, Alarm.class);
                intent.putExtra("filepath", musicFile.getFolderPath());
                intent.putExtra("prayerTime", getPrefTokenTimeSetter());

                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                if(myDatabaseHelper.deleteAfilesRow(musicFile.getId()) > 0) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, musicFile.getIntentReqCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    //pendingIntent.cancel();

                    L.l(context, "Delete Alarm " + musicFile.getIntentReqCode());
                    musicFileArrayList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });

        //change the audio file assigned to the alarm
//        holder.editMusic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                L.l(context, "Edit Audio Alarm "+musicFile.getFolderPath());
//            }
//        });
    }

    private String getPrefTokenTimeSetter(){
        SharedPreferences preferences   = context.getSharedPreferences("timeSetterPref", MODE_PRIVATE);
        return preferences.getString("timeSetter", "");
    }

    @Override
    public int getItemCount() {
        return musicFileArrayList.size();
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

    private long getSimpleStringDatetoMilli(String timeString){
        long timeMilliSeconds = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        try {
            Date rowTimeDateType    = dateFormat.parse(timeString);
            timeMilliSeconds        = rowTimeDateType.getTime();
            return timeMilliSeconds;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMilliSeconds;
    }

    public ArrayList<MusicFile> getMusicFileArrayList() {
        return musicFileArrayList;
    }

    public void setMusicFileArrayList(ArrayList<MusicFile> musicFileArrayList) {
        this.musicFileArrayList = musicFileArrayList;
    }

    class MainPageViewHolder extends RecyclerView.ViewHolder{

        TextView filename, timerAlarm, mainAlarm;
        ImageView triggerButton, editButton, editMusic;

        private Typeface myCustomTypeface, myCustomTypefaceBold, myCustomTypefaceBlack, myBlackBold;

        MainPageViewHolder(@NonNull View itemView) {
            super(itemView);
            filename        = itemView.findViewById(R.id.textReceiver);
            timerAlarm      = itemView.findViewById(R.id.phoneTag);
            triggerButton   = itemView.findViewById(R.id.openMenuOptions);
            editButton      = itemView.findViewById(R.id.editOptions);
            mainAlarm       = itemView.findViewById(R.id.timeRoot);
            //editMusic       = itemView.findViewById(R.id.editMusic);

            setTypeFaceTask(itemView);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imainPageRecyclerAdapter.onItemEditClick(getAdapterPosition());
                }
            });
        }

        private void setTypeFaceTask(View itemView){
            myCustomTypeface        = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/FuturaBookFont.ttf");
            myCustomTypefaceBold    = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/FuturaMediumBt.ttf");
            myCustomTypefaceBlack   = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/FuturaHeavyFont.ttf");
            myBlackBold             = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/hurme-geometric-bold.ttf");

            filename.setTypeface(myCustomTypefaceBold);
            timerAlarm.setTypeface(myCustomTypefaceBlack);
            mainAlarm.setTypeface(myBlackBold);
        }

    }

    public interface ImainPageRecyclerAdapter {
        void onItemEditClick(int position);
        //void onLongItemEditClick(int position);
    }
}
