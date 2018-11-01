import java.io.*;
import java.util.*;

public class NetworkAccess implements Printable, Connexion{
	private Map<String, String> details;
	private final Map<String, String> TYPES = Collections.unmodifiableMap(new TreeMap<String, String>() {{
       		put("0800", "IPV4");
        	put("0806", "ARP");
    	}});

NetworkAccess(byte[] frame){
	details = new LinkedHashMap<>();
	details.put("Mac_dest", Utils.byteToMac(Arrays.copyOfRange(frame, 0, 6)));
	details.put("Mac_source", Utils.byteToMac(Arrays.copyOfRange(frame, 6, 12)));
	details.put("Type", TYPES.get(Utils.byteToHex(Arrays.copyOfRange(frame, 12, 14))));
}

public boolean equals(NetworkAccess networkAccess){
	return 	this.details.get("Mac_dest").equals(networkAccess.getDetails().get("Mac_dest")) &&
			this.details.get("Mac_source").equals(networkAccess.getDetails().get("Mac_source")) &&
			this.details.get("Type").equals(networkAccess.getDetails().get("Type"));
}

public Map<String, String> getDetails(){
	return this.details;
}

public String tinyPrint(){
	return details.get("Mac_source") + " -> " + details.get("Mac_dest") + 
	       " Type :" +details.get("Type"); 
}
public String detailPrint(){
	return tinyPrint();
}

public String getSource(){
	return details.get("Mac_source");
}

public String getDest(){
	return details.get("Mac_dest");
}

public String getProtocol(){
	return "ETHERNET";
}
}
