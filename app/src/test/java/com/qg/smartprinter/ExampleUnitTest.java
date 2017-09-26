package com.qg.smartprinter;

import com.qg.smartprinter.dummy.OrderDummy;
import com.qg.smartprinter.localorder.messages.AbstractMessage;
import com.qg.smartprinter.localorder.messages.BOrder;
import com.qg.smartprinter.localorder.messages.BOrderData;
import com.qg.smartprinter.localorder.messages.BOrderStatus;
import com.qg.smartprinter.localorder.messages.BPrinterStatus;
import com.qg.smartprinter.localorder.messages.BResponse;
import com.qg.smartprinter.localorder.messages.WifiOrderRequest;
import com.qg.smartprinter.localorder.util.BytesConvert;
import com.qg.smartprinter.localorder.util.CheckSumUtil;
import com.qg.smartprinter.localorder.util.DebugUtil;

import org.junit.Ignore;
import org.junit.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static com.qg.smartprinter.util.NetworkUtils.inetAddressToInt;
import static com.qg.smartprinter.util.NetworkUtils.intToInetAddress;
import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void test() throws UnknownHostException {
        byte[] address = {10, 10, 100, 100};
        InetAddress byAddress = InetAddress.getByAddress(address);
        int addressToInt = inetAddressToInt((Inet4Address) byAddress);
        InetAddress inetAddress = intToInetAddress(addressToInt);
        System.out.println(addressToInt);
        System.out.println(inetAddressToInt((Inet4Address) inetAddress));
    }

    @Test
    public void checkSum() {
        int ip = 12;
        BOrder order = BOrder.fromOrderData(1, new BOrderData(), 0);
        WifiOrderRequest request = WifiOrderRequest.fromOrder(order, ip);
        System.out.println(DebugUtil.getBytesString(request.toBytes()));
        System.out.println(CheckSumUtil.checkSum(request.toBytes()));
        System.out.println(DebugUtil.getBytesString(request.toRealBytes()));
        System.out.println(CheckSumUtil.checkSum(request.toRealBytes()));
    }

    @Test
    public void calc_isCorrect() throws Exception {
        assertEquals(getIdlingDelay(0), 1);
        assertEquals(getIdlingDelay(100), 1);
        assertEquals(getIdlingDelay(900), 1);
        assertEquals(getIdlingDelay(1000), 1);
        assertEquals(getIdlingDelay(1001), 2);
        assertEquals(getIdlingDelay(9999), 10);
        assertEquals(getIdlingDelay(10000), 10);
        assertEquals(getIdlingDelay(10001), 11);
    }

    private int getIdlingDelay(int a) {
        int delay = (int) Math.ceil((float) a / 1000);
        return delay <= 0 ? 1 : delay;
    }

    @Ignore
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dg_test() {
        BOrderData bOrderData = OrderDummy.newOrderData(1);
        BOrder bOrder = BOrder.fromOrderData(1, bOrderData, 0);
        System.out.println(DebugUtil.getBytesString(bOrder.toRealBytes()));
    }

    @Test
    public void receive_isCorrect() {
        BResponse bResponse = new BResponse(0, 0, 1);
        System.out.println(DebugUtil.getBytesString(bResponse.toRealBytes()));
        byte[] bytes = {
                (byte) 0xCF, (byte) 0xFC, (byte) 0b01000011, (byte) 0x10,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                (byte) 0x32, (byte) 0xF1, (byte) 0xFC, (byte) 0xCF,
        };
        System.out.println(DebugUtil.getBytesString(bytes));
        AbstractMessage abstractMessage = AbstractMessage.bytesToAbstractStatus(bytes);
        switch (abstractMessage.getStatusToken()) {
            case BPrinterStatus.TYPE_TOKEN:
                System.out.println("print");
                BPrinterStatus bPrinterStatus = BPrinterStatus.bytesToPrinterStatus(bytes);
                System.out.println(Integer.toHexString(bPrinterStatus.sta));
                if (bPrinterStatus.sta == 0x10) {
                    System.out.printf("1");
                }
                break;
            case BOrderStatus.TYPE_TOKEN:
                System.out.println("order");
                break;
            case BResponse.TYPE_TOKEN:
                System.out.println("response");
                break;
        }
    }

    @Test
    public void testBytesConvert() {
        byte[] bytes = {-1, -1, -1, -1, -1};
        long i = ByteBuffer.allocate(8).putInt(0).put(bytes, 0, 4).getLong(0);
        long i1 = 0xFFFFFFFFL;
        assertEquals(i, i1);
    }

    @Test
    public void testIntToLong() {
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
            int c = (int) (long) i;
            assertEquals(i, c);
        }
    }

    @Test
    public void testLongToInt() {
        final long maxUnsignedInt32 = 1L + Integer.MAX_VALUE + Integer.MAX_VALUE;
        for (long i = Integer.MAX_VALUE; i < Integer.MAX_VALUE + 10L; i++) {
            assert1(i);
        }
        for (long i = maxUnsignedInt32; i > maxUnsignedInt32 - 10; i--) {
            assert1(i);
        }
    }

    void assert1(long i) {
        int i1 = (int) i;
        byte[] bytes = BytesConvert.intTo4Bytes(i1);
        long l = BytesConvert.longFrom4Bytes(bytes);
        System.out.println(i + ";" + l);
        assertEquals(i, l);

    }

    @Test
    public void testShift() {
        System.out.println(Short.MAX_VALUE);
        System.out.println(Short.MAX_VALUE & 0xFFFF);
        System.out.println(Integer.toBinaryString(Short.MIN_VALUE).length());
        String s = Integer.toBinaryString(0xFFFF & Short.MIN_VALUE);
        System.out.println(s);
        System.out.println(s.length());
        System.out.println(Integer.toBinaryString(Integer.MAX_VALUE).length());
        for (int i = 0; i < (0xFFFF & Short.MIN_VALUE); i++) {
            byte b = (byte) ((i & 0xe000) >>> (5 + 8));// >> 13bit
            byte b1 = (byte) ((i) >>> (5 + 8));// >> 13bit
            try {
                assertEquals(b, b1);
            } catch (AssertionError e) {
                System.out.println((i));
                throw e;
            }
        }
    }
}