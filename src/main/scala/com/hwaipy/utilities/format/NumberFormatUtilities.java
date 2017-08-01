package com.hwaipy.utilities.format;

/**
 *
 * @author Hwaipy
 */
public class NumberFormatUtilities {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将一列byte改写为16进制的形式。每个byte两位。
     *
     * @param data
     * @return
     */
    public static String toHex(byte[] data) {
        char[] temp = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            temp[i * 2] = hexDigits[b >>> 4 & 0x0f];
            temp[i * 2 + 1] = hexDigits[b & 0x0f];
        }
        return new String(temp);
    }
}
