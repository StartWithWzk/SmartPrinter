package com.qg.smartprinter.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * @author TZH
 * @version 1.0
 */
public class BinaryUtil {

    /**
     * 将文本转换成二维码bitmap
     */
    public static Bitmap encodeAsBitmap(String text, int size) throws WriterException {
        BitMatrix result;
        result = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                size,
                size,
                null
        );
        final int width = result.getWidth();
        final int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    /**
     * 将RGB图片变为压缩后的数据
     */
    public static byte[] getDatagram(int[][] pixels) {
        return compress(rgbToBitmap(pixels), pixels.length, pixels[0].length);
    }

    /**
     * 用行程编码的方式对位图进行压缩
     *
     * @param src 未压缩前数据
     * @param row 打印数据的行数
     * @param col 打印数据的列数
     * @return 压缩后的数据
     */
    public static byte[] compress(byte[] src, int row, int col) {
        // 此算法有缺陷，最坏情况下会生成 length*8+2 的长度的数据
        byte[] des = new byte[src.length * 8 + 2];
        int count = 0;
        int map = 0x80;

        // 用索引代替指针
        int s = 0;
        int d = 0;

        //压缩完毕后的数组头两个字节代表行数、列数，其余字节为压缩的图像数据
        des[d++] = (byte) row;
        des[d++] = (byte) (col / 8);
        while (true) {
            //测试连续的1
            while ((src[s] & map) != 0) {
                if (count++ == 127) { //des存满一个字节
                    des[d++] = (byte) 0xff;
                    count = 0;
                } else if ((map >>= 1) == 0) { //src测试完一个字节
                    if (++s == src.length)    //假如src已经转换完毕,跳出循环
                        break;
                    map = 0x80;
                }
            }

            if (count != 0) {
                des[d++] = (byte) (128 + count);
                count = 0;
            }

            //假如src已经转换完毕,跳出循环
            if (s == src.length) {
                break;
            }
            //测试连续的0
            while ((src[s] & map) == 0) {
                if (count++ == 127) {  //des存满一个字节
                    des[d++] = 127;
                    count = 0;
                } else if ((map >>= 1) == 0) { //src测试完一个字节
                    //假如src已经转换完毕,跳出循环
                    if (++s == src.length)
                        break;
                    map = 0x80;
                }
            }

            if (count != 0) {
                des[d++] = (byte) count;
                count = 0;
            }
            //假如src已经转换完毕,跳出循环
            if (s == src.length) {
                break;
            }
        }
        return Arrays.copyOfRange(des, 0, d);
    }

    /**
     * rgb转1位二值图
     */
    public static byte[] rgbToBitmap(int[][] rgbPixels) {
        return binary(rgbToBW(rgbPixels));
    }

    /**
     * rgb转8位二值图
     */
    public static byte[][] rgbToBW(int[][] rgbPixels) {
        return twoValue(grey(rgbPixels));
    }

    /**
     * rgb点转8位二值点
     */
    public static byte rgbToBW(int rgbPixel) {
        return twoValue(grey(rgbPixel));
    }

    /**
     * 8位二值图图转1位二值图
     */
    public static byte[] binary(byte[][] pixels) {
        final int length = pixels.length * pixels[0].length;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(length);

        final int START = 0b10000000; // 第8位开始

        int index = START;
        byte tempByte = 0;
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                byte b = pixels[i][j];
                if ((b & 0xff) == 255) {
                    tempByte |= 0;
                } else {
                    tempByte |= index;
                }
                index >>>= 1;
                if (index == 0) {
                    index = START;
                    baos.write(tempByte);
                    tempByte = 0;
                }
            }
        }
        return baos.toByteArray();
    }

    /**
     * 灰度图转8位二值图(255-白， 0-黑)
     */
    private static byte[][] twoValue(byte[][] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                pixels[i][j] = twoValue(pixels[i][j]);
            }
        }
        return pixels;
    }


    /**
     * 灰度图像素点转二值像素点
     */
    private static byte twoValue(byte pixel) {
        // 阈值法, 阈值为128
        return (byte) ((pixel & 0xff) > 128 ? 255 : 0);
    }

    /**
     * RGB图转灰度图
     */
    private static byte[][] grey(int[][] pixels) {
        byte[][] bytes = new byte[pixels.length][pixels[0].length];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                bytes[i][j] = grey(pixels[i][j]);
            }
        }
        return bytes;
    }

    /**
     * RGB像素点转灰度数值
     */
    private static byte grey(int pixel) {
        int red = red(pixel);
        int green = green(pixel);
        int blue = blue(pixel);
        return (byte) (red * 0.3 + green * 0.59 + blue * 0.11);
    }

    private static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    private static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    private static int blue(int color) {
        return color & 0xFF;
    }
}
