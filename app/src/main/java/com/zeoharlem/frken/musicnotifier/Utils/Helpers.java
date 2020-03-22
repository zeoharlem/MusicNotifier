package com.zeoharlem.frken.musicnotifier.Utils;

import android.widget.EditText;

/**
 * Created by Theophilus on 10/28/2017.
 */

public class Helpers {
    //Email Validation pattern
    public static final String regEx = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    //Fragments Tags
    //public static final String Login_Fragment = "Login_Fragment";
    //public static final String SignUp_Fragment = "SignUp_Fragment";
    //public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";
    //public static final String URL_STRING   = "http://10.0.2.2/tfis";
    //public static final String URL_STRING   = "http://192.168.16.2/tfis";
    public static final String URL_STRING   = "http://albertoduwole.com/tfis";
    public static final String API_ID       = "59146428466177992482";
    public static final String API_KEY      = "tkQ03VCcziNtG5qmrh1cKXKQDEEkqojUGnL7tYgW";
    public static final String URL_VARS     = "?apiId="+Helpers.API_ID+"&apiKey="+Helpers.API_KEY;

    public static boolean validate(EditText[] fields){
        for(int i = 0; i < fields.length; i++){
            EditText currentField = fields[i];
            if(currentField.getText().toString().length() <= 0 || currentField.getText().toString().isEmpty()){
                return false;
            }
        }
        return true;
    }

    //Reset the Edit TextField to be empty after submission
    public static void resetEditText(EditText[] fields){
        for(int i = 0; i < fields.length; i++){
            EditText currentField = fields[i];
            currentField.setText("");
        }
    }
}
