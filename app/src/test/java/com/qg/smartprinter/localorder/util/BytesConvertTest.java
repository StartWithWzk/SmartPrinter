package com.qg.smartprinter.localorder.util;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author TZH
 * @version 1.0
 */
public class BytesConvertTest {
    byte[][] i_bytes = {
            {(byte) 0xFF, (byte) 0xFF},
            {(byte) 0xEF, (byte) 0xFF},
            {(byte) 0x0F, (byte) 0xFF},
            {(byte) 0x00, (byte) 0xFF},
    };

    byte[][] l_bytes = {
            {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
            {(byte) 0xEF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
            {(byte) 0x0F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
            {(byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
    };
    int[] ints = {
            0xFFFF,
            0xEFFF,
            0x0FFF,
            0x00FF,
    };
    long[] longs = {
            0xFFFFFFFFL,
            0xEFFFFFFFL,
            0x0FFFFFFFL,
            0x00FFFFFFL,
    };

    @Test
    public void testLongFrom4Bytes() {
        for (int i = 0; i < l_bytes.length; i++) {
            assertEquals(longs[i], BytesConvert.longFrom4Bytes(l_bytes[i]));
        }
    }

    @Test
    public void testIntFrom2Bytes() {
        for (int i = 0; i < i_bytes.length; i++) {
            assertEquals(ints[i], BytesConvert.intFrom2Bytes(i_bytes[i]));
        }
    }

    @Test
    public void testIntTo4Bytes() {
        for (int i = 0; i < l_bytes.length; i++) {
            byte[] bytes = BytesConvert.intTo4Bytes((int) longs[i]);
            assertTrue(Arrays.equals(l_bytes[i], bytes));
        }
    }

    @Test
    public void testShortTo2Bytes() {
        for (int i = 0; i < i_bytes.length; i++) {
            byte[] bytes = BytesConvert.shortTo2Bytes((short) ints[i]);
            assertTrue(Arrays.equals(i_bytes[i], bytes));
        }
    }
}
