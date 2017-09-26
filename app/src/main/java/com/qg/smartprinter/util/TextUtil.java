package com.qg.smartprinter.util;

import android.support.annotation.Nullable;

/**
 * @author TZH
 * @version 1.0
 */
public class TextUtil {
    public static String nullable(@Nullable String str) {
        return str == null ? "" :str;
    }
}
