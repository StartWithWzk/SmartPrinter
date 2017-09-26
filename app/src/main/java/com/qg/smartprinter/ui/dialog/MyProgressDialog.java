package com.qg.smartprinter.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class MyProgressDialog extends DialogFragment {
    private ProgressDialog dialog;
    private String mMessage;
    private OnCancelListener mOnCancelListener;

    public static MyProgressDialog newInstance(String msg) {
        MyProgressDialog dialog = new MyProgressDialog();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    public static interface OnCancelListener {
        void onCancel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessage = getArguments().getString("msg");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.setMessage(mMessage);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel();
        }
    }

    public void setProgress(int progress) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(progress);
        }
    }
}
