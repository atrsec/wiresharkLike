import java.io.*;
import java.util.*;

public class NetworkAccess {
	private Map<String, String> details;

NetworkAccess(byte[] frame){
	details = new HashMap<>();
	details.put("Mac_dest", Utils.byteToMac(Arrays.copyOfRange(frame, 0, 6)));
	details.put("Mac_source", Utils.byteToMac(Arrays.copyOfRange(frame, 6, 12)));
	details.put("Type", Utils.byteToHex(Arrays.copyOfRange(frame, 12, 14)));
}

public boolean equals(NetworkAccess networkAccess){
	return 	this.details.get("Mac_dest").equals(networkAccess.getDetails().get("Mac_dest")) &&
			this.details.get("Mac_source").equals(networkAccess.getDetails().get("Mac_source")) &&
			this.details.get("Type").equals(networkAccess.getDetails().get("Type"));
}

public Map<String, String> getDetails(){
	return this.details;
}
}
