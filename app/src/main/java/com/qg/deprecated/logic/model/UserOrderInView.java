package com.qg.deprecated.logic.model;

import com.qg.smartprinter.logic.model.CookInView;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 订单
 */
public class UserOrderInView implements Serializable {
    public LinkedList<CookInView> cooks;

    public UserOrderInView(List<CookInView> cooks) {
        LinkedList linkedList = new LinkedList(cooks);
        this.cooks = linkedList;
    }
}
