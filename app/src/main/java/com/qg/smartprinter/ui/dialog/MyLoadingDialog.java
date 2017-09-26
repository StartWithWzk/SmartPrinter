package com.qg.smartprinter.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by 攀登 on 2016/8/1.
 */
public class MyLoadingDialog extends DialogFragment {
    private String mMessage;

    public static MyLoadingDialog newInstance(String msg) {
        MyLoadingDialog dialog = new MyLoadingDialog();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessage = getArguments().getString("msg");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setMessage(mMessage);
        return dialog;
    }
}
