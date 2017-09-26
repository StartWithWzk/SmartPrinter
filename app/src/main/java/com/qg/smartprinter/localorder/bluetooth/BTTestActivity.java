package com.qg.smartprinter.localorder.bluetooth;

import android.content.Context;
import android.content.Intent;

import com.qg.smartprinter.localorder.PrinterTestActivity;
import com.qg.smartprinter.localorder.PrinterTestFragment;

public class BTTestActivity extends PrinterTestActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, BTTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected PrinterTestFragment getPrinterTestFragment() {
        return BTPrinterTestFragment.newInstance();
    }
}
