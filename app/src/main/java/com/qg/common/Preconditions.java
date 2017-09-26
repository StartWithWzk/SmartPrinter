package com.qg.common;

import android.support.annotation.NonNull;

/**
 * A simple utility class used to check method Preconditions.
 */
public final class Preconditions {

    private Preconditions() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
}
