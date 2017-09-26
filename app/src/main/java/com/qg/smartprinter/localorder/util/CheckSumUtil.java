package com.qg.smartprinter.localorder.util;

import java.util.Arrays;

/**
 * 校验和工具类
 */
public class CheckSumUtil {

//    public static int checkSum(byte[] msg) {
//        return csum(msg, msg.length);
//    }
//
//    private static int csum(byte[] addr, int count) {
//        int sum = 0;
//        for (int i = 0; i < count; i += 2) {
//            short s1 = (short) (addr[i] & 0xff + (addr[i + 1] << 8));
//            int s = BytesConvert.intFrom2Bytes(
//                    Arrays.copyOfRange(addr, i, i + 2)
//            );
//            System.out.print(s);
//            System.out.print("---");
//            System.out.println(s & 0xffff);
////            int i1 = s & 0xffff;
////            sum += i1;
//            sum += s;
//        }
//    /*
//    if(len){//理论上都是对齐4字节，所以没有len == 0的情况
//		sum += *(u8_t*)data;
//	}
//	*/
//        sum = (sum >> 16) + (sum & 0xffff);
//        sum += (sum >> 16);
//        System.out.println("aaa`" + (short) ~sum + "`bbb" + (0xff & (short) ~sum));
//        return 0xFF & (short) (~sum);
//    }

    public static short checkSum(byte[] msg) {
        return csum(msg, msg.length);
    }

    private static short csum(byte[] addr, int count) {
        int sum = 0;
        for (int i = 0; i < count; i += 2) {
//            short s = (short) (addr[i] & 0xff + (addr[i + 1] << 8));
            short s = (short) BytesConvert.intFrom2Bytes(
                    Arrays.copyOfRange(addr, i, i + 2)
            );
            int i1 = s & 0xffff;
            sum += i1;
        }
    /*
    if(len){//理论上都是对齐4字节，所以没有len == 0的情况
		sum += *(u8_t*)data;
	}
	*/
        sum = (sum >> 16) + (sum & 0xffff);
        sum += (sum >> 16);
        return (short) (~sum);
    }

}
