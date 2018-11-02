import java.io.*;
import java.util.*;

public class Application implements Printable{
	/*private Http http;
	private Dhcp dhcp;*/
	private boolean isPartial;
	private String reassembledPacket;
	private AppProtocol appProtocol;

	/*Get, Head, post, put, delete, connect, options, trace, patch*/
	private static final Set<String> HTTP_METHOD = new HashSet<String>(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH", "HTTP"));

//TODO delete num packet in constructor use for debug
Application(AppProtocol appProto, boolean isPartial, String reassembledPacket) {
	this.isPartial = isPartial;
	this.reassembledPacket = reassembledPacket;
	this.appProtocol = appProto;
}

public static AppProtocol buildProtocol(byte[] datagram){
	if (isHttp(datagram))
	{
		return new Http(datagram);
	}else if (isDhcp(datagram)){
		return new Dhcp(datagram);
	}else
		return null;
}

public static boolean isHttp(byte[] datagram){
		try{
			String str = new String(datagram, "US-ASCII");
			return 	HTTP_METHOD.contains(str.substring(0,3)) ||
				HTTP_METHOD.contains(str.substring(0,4)) ||
				HTTP_METHOD.contains(str.substring(0,5)) ||
				HTTP_METHOD.contains(str.substring(0,6)) ||
				HTTP_METHOD.contains(str.substring(0,7));
		} catch(Exception e){
			return false;
		}
}

public static boolean isDhcp(byte[] datagram){
	if (datagram.length > 240)
		return Utils.byteToHex(Arrays.copyOfRange(datagram, 236, 240)).equals("63825363");
	return false;
}

public String getProtocol(){
	if (this.appProtocol == null)
		return "????";
	return this.appProtocol.getProtocol();
}

public String tinyPrint(){
	if (isPartial)
		return this.reassembledPacket.split("\r\n")[0];//.replace("\n", " ");
	if (this.appProtocol != null)
		return this.appProtocol.tinyPrint();
	return "Unknown protocol";
}
public String detailPrint(){
	return null;
}
public boolean isPartial(){
	return this.isPartial;
}
public String getReassembledPacket(){
	return this.reassembledPacket;
}
}
