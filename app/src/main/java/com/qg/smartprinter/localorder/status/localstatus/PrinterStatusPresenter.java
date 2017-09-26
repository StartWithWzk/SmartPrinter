package com.qg.smartprinter.localorder.status.localstatus;

import android.support.annotation.NonNull;

import com.qg.common.logger.Log;
import com.qg.smartprinter.data.source.PrintersDataSource;
import com.qg.smartprinter.localorder.Printer;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link PrinterStatusFragment}), retrieves the data and
 * updates the UI as required.
 */
public class PrinterStatusPresenter implements LocalStatusContract.PrinterStatusPresenter {

    private PrintersDataSource mPrintersRepository;

    private LocalStatusContract.PrinterStatusView mPrinterStatusView;

    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeSubscription mSubscriptions;

    public PrinterStatusPresenter(@NonNull PrintersDataSource printersRepository,
                                  @NonNull LocalStatusContract.PrinterStatusView printerStatusView,
                                  @NonNull BaseSchedulerProvider schedulerProvider) {
        mPrintersRepository = checkNotNull(printersRepository);
        mPrinterStatusView = checkNotNull(printerStatusView);
        mSchedulerProvider = checkNotNull(schedulerProvider);

        mSubscriptions = new CompositeSubscription();
        mPrinterStatusView.setPresenter(this);
    }

    @Override
    public void loadPrinters(boolean forceUpdate) {
        mPrinterStatusView.setLoadingIndicator(true);
        mSubscriptions.clear();
        Subscription subscription = mPrintersRepository
                .getPrinters()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<List<Printer>>() {
                    @Override
                    public void onCompleted() {
                        mPrinterStatusView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPrinterStatusView.showLoadingPrintersError();
                    }

                    @Override
                    public void onNext(List<Printer> printers) {
                        processPrinters(printers);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void processPrinters(@NonNull List<Printer> printers) {
        if (!printers.isEmpty()) {
            mPrinterStatusView.showPrinters(printers);
            Log.d(TAG, "processPrinters: HasPrinter");
        } else {
            mPrinterStatusView.showNoPrinters();
            Log.d(TAG, "processPrinters: NoPrinter");
        }
    }

    @Override
    public void subscribe() {
        loadPrinters(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    private static final String TAG = "PrinterStatusPresenter";
}
