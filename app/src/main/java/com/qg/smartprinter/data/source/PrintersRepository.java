package com.qg.smartprinter.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.qg.common.logger.Log;
import com.qg.smartprinter.localorder.Printer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * @author TZH
 * @version 1.0
 */
public class PrintersRepository implements PrintersDataSource {
    private static final String TAG = "PrintersRepository";

    private static final PrintersRepository printersRepository = new PrintersRepository();

    @NonNull
    private Map<String, Printer> mConnectedPrinters;

    private PrintersRepository() {
        mConnectedPrinters = new HashMap<>();
    }

    public static PrintersRepository getInstance() {
        return printersRepository;
    }

    @Override
    public Observable<List<Printer>> getPrinters() {
        Log.d(TAG, "Printer: size:" + mConnectedPrinters.values().size());
        return Observable.from(mConnectedPrinters.values()).toList();
    }

    @Override
    public void addPrinter(@NonNull Printer printer) {
        checkNotNull(printer);
        String id = String.valueOf(printer.getId());
        mConnectedPrinters.put(id, printer);
        Log.d(TAG, "addPrinter: id:" + id + "; size:" + mConnectedPrinters.values().size());
    }

    @Override
    public void removePrinter(@NonNull String printerId) {
        checkNotNull(printerId);
        mConnectedPrinters.remove(printerId);
        Log.d(TAG, "removePrinter: id" + printerId + "; size:" + mConnectedPrinters.values().size());
    }
}
