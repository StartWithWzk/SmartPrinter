package com.qg.smartprinter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.qg.smartprinter.data.source.OrdersDataSource;
import com.qg.smartprinter.data.source.OrdersRepository;
import com.qg.smartprinter.data.source.PrintersDataSource;
import com.qg.smartprinter.data.source.PrintersRepository;
import com.qg.smartprinter.util.scheduler.BaseSchedulerProvider;
import com.qg.smartprinter.util.scheduler.SchedulerProvider;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link OrdersRepository} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static PrintersRepository providePrintersRepository() {
        return PrintersRepository.getInstance();
    }

    public static OrdersRepository provideOrdersRepository(@NonNull Context context) {
        checkNotNull(context);
        return OrdersRepository.getInstance(context, provideBaseSchedulerProvider());
    }

    public static BaseSchedulerProvider provideBaseSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }
}