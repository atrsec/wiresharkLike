import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Header {
	private int number;
	private byte[] ts_sec;
	private byte[] ts_usec;
	private byte[] incl_len;
	private byte[] orig_len;

Header(byte[] header, int number){
	this.number = number;
	this.ts_sec = Arrays.copyOfRange(header, 0, 4);
	this.ts_usec = Arrays.copyOfRange(header, 4, 8);
	this.incl_len = Arrays.copyOfRange(header, 8, 12);
	this.orig_len = Arrays.copyOfRange(header, 12, 16);
}

int getLength(){
	return Utils.byteToIntLE(this.incl_len);
}

public String printHeader(){
	DateFormat df = new SimpleDateFormat("dd-MM-yyyy"); 
	Date date=null;
	try
	{
		date= df.parse("01-01-1970");
	} catch (ParseException e){
		System.out.println("Parsing date error.");
	} 
	String result = this.number + "\n";
	result += Utils.addSeconds(date, Utils.byteToIntLE(this.ts_sec)).toString();
	result += " + " + Utils.byteToIntLE(this.ts_usec) + " USEC\n";
	result += "Length of the packet = " + Utils.byteToIntLE(this.incl_len)+ " bytes \n";
	//result += Utils.byteToInt(this.orig_len)+ "\n";
	return result;
}

public int getNumber(){
	return this.number;
}

public double getTimestamp(){
	double sec = Utils.byteToIntLE(this.ts_sec);
	double usec = Utils.byteToIntLE(this.ts_usec);
	return sec + usec / 1000000;
}
}
