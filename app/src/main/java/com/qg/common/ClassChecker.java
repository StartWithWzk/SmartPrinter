package com.qg.common;

import android.support.annotation.NonNull;

import static com.qg.common.Preconditions.checkNotNull;

/**
 * A checker of classes.
 */
public class ClassChecker {
    private Class mClass;

    private ClassChecker(Class c) {
        mClass = c;
    }

    public static ClassChecker is(@NonNull Object o) {
        checkNotNull(o);
        return new ClassChecker(o.getClass());
    }

    public boolean in(@NonNull Class... classes) {
        for (Class aClass : classes) {
            if (aClass.equals(mClass)) {
                return true;
            }
        }
        return false;
    }
}

