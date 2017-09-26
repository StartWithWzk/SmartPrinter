package com.qg.smartprinter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import com.qg.common.logger.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Bitmap处理工具
 */
public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    public static class BWBitmapTask extends AsyncTask<Uri, Void, Result> {
        private Context mContext;
        private Uri mUri;
        private Handler mHandler;
        private static final int IMG_WIDTH = 120;
        private static final int IMG_HEIGHT = 120;

        public static abstract class Handler {
            public void pre() {}
            public void post() {}
            public abstract void success(Result result);
            public abstract void failure();
        }

        public BWBitmapTask(Context context, Uri uri, Handler handler) {
            mContext = context;
            mUri = uri;
            mHandler = handler;
        }

        @Override
        protected void onPreExecute() {
            mHandler.pre();
        }

        @Override
        protected Result doInBackground(Uri... params) {
            try {
                ParcelFileDescriptor r1 = mContext.getContentResolver().openFileDescriptor(mUri, "r");
                FileDescriptor r = r1 != null ? r1.getFileDescriptor() : null;
                return BitmapUtil.bwStream(r, IMG_WIDTH, IMG_HEIGHT);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "openFileDescriptor Error", e);
            } catch (IOException e) {
                Log.e(TAG, "bw Stream Error", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(BitmapUtil.Result result) {
            mHandler.post();
            if (result != null) {
                mHandler.success(result);
            } else {
                mHandler.failure();
            }
        }
    }


    public static class Result {
        public Bitmap bitmap;
        public byte[] bytes;

        public Result(Bitmap bitmap, byte[] bytes) {
            this.bitmap = bitmap;
            this.bytes = bytes;
        }
    }

    /**
     * 黑白化Bitmap流
     */
    public static Result bwStream(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) throws IOException {
        return bwImage(zoomImage(decodeFD(fileDescriptor, reqWidth, reqHeight), reqWidth, reqHeight));
    }

    /**
     * 黑白化Bitmap
     */
    public static Result bwImage(Bitmap bitmap) {
        Bitmap bitmap1 = bitmap.copy(Bitmap.Config.RGB_565, true);
        int[][] rawInts = new int[bitmap.getHeight()][bitmap.getWidth()];
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int pixel = bitmap.getPixel(i, j);
                pixel = BinaryUtil.rgbToBW(pixel);
                bitmap1.setPixel(i, j, pixel);
                rawInts[j][i] = pixel;
            }
        }

        return new Result(bitmap1, BinaryUtil.getDatagram(rawInts));
    }

    public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
        // 获取这个图片的宽和高
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, width,
                height, matrix, true);
    }

    public static Bitmap decodeFD(FileDescriptor fd, int reqWidth, int reqHeight) throws IOException {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, opts);
        opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, opts);
    }

    public static int calculateInSampleSize(BitmapFactory.Options opt, int reqWidth, int reqHeight) {
        final int height = opt.outHeight;
        final int width = opt.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
