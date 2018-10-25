import java.util.*;

public class Dhcp{

	//TODO LinkedHashMap
	private Map<String, String> details;
	private Map<String, String> options;

Dhcp(byte[] datagram){
	this.details.put("op", (datagram[0] & 0xFF) + "");
	this.details.put("htype", String.format("%02X", datagram[1]));
	this.details.put("hlen", (datagram[2] & 0xFF) + "");
	this.details.put("hops", (datagram[3] & 0xFF) + "");
	this.details.put("TransacId", Utils.byteToHex(Arrays.copyOfRange(datagram, 4, 8)));
	this.details.put("Seconds", Utils.byteToHex(Arrays.copyOfRange(datagram, 8, 10)));
	this.details.put("Flags", Utils.byteToHex(Arrays.copyOfRange(datagram, 10, 12)));
	this.details.put("IpClient", Utils.byteToIp(Arrays.copyOfRange(datagram, 12, 16)));
	this.details.put("FutureIpClient", Utils.byteToIp(Arrays.copyOfRange(datagram, 16, 20)));
	this.details.put("IpNextServ", Utils.byteToIp(Arrays.copyOfRange(datagram, 20, 24)));
	this.details.put("IpRelay", Utils.byteToIp(Arrays.copyOfRange(datagram, 24, 28)));
	this.details.put("MacClient", Utils.byteToMac(Arrays.copyOfRange(datagram, 28, 44)));
	this.details.put("ServerName", Utils.byteToChar(Arrays.copyOfRange(datagram, 44, 108)));
	this.details.put("File", Utils.byteToChar(Arrays.copyOfRange(datagram, 108, 236)));
}
}
}
