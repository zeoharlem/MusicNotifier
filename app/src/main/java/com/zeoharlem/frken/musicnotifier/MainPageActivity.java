package com.zeoharlem.frken.musicnotifier;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeoharlem.frken.musicnotifier.Adapters.MainPageRecyclerAdapter;
import com.zeoharlem.frken.musicnotifier.Adapters.RecyclerViewAdapter;
import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.DialogBox.MyLoadingAlertDialogFrag;
import com.zeoharlem.frken.musicnotifier.Models.MusicFile;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerAdapter;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;
    Typeface mTypeface, mTypefaceBold, mTypefaceBlack;
    private DatabaseHelper myDatabaseHelper;
    ArrayList<MusicFile> musicFileArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Set Alarms/Files");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDatabaseHelper            = new DatabaseHelper(this);
        myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();

        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "MainPageBox");
        //Volley Request should be made here instead of the dismiss() callback
        myLoadingAlertDialogFrag.callAlertLoadingTaskCallback(new MyLoadingAlertDialogFrag.AlertLoadingTaskCallback() {
            @Override
            public void CallbackTask(final MyLoadingAlertDialogFrag myLoadingAlertDialogFrag) {
                myLoadingAlertDialogFrag.dismiss();

                musicFileArrayList  = myDatabaseHelper.getAllMusicFilesRow();
                //L.l(getApplicationContext(), String.valueOf(musicFileArrayList));

                //Set Contents for the recyclerview
                setRecyclerViewAdapterRow();
            }
        });

        setTypefaceButton();

    }

    private void setRecyclerViewAdapterRow(){
        recyclerView                        = findViewById(R.id.mainPageRecyclerView);
        LinearLayoutManager layoutManager   = new LinearLayoutManager(this);
        DividerItemDecoration decoration    = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerAdapter                     = new RecyclerViewAdapter(musicFileArrayList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(recyclerAdapter);
    }

    //Set the buttn and the edittext fields
    private void setTypefaceButton(){
        mTypefaceBlack  = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Black.ttf");
        mTypefaceBold   = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");

        Button closeApp = findViewById(R.id.closeAppNow);

        closeApp.setTypeface(mTypefaceBlack);
//        closeApp.setBackgroundColor(Color.TRANSPARENT);
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

}
