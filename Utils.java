import java.io.*;
import java.util.*;

public class Utils {


public static String byteToHex(byte[] dataB){
	String result = "";	
	for(byte b : dataB){
		result += String.format("%02X", b);
	}
	return result;
}

public static String hexToChar(String hex){
	String result = "";
	String tmp = "";
	for(int i = 0; i < hex.length(); i+=2){
		tmp = hex.substring(i, i + 2);
		result += (char)Integer.parseInt(tmp, 16);
	}
	return result;
}

public static byte[] hexToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
}

public static long byteToIntBE(byte[] bytes){
	String hex = "";
	for(int i = 0; i <= bytes.length - 1; i++){
		hex += String.format("%02X", bytes[i]);
	}
        long i = Long.parseLong(hex,16);
	return i;
}

public static int byteToIntLE(byte[] bytes){
	String hex = "";
	for(int i = bytes.length - 1; i >= 0; i--){
		hex += String.format("%02X", bytes[i]);
	}
        int i = Integer.parseInt(hex,16);
	return i;
}

public static Date addSeconds(Date date, int seconds) {
 	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(Calendar.SECOND, seconds);
    	return cal.getTime();
}

public static String addTab(String text, int numberOfTab){
	String newString = "";
	String[] parts = text.split("\n");
	int j = 0;
	for (int i = 0; i < parts.length; i++){
		j = 0;
		while(j < numberOfTab){
			newString += "\t";
			j++;
		}
		newString += parts[i] + "\n";
	}
	return newString;
}

public static String byteToIP(byte[] dataB){
	String result = "";	
	for(byte b : dataB){
		result += String.format("%d.", b & 255);
	}
	
	return result.substring(0, result.length() - 1);
}

public static String byteToMac(byte[] dataB){
	String result = "";	
	for(byte b : dataB){
		result += String.format("%02X:", b);
	}
	return result.substring(0, result.length() - 1);
}
}
