package com.qg.deprecated;

/**
 * This is a readme document for the package {@see com.qg.deprecated}
 */
public class Readme {
    private static final String mText = "" +
            "本package内容包含了通过服务器下单相关的功能\n" +
            "注意：这是一个废弃的package， 里面的东西" +
            "  有可能再也用不到了， 但这是不确定的, " +
            "  所以暂时不删除， 等到确定不需要时可以直接清理此package。";

    private Readme() {
        throw new AssertionError(mText);
    }
}
