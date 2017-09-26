package com.qg.deprecated.logic.result;

import java.io.Serializable;

/**
 * Created by TZH on 2016/7/25.
 */
public class Result implements Serializable {
    public String msg;
    /**
     * 1-success
     * 0-failure
     */
    public int retcode;

    public boolean isOk() {
        return msg == null && retcode == 1;
    }
}
