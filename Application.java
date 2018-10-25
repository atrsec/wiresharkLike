import java.io.*;
import java.util.*;

public class Application {
	private Map<String, String> details;
	private Http http;
	private Dhcp dhcp;

	/*Get, Head, post, put, delete, connect, options, trace, patch*/
	private final Set<String> HTTP_METHOD = new HashSet<String>(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH", "HTTP"));

//TODO delete num packet in constructor use for debug
Application(byte[] datagram, int num) {
	String proto = recognizeProto(datagram);
	if (proto.equals("HTTP"))
	{
		this.http = new Http(datagram);
	}else if (proto.equals("DHCP")){
		this.dhcp = new Dhcp(datagram);
	}
	//else
	//	System.out.println(proto);
	//this.details = new HashMap<String, String>();
	//details.put("All", getHttp(datagram));
}


public String recognizeProto(byte[] datagram){
	if (isHttp(datagram)){
		return "HTTP";
	}
	else if (isDhcp(datagram))
		return "DHCP";
	return "NSP";
		
}

public Map<String, String> getDetails(){
	return this.details;
}

public boolean isHttp(byte[] datagram){
		try{
			String str = new String(datagram, "US-ASCII");
		/*	System.out.println(str.substring(0,3));
			System.out.println(str.substring(0,4));
			System.out.println(str.substring(0,5));
			System.out.println(str.substring(0,6));
			System.out.println(str.substring(0,7));*/
			return 	HTTP_METHOD.contains(str.substring(0,3)) ||
				HTTP_METHOD.contains(str.substring(0,4)) ||
				HTTP_METHOD.contains(str.substring(0,5)) ||
				HTTP_METHOD.contains(str.substring(0,6)) ||
				HTTP_METHOD.contains(str.substring(0,7));
		} catch(Exception e){
			//e.printStackTrace();
			return false;
		}
}

public boolean isDhcp(byte[] datagram){
	if (datagram.length > 240)
		return Utils.byteToHex(Arrays.copyOfRange(datagram, 236, 240)).equals("63825363");
	return false;
}

public Http getHttp(){
	return this.http;
}

}
