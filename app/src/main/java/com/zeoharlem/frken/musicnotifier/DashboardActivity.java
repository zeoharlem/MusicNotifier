package com.zeoharlem.frken.musicnotifier;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.Models.Folders;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.io.File;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    Typeface mTypeface, mTypefaceBold, mTypefaceBlack;
    TextInputEditText createFolderEdit;
    TextInputLayout createFolder;
    Button createButton;
    DatabaseHelper myDataHelper;
    RadioButton internalStorage, externalStorage;
    RadioGroup radioGroupBox;
    private Folders folders;
    private RadioButton radioButtonPick;
    private int STORAGE_PERMISSION_CODE = 1155;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
        setTypefaceEditButton();

        folders         = new Folders();
        myDataHelper    = new DatabaseHelper(this);

        TextView removeDatabase = findViewById(R.id.removeDb);
        removeDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDataHelper.dropTables();
                L.l(getApplicationContext(), "Table Dropped and Created");
            }
        });

        radioGroupBox   = findViewById(R.id.groupRadioStorage);



        //Check for the existence of the folder in the database
        //And if found check for the existence on the mobile phone
        //if all true startActivity on the next screen not the current one
        if(checkIfFolderExistAction()){
            L.l(getApplicationContext(), folders.getFolderName());
            Intent intent   = new Intent(getApplicationContext(), ActionTabActivity.class);
            intent.putExtra("folderName", folders.getFolderName());
            intent.putExtra("storageType", folders.getStorageType());
            startActivity(intent);
            finish();
        }

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //L.l(getApplicationContext(), "Start permission granted");
                if(ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                    //L.l(getApplicationContext(), "Permission Granted");
                    triggerSelectionAction();
                }
                else{
                    //L.l(getApplicationContext(), "request for perssion");
                    requestStoragePermission();
                }
            }
        });
    }

    private void triggerSelectionAction(){
        int seletedRadio= radioGroupBox.getCheckedRadioButtonId();
        radioButtonPick = findViewById(seletedRadio);

        //CHeck the storage Type selected and make the folder
        if(radioButtonPick.getText().toString().equals("SD Card")){

            File fileDirectory  = new File(Environment.getExternalStorageDirectory()+"/MusicNotifier",
                    Objects.requireNonNull(createFolderEdit.getText()).toString());

            //Code should be Refactored
            if(fileDirectory.mkdirs()){
                //L.l(getApplicationContext(), createFolderEdit.getText().toString());
                if(setFolderToDatabaseAction()){
                    Intent intent   = new Intent(DashboardActivity.this, ActionTabActivity.class);
                    intent.putExtra("folderName", folders.getFolderName());
                    intent.putExtra("storageType", "SD Card");
                    startActivity(intent);
                }
                else{
                    L.l(getApplicationContext(), "Cannot upload to the Database");
                }
            }
            else{
                L.l(getApplicationContext(), createFolderEdit.getText().toString()+" External Storage Not created");
            }
        }
        else if(radioButtonPick.getText().toString().equals("Internal Storage")){

            File myFileDirectory    = new File(getFilesDir()+"/MusicNotifier",
                    Objects.requireNonNull(createFolderEdit.getText()).toString());

            //Code should be Refactored
            if(myFileDirectory.mkdirs()){
                if(setFolderToDatabaseAction()){
                    Intent intent   = new Intent(getApplicationContext(), ActionTabActivity.class);
                    intent.putExtra("folderName", folders.getFolderName());
                    intent.putExtra("storageType", "Internal Storage");
                    startActivity(intent);
                }
                else{
                    L.l(getApplicationContext(), "Cannot upload to the Database");
                }
            }
            else{
                L.l(getApplicationContext(), createFolderEdit.getText().toString()+" Internal Storage Error");
            }
        }
        myDataHelper.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                triggerSelectionAction();
            }
            else{
                L.l(DashboardActivity.this, "Permission not granted");
            }
        }
        else{
            L.l(DashboardActivity.this, "Permission not granted "+STORAGE_PERMISSION_CODE);
        }
    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Grant permission to app to read storage areas")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DashboardActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void setTypefaceEditButton(){
        mTypefaceBlack  = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Black.ttf");
        mTypefaceBold   = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");
        createButton    = findViewById(R.id.createButton);
        createFolder    = findViewById(R.id.folderCreate);
        createFolderEdit= findViewById(R.id.folderCreateEdit);

        internalStorage = findViewById(R.id.internalStorage);
        externalStorage = findViewById(R.id.externalStorage);

        createButton.setTypeface(mTypefaceBlack);
        createFolderEdit.setTypeface(mTypefaceBlack);
        createFolder.setTypeface(mTypefaceBold);
        internalStorage.setTypeface(mTypefaceBold);
        externalStorage.setTypeface(mTypefaceBold);
    }

    //Create a folder with name and save to SQLLite Database
    private boolean setFolderToDatabaseAction(){
        folders = new Folders();
        folders.setFolderName(Objects.requireNonNull(createFolderEdit.getText()).toString());
        folders.setStorageType(radioButtonPick.getText().toString());
        return myDataHelper.createFolder(folders);
    }

    private boolean checkIfFolderExistAction(){
        folders = new Folders();
        if(myDataHelper.getDirectoryLimit(folders, this)) {
            File fileDirectory = new File(Environment.getExternalStorageDirectory() + "/MusicNotifier/" + folders.getFolderName());
            return fileDirectory.exists();
        }
        return false;
    }
}
