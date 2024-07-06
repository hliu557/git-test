import com.lhy.anoTest.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String hexCode = "8431C5A3";
        String hexCode2 = "ff";
        byte[] bytes = HexToBytesConverter.hexToBytes(hexCode);
        byte[] bytes2 = HexToBytesConverter.hexToBytes(hexCode2);


        String str = new String(bytes, "GB18030");
        String str2 = new String(bytes2,"GBK");
        System.out.println(str);
        System.out.println(str2);
        System.out.println(str.equals(str2));
        System.out.println(2);


    }


}
