import java.io.*;
import java.util.*;

public class Application implements Printable{
	private Http http;
	private Dhcp dhcp;

	/*Get, Head, post, put, delete, connect, options, trace, patch*/
	private final Set<String> HTTP_METHOD = new HashSet<String>(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH", "HTTP"));

//TODO delete num packet in constructor use for debug
Application(byte[] datagram, int num) {
	//String proto = recognizeProto(datagram);
	if (isHttp(datagram))
	{
		this.http = new Http(datagram);
	}else if (isDhcp(datagram)){
		this.dhcp = new Dhcp(datagram);
	}
	//else
	//	System.out.println(proto);
	//this.details = new HashMap<String, String>();
	//details.put("All", getHttp(datagram));
}

public boolean isHttp(byte[] datagram){
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

public boolean isDhcp(byte[] datagram){
	if (datagram.length > 240)
		return Utils.byteToHex(Arrays.copyOfRange(datagram, 236, 240)).equals("63825363");
	return false;
}

public Http getHttp(){
	return this.http;
}

public Dhcp getDhcp(){
	return this.dhcp;
}

public String getProtocol(){
	if (this.http != null)
		return "HTTP";
	else if (this.dhcp != null)
		return "DHCP";
	else
		return "NSP";
}
public String tinyPrint(){
	if (this.http != null)
		return this.http.tinyPrint();
	return null;
}
public String detailPrint(){
	return null;
}
}
