package com.qg.smartprinter.util.rxbus;

import com.google.gson.Gson;
import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.Relay;
import com.qg.common.logger.Log;

import rx.Observable;
import rx.functions.Action1;

public class RxBus {

    private final Relay<Object, Object> mBus = PublishRelay.create().toSerialized();

    private static class DefaultRxBus {
        // The default singleton bus.
        static final RxBus INSTANCE = new RxBus();
    }

    /**
     * Get the default bus.
     */
    public static RxBus getDefault() {
        return DefaultRxBus.INSTANCE;
    }

    /**
     * Post the object to the bus.
     */
    public void post(Object o) {
        mBus.call(o);
    }

    public Observable<Object> asObservable() {
        return mBus.asObservable();
    }

    private static final String TAG = "RxBus";

    public boolean hasObservers() {
        return mBus.hasObservers();
    }
}
