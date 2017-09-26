package com.qg.smartprinter.dummy;

import com.qg.smartprinter.R;
import com.qg.smartprinter.logic.model.CookInView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TZH on 2016/7/25.
 */
public class CookbookDummy {

    public static final int[] PICTURES = {
            R.drawable.baiguachaoshaoya,
            R.drawable.caiganbaicaibaolonggutang,
            R.drawable.caiganrousuizhou,
            R.drawable.caipuzhengroubing,
            R.drawable.chongcaohuadunshouroutang,
            R.drawable.fanqiezhurousuijidan
    };

    public static final String[] NAMES = {
            "白瓜炒烧鸭",
            "菜干白菜煲龙骨汤",
            "菜干肉碎粥",
            "蒸肉饼",
            "虫草花炖瘦肉汤",
            "番茄猪肉碎鸡蛋"
    };

    public static final Integer[] PRICES = {
            10,
            16,
            8,
            9,
            18,
            12
    };

    public static final List<CookInView> newCooks() {
        List<CookInView> cooks = new ArrayList<>();
        for (int i = 0; i < NAMES.length; i++) {
            cooks.add(new CookInView(PICTURES[i % PICTURES.length], NAMES[i], PRICES[i]));
        }
        return cooks;
    }
}
