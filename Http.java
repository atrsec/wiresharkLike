import java.util.*;
import java.math.BigInteger;

public class Http implements AppProtocol{
	private ArrayList<String> requests;
	private final Set<String> HTTP_REQUEST = new HashSet<String>(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH"));
	private final String HTTP_RESPONSE = "HTTP";


	Http(byte[] datagram) {
		try{
			String str = new String(datagram, "US-ASCII");
			requests = new ArrayList<>();
			parseHTTP(str);
		} catch(Exception e){
			//e.printStackTrace();
			
		}
	}
	
	public boolean isHttp(String method){
		return  HTTP_REQUEST.contains(method.substring(0,3)) ||
			HTTP_REQUEST.contains(method.substring(0,4)) ||
			HTTP_REQUEST.contains(method.substring(0,5)) ||
			HTTP_REQUEST.contains(method.substring(0,6)) ||
			HTTP_REQUEST.contains(method.substring(0,7)) ||
			HTTP_RESPONSE.equals(method.substring(0,4));
	}
/*
	public boolean isResponse(String datagram){
		return HTTP_RESPONSE.equals(datagram.substring(0,4));
	}
*/
	public void parseHTTP(String datagram){
		int cursor = 0;
		String newElement;
		String restOfDatagram;
		while (cursor <= datagram.length() && isHttp(restOfDatagram = datagram.substring(cursor, datagram.length()))){
			//restOfDatagram = datagram.substring(cursor, datagram.length());
			String request = restOfDatagram.split("\r\n\r\n")[0] + "\r\n\r\n";
			cursor += request.length();
			String body = getBody(request, restOfDatagram.substring(request.length(), restOfDatagram.length()));
			if (body != null){
				cursor += body.length();
				this.requests.add(request + body);
			}else
				this.requests.add(request);
		}
	}

	public String getBody(String request, String rest){
		int bodyLength = 0;
		if (request.toLowerCase().contains("content-length")){
			bodyLength = getContentLength(request);
			//System.out.println("Content-Length = " + bodyLength);
		}
		else if(!request.toLowerCase().contains("content-length") && isTransferEncoding(request))
			bodyLength = rest.indexOf("0\r\n\r\n") + 5;
		if (bodyLength == 0)
			return null;
		return rest.substring(0, bodyLength);
	}

	int getContentLength(String datagram){
		int indexContent = datagram.toLowerCase().indexOf("content-length");
		int indexClrf = datagram.indexOf("\r\n", indexContent);
		if (indexClrf == -1)
			indexClrf = datagram.length();
		String header = datagram.substring(indexContent, indexClrf);
		return Integer.parseInt(header.split(":")[1].replaceAll("\\s+",""));
	}

	public boolean isTransferEncoding(String datagram){
		return datagram.toLowerCase().contains("transfer-encoding: chunked");
	}

	public String tinyPrint(){
		String http = "";
		for (String elt : this.requests){
				http += elt.split("\n")[0] + "\n";
		}
		return http;
	}
	public String detailPrint(){
		return null;
	}
	
	public String getProtocol(){
		return "HTTP";
	}
	
	public ArrayList<String> getRequests(){
		return this.requests;
	}
}
