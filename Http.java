import java.util.*;
import java.math.BigInteger;

public class Http{
	private ArrayList<String[]> requests;
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
	
	public boolean isRequest(String method){
		return  HTTP_REQUEST.contains(method.substring(0,3)) ||
			HTTP_REQUEST.contains(method.substring(0,4)) ||
			HTTP_REQUEST.contains(method.substring(0,5)) ||
			HTTP_REQUEST.contains(method.substring(0,6)) ||
			HTTP_REQUEST.contains(method.substring(0,7));
	}

	public boolean isResponse(String datagram){
		return HTTP_RESPONSE.equals(datagram.substring(0,4));
	}

	public void parseHTTP(String datagram){
		int cursor = 0;
		String newElement;
		String restOfDatagram;
		while (cursor <= datagram.length()){
			restOfDatagram = datagram.substring(cursor, datagram.length());
			String request = restOfDatagram.split("\r\n\r\n")[0];
			cursor += request.length() + 4;
			String body = getBody(request, restOfDatagram.substring(request.length() + 4, restOfDatagram.length()));
			if (body != null)
				cursor += body.length();
			String[] dial = new String[2];
			dial[0] = request;
			dial[1] = body;
			this.requests.add(dial);
		}
	}

	public String getBody(String request, String rest){
		int bodyLength = 0;
		if (request.contains("Content-Length"))
			bodyLength = getContentLength(request);
		else if(!request.contains("Content-Length") && isTransferEncoding(request))
			bodyLength = rest.indexOf("0\r\n\r\n") + 5;
		if (bodyLength == 0)
			return null;
		return rest.substring(0, bodyLength);
	}

	int getContentLength(String datagram){
		int indexContent = datagram.indexOf("Content-Length");
		int indexClrf = datagram.indexOf("\r\n", indexContent);
		if (indexClrf == -1)
			indexClrf = datagram.length();
		String header = datagram.substring(indexContent, indexClrf);
		return Integer.parseInt(header.split(":")[1].replaceAll("\\s+",""));
	}

	public boolean isTransferEncoding(String datagram){
		return datagram.contains("Transfer-Encoding: chunked");
	}

	public String print(){
		String http = "";
		for (String[] elt : this.requests){
				http += elt[0].split("\n")[0] + "\n";
				if (elt[1] != null){
					http += "body Present\n";
				}
		}
		return http;
	}

	public ArrayList<String[]> getRequests(){
		return requests;
	}
}
