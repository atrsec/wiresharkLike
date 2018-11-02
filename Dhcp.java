import java.util.*;

public class Dhcp implements AppProtocol{

	//TODO LinkedHashMap
	private Map<String, String> details;
	private Map<String, String> options;

Dhcp(byte[] datagram){
	this.details = new LinkedHashMap<String, String>();
	this.details.put("op", (datagram[0] & 0xFF) + "");
	this.details.put("htype", String.format("%02X", datagram[1]));
	this.details.put("hlen", (datagram[2] & 0xFF) + "");
	this.details.put("hops", (datagram[3] & 0xFF) + "");
	this.details.put("TransacId", Utils.byteToHex(Arrays.copyOfRange(datagram, 4, 8)));
	this.details.put("Seconds", Utils.byteToHex(Arrays.copyOfRange(datagram, 8, 10)));
	this.details.put("Flags", Utils.byteToHex(Arrays.copyOfRange(datagram, 10, 12)));
	this.details.put("IpClient", Utils.byteToIP(Arrays.copyOfRange(datagram, 12, 16)));
	this.details.put("FutureIpClient", Utils.byteToIP(Arrays.copyOfRange(datagram, 16, 20)));
	this.details.put("IpNextServ", Utils.byteToIP(Arrays.copyOfRange(datagram, 20, 24)));
	this.details.put("IpRelay", Utils.byteToIP(Arrays.copyOfRange(datagram, 24, 28)));
	this.details.put("MacClient", Utils.byteToMac(Arrays.copyOfRange(datagram, 28, 34)));
	this.details.put("ServerName", Utils.byteToHex(Arrays.copyOfRange(datagram, 44, 108)));
	this.details.put("File", Utils.byteToHex(Arrays.copyOfRange(datagram, 108, 236)));
	this.details.put("MagicCookie", Utils.byteToHex(Arrays.copyOfRange(datagram, 236, 240)));
	parseOptions(Arrays.copyOfRange(datagram, 240, datagram.length));
}

private void parseOptions(byte[] datagram){
	options = new LinkedHashMap<String, String>();
	int cursor = 0;
	int length;
	int code = (int)Utils.byteToIntBE(Arrays.copyOfRange(datagram, cursor, cursor + 1));
	while (code != 255){
		length = (int)Utils.byteToIntBE(Arrays.copyOfRange(datagram, cursor + 1, cursor + 2));
		this.options.put(code + "", Utils.byteToHex(Arrays.copyOfRange(datagram, cursor + 2, cursor + 2 + length)));
		cursor += 2 + length;
		code = (int)Utils.byteToIntBE(Arrays.copyOfRange(datagram, cursor, cursor + 1));
	}
	this.options.put(code + "", "0");
}

public String detailPrint(){
	String result = "";
	for (Map.Entry<String, String> detail : this.details.entrySet())
		result += detail.getKey() + " = " + detail.getValue() + "\n";
	for (Map.Entry<String, String> option : this.options.entrySet())
		result += option.getKey() + " = " + option.getValue() + "\n";
	return result;
}

public String tinyPrint(){
	return "DHCP";
}

public String getProtocol(){
	return "DHCP";
}

public ArrayList<String> getRequests(){
	return null;
}
}

