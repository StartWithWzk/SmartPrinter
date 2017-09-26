package com.qg.common.caughtexception;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.qg.common.logger.FileLogNode;
import com.qg.smartprinter.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.qg.common.Preconditions.checkNotNull;
import static com.qg.common.Preconditions.checkState;

public class CaughtExceptionActivity extends AppCompatActivity {

    public static final String EXTRA_THROWABLE = "THROWABLE";
    private static final String LOG_DIR = Environment
            .getExternalStorageDirectory().getPath()
            + "QGProjects"
            + "/"
            + "SmartPrinter"
            + "/"
            + "/log"
            + "/"
            + "Exception"
            + "/";
    private View mFinishButton;
    private View mSendButton;

    private static final String TAG = "CaughtExceptionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_caught_exception);

        Serializable serializableExtra = getIntent().getSerializableExtra(EXTRA_THROWABLE);
        Throwable throwable = (Throwable) checkNotNull(serializableExtra);

        TextView msgView = (TextView) findViewById(R.id.msg);
        msgView.setText(Arrays.toString(throwable.getStackTrace()));

        mSendButton = findViewById(R.id.button_send);
        RxView.clicks(mSendButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        onSendClick();
                    }
                });
        mFinishButton = findViewById(R.id.button_finish);
        RxView.clicks(mFinishButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        finish();
                    }
                });

    }

    private void onSendClick() {
        mSendButton.setEnabled(false);
        mFinishButton.setEnabled(false);
        Observable.just(extractLogToFile())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mFinishButton.setEnabled(true);
                    }
                });
    }

    private String extractLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            // ignore
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        // Make file name - file must be saved to external storage or it wont be readable by
        // the email app.
        String path = LOG_DIR;
        File pathF = new File(path);
        if (!pathF.exists()) {
            android.util.Log.d(TAG, "Create the path:" + path);
            pathF.mkdirs();
        }
        String fullName = path + FileLogNode.getFileName();

        // Extract to file.
        File file = new File(fullName);
        InputStreamReader reader = null;
        FileWriter writer = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time SmartPrinter:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader(process.getInputStream());

            // write output stream
            writer = new FileWriter(file);
            writer.write("Android version: " + Build.VERSION.SDK_INT + "\n");
            writer.write("Device: " + model + "\n");
            writer.write("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");

            char[] buffer = new char[10000];
            do {
                int n = reader.read(buffer, 0, buffer.length);
                if (n == -1)
                    break;
                writer.write(buffer, 0, n);
            } while (true);

            reader.close();
            writer.close();
        } catch (IOException e) {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                    // ignore
                }
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                    // ignore
                }

            // You might want to write a failure message to the log here.
            return null;
        }

        return fullName;
    }
}
