package com.qg.deprecated.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.qg.smartprinter.ui.BaseActivity;
import com.qg.smartprinter.R;
import com.qg.deprecated.adapter.PhotoAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 攀登 on 2016/8/6.
 * @deprecated
 */
public class ChosePhotoActivity extends BaseActivity {

    public static void startForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, ChosePhotoActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    private final static int SCAN_OK = 1;
    private ProgressDialog mProgressDialog;
    private PhotoAdapter adapter;
    private GridView mGridView;
    private List<String> img_path = new ArrayList<String>();

    private Handler mHandler = new MyHandler(this);

    public static class MyHandler extends Handler {
        WeakReference<ChosePhotoActivity> mTarget;

        public MyHandler(ChosePhotoActivity target) {
            super(Looper.getMainLooper());
            this.mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChosePhotoActivity target = mTarget.get();
            if (target == null) return;
            switch (msg.what) {
                case SCAN_OK:
                    target.mProgressDialog.dismiss();
                    target.adapter = new PhotoAdapter(target, target.img_path);
                    target.mGridView.setAdapter(target.adapter);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosephoto);
        mGridView = (GridView) findViewById(R.id.gridview);
        getImages();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("photo_path", img_path.get(position).substring(7));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ChosePhotoActivity.this.getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
//                    Log.e("img_path->", path);
                    img_path.add("file://" + path);

                }
                mCursor.close();
                mHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }
}
