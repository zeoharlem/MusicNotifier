package com.zeoharlem.frken.musicnotifier.DialogBox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zeoharlem.frken.musicnotifier.R;

import java.util.Objects;

public class MyLoadingAlertDialogFrag extends DialogFragment {

    private AlertDialog.Builder builder;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        builder.setView(Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.fragment_loading_alert_dialog, null));
        setCancelable(false);
        return builder.create();
    }

    public void callAlertLoadingTaskCallback(AlertLoadingTaskCallback alertLoadingTaskCallback){
        alertLoadingTaskCallback.CallbackTask(this);
    }

    public interface AlertLoadingTaskCallback{
        void CallbackTask(MyLoadingAlertDialogFrag myLoadingAlertDialogFrag);
    }
}
