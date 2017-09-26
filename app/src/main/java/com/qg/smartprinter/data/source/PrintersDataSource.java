package com.qg.smartprinter.data.source;

import android.support.annotation.NonNull;

import com.qg.smartprinter.localorder.Printer;

import java.util.List;

import rx.Observable;

/**
 * @author TZH
 * @version 1.0
 */
public interface PrintersDataSource {
    Observable<List<Printer>> getPrinters();

    void addPrinter(@NonNull Printer printer);

    void removePrinter(@NonNull String printerId);
}
