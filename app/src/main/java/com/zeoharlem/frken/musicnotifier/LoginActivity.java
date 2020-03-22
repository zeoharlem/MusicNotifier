package com.zeoharlem.frken.musicnotifier;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zeoharlem.frken.musicnotifier.Business.DatabaseHelper;
import com.zeoharlem.frken.musicnotifier.DialogBox.MyLoadingAlertDialogFrag;
import com.zeoharlem.frken.musicnotifier.Utils.Helpers;
import com.zeoharlem.frken.musicnotifier.Utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private Typeface mTypefaceBlack, mTypefaceBoldBlack;
    private TextView welcomeNotifier;
    private TextView signInRow;
    private Button signInButton;
    private ImageView logoNotifier;
    private TextInputLayout username, password;
    private TextInputEditText usernameEdit, passwordEdit;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        setTypefaceTask();

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideSoftKeyBoard(v);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideSoftKeyBoard(v);
                }
            }
        });

        welcomeNotifier.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final DatabaseHelper myDatabaseHelper = new DatabaseHelper(LoginActivity.this);
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Database Reset")
                        .setMessage("Do you want to clear the DB")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDatabaseHelper.dropTables();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                myDatabaseHelper.dropTables();
                return true;
            }
        });

        View viewBody   = this.getCurrentFocus();
        if(viewBody != null){
            hideSoftKeyBoard(viewBody);
        }
    }

    private void setTypefaceTask(){
        mTypefaceBlack  = Typeface.createFromAsset(getAssets(), "fonts/FuturaBold.ttf");
        Typeface lightType  = Typeface.createFromAsset(getAssets(), "fonts/FuturaMediumBt.ttf");
        mTypefaceBoldBlack  = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Black.ttf");
        welcomeNotifier = findViewById(R.id.welcomeNotifier);
        signInRow       = findViewById(R.id.signInRow);
        //logoNotifier    = findViewById(R.id.logoNotifier);
        username        = findViewById(R.id.username);
        password        = findViewById(R.id.password);
        usernameEdit    = findViewById(R.id.usernameEdit);
        passwordEdit    = findViewById(R.id.passwordEdit);
        signInButton    = findViewById(R.id.signInButton);

        username.setTypeface(mTypefaceBlack);
        password.setTypeface(mTypefaceBlack);
        usernameEdit.setTypeface(mTypefaceBlack);
        passwordEdit.setTypeface(mTypefaceBlack);

        welcomeNotifier.setTypeface(mTypefaceBlack);
        signInRow.setTypeface(lightType);

        signInButton.setTypeface(mTypefaceBoldBlack);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        if(validateRow()){
            String textEmailId  = usernameEdit.getText().toString().trim();
            String textPassword = passwordEdit.getText().toString().trim();
            myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();
            myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "MyLoginDialogBox");
            myLoadingAlertDialogFrag.callAlertLoadingTaskCallback(new MyLoadingAlertDialogFrag.AlertLoadingTaskCallback() {
                @Override
                public void CallbackTask(final MyLoadingAlertDialogFrag myLoadingAlertDialogFrag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myLoadingAlertDialogFrag.dismiss();
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            finish();
                        }
                    }, 2000);
                }
            });
        }
    }

    private boolean validateRow(){
        String textEmailId  = usernameEdit.getText().toString().trim();
        String textPassword = passwordEdit.getText().toString().trim();
        Pattern pattern     = Pattern.compile(Helpers.regEx);
        Matcher matcher     = pattern.matcher(textEmailId);
        if(textEmailId.equals("") || textEmailId.isEmpty()){
            L.l(getApplicationContext(), "Your Username Field is Empty");
            return false;
        }
        else if(textPassword.equals("") || textPassword.isEmpty()){
            L.l(getApplicationContext(), "Your Password Field is Empty");
            return false;
        }
        else if(!matcher.find()){
            L.l(getApplicationContext(), "Your Email Address is Invalid Format");
            return false;
        }
        return true;
    }

    private void hideSoftKeyBoard(View view) {
        InputMethodManager inputMethodManager   = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
