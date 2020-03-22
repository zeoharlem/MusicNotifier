package com.zeoharlem.frken.musicnotifier.DialogBox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.zeoharlem.frken.musicnotifier.R;

public class MyFragmentDialogBox extends DialogFragment implements View.OnClickListener{

    private Button yes, no;

    MyDialogBoxCallBackListener myDialogBoxCallBackListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myDialogBoxCallBackListener = (MyDialogBoxCallBackListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.my_fragment_dialog_box, null);
        yes         = view.findViewById(R.id.yes);
        no          = view.findViewById(R.id.no);

        no.setOnClickListener(this);
        yes.setOnClickListener(this);

        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.yes){
            myDialogBoxCallBackListener.onDialogMessageClick("Yes");
            dismiss();
        }
        else{
            myDialogBoxCallBackListener.onDialogMessageClick("No");
            dismiss();
        }
    }


    //    Context context;
//
//    public MyFragmentDialogBox() {
//        context = getActivity();
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setTitle("Really?");
//        alertDialogBuilder.setMessage("Are you sure?");
//        //null should be your on click listener
//        alertDialogBuilder.setPositiveButton("Yes", null);
//        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        return alertDialogBuilder.create();
////        return super.onCreateDialog(savedInstanceState);
//    }

    public interface MyDialogBoxCallBackListener{
        public void onDialogMessageClick(String message);
    }
}
