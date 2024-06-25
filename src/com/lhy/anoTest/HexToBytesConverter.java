package com.lhy.anoTest;

public class HexToBytesConverter {
        public static byte[] hexToBytes(String hexString) {
            if (hexString == null || hexString.equals("")) {
                return null;
            }
            hexString = hexString.replaceAll(" ", "");
            if ((hexString.length() % 2) != 0) {
                hexString = "0" + hexString;
            }

            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < hexString.length(); i += 2) {
                bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                        + Character.digit(hexString.charAt(i+1), 16));
            }
            return bytes;
        }

        public static void main(String[] args) {
            String hexString = "1a 2b 3c 4d 5e"; // 示例16进制字符串
            byte[] bytes = hexToBytes(hexString);

            // 输出转换后的字节数组
            for (byte b : bytes) {
                System.out.format("0x%X ", b);
            }
        }
    }