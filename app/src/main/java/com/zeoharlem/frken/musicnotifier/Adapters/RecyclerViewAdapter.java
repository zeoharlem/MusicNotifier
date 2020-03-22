package com.zeoharlem.frken.musicnotifier.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeoharlem.frken.musicnotifier.Broadcasters.Alarm;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;
import com.zeoharlem.frken.musicnotifier.R;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewAdapterHolder> {
    private LayoutInflater layoutInflater;
    private ArrayList<MusicFile> musicFileArrayList;
    private Context context;

    public RecyclerViewAdapter(ArrayList<MusicFile> musicFileArrayList, Context context) {
        this.musicFileArrayList = musicFileArrayList;
        this.context            = context;
        layoutInflater          = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.RecyclerViewAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view   = layoutInflater.inflate(R.layout.item_rec_view_rows, parent, false);
        return new RecyclerViewAdapterHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterHolder holder, int position) {
        final MusicFile musicFile = musicFileArrayList.get(position);
        final String nFileName    = musicFile.getFolderPath().substring(musicFile.getFolderPath().lastIndexOf("/")+1);
        holder.timerAlarm.setText(musicFile.getCreatedAt());
        holder.filename.setText(nFileName);
    }

    @Override
    public int getItemCount() {
        return musicFileArrayList.size();
    }

    /**
     * Should be refactored it's too tightly coupled to the class
     * @param musicFile
     * @return
     */
    private long getSimpleStringDatetoMilli(MusicFile musicFile){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        switch (musicFile.getCreatedAt()){
            case "12:00:00":
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                break;
            case "15:00:00":
                calendar.set(Calendar.HOUR_OF_DAY, 17);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                break;
            case "18:00:00":
                calendar.set(Calendar.HOUR_OF_DAY, 18);
                break;
        }
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

    static class RecyclerViewAdapterHolder extends RecyclerView.ViewHolder{

        TextView filename, timerAlarm;
        ImageView triggerButton;
        private Typeface myCustomTypeface, myCustomTypefaceBold, myCustomTypefaceBlack;

        RecyclerViewAdapterHolder(@NonNull View itemView) {
            super(itemView);
            filename        = itemView.findViewById(R.id.textReceiver);
            timerAlarm      = itemView.findViewById(R.id.phoneTag);
            triggerButton   = itemView.findViewById(R.id.openMenuOptions);

            setTypeFaceTask(itemView);
        }

        private void setTypeFaceTask(View itemView){
            myCustomTypeface        = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/FuturaBookFont.ttf");
            myCustomTypefaceBold    = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/hurme-geometric-bold.ttf");
            myCustomTypefaceBlack   = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/ProximaNova-Black.ttf");

            filename.setTypeface(myCustomTypefaceBold);
            timerAlarm.setTypeface(myCustomTypeface);
        }

    }
}
