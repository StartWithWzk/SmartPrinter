

package com.qg.smartprinter.localorder.wifi;

import android.content.Context;
import android.content.Intent;

import com.qg.smartprinter.localorder.PrinterTestActivity;
import com.qg.smartprinter.localorder.PrinterTestFragment;

public class WifiTestActivity extends PrinterTestActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, WifiTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected PrinterTestFragment getPrinterTestFragment() {
        return WifiPrinterTestFragment.newInstance();
    }

}
