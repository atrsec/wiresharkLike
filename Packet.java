import java.util.*;

public class Packet {

	private Header header;
	private byte[] packet;
	private NetworkAccess networkAccess;
	private Internet internet;
	private Transport transport;
//	private Application application;

	private final int NETWORKACCESS_LENGTH = 14;
	private final int IP_LENGTH = 20;

	//TODO Send just the size of the couch not until the end
Packet(Header header, byte[] packet){
	this.header = header;
	this.packet = packet;
}

	public void parseInternet(){
		this.networkAccess = new NetworkAccess(Arrays.copyOfRange(packet, 0, NETWORKACCESS_LENGTH));
		this.internet = new Internet(this.networkAccess.getDetails().get("Type"), Arrays.copyOfRange(this.packet, NETWORKACCESS_LENGTH, this.packet.length));
	}

	public boolean sameIpPacket(Packet packet){
		return this.networkAccess.equals(packet.getNetworkAccess()) && this.internet.sameDatagram(packet.getInternet());
	}

	public Header getHeader(){
		return this.header;
	}

	public void parseTransport(){
		if (!this.internet.isFrag())
			this.transport = new Transport(this.internet.getDetails().get("ProtoC4"), this.internet.getPayload(), this.header.getNumber());
		else
			this.transport = new Transport(this.internet.getDetails().get("ProtoC4"), this.internet.getAssembledPayload(), this.header.getNumber());
	}

/*	public String printPacket(){
		String result = "";// Utils.addTab(printLayer(this.networkAccess.getDetails()), 1);
		if (this.internet != null) {
			result += Utils.addTab(printLayer(this.internet.getDetails()), 2);
			if (this.internet.getDetails().get("ProtoC3").equals("IPV4"))
				result += Utils.addTab(Utils.byteToHex(this.internet.getPayload()), 2);
		}
		if (this.transport != null){
			result += Utils.addTab(printLayer(this.transport.getDetails()), 3);
			if (this.transport.getPayload() != null)
				result += Utils.addTab(Utils.byteToHex(this.transport.getPayload()), 3);
		}
		if (this.application != null){
			if (this.application.getHttp() != null){
				result += this.application.getHttp().print();
			}
			if (this.application.getDhcp() != null){
				result += this.application.getDhcp().print();
			}
		}
		return result;
	}*/

	public String printLayer(Map<String, String> map){
			String result = "";
			for(Map.Entry<String, String> field : map.entrySet()) {
				result += field.getKey() + " = " + field.getValue() + "\n";
			}
			return result;
	}

	public NetworkAccess getNetworkAccess() {
		return networkAccess;
	}

	public Internet getInternet() {
		return internet;
	}

	public Transport getTransport() {
		return transport;
	}
/*
	public Application getApplication() {
		return application;
	}
*/	
	public boolean isTcpPacket(){
		return 	this.transport != null &&
			this.transport.getDetails().get("ProtoC4").equals("TCP");
	}

	
	public boolean sameTcpConnexion(Packet packet){
		return this.transport.sameTcpConnexion(packet.getTransport()) &&
			((this.internet.getDetails().get("IP_source").equals(packet.getInternet().getDetails().get("IP_source")) &&
			this.internet.getDetails().get("IP_dest").equals(packet.getInternet().getDetails().get("IP_dest"))) ||
			(this.internet.getDetails().get("IP_source").equals(packet.getInternet().getDetails().get("IP_dest")) &&
			this.internet.getDetails().get("IP_dest").equals(packet.getInternet().getDetails().get("IP_source"))));
	}
	
	public boolean isNextTcpPacket(Packet packet){
		return 	this.sameTcpConnexion(packet) && 
			(this.transport.getNextSequenceNumber() == Long.parseLong(packet.getTransport().getDetails().get("Num_seq")) ||
			this.transport.getNextSequenceNumber() == Long.parseLong(packet.getTransport().getDetails().get("Num_ack")));
	}

public void parseApplication(){
	String protoC4 = this.transport.getDetails().get("ProtoC4");
	if (protoC4.equals("TCP") && this.transport.isBegin()){
		String tcpStream = getAllTcpSession();
		AppProtocol appProto = Application.buildProtocol(Utils.hexToByteArray(tcpStream)); 
		if (appProto != null)
			applicationLayerForTcp(this.transport, appProto);
		else
			this.transport.setApplication(new Application(null, false, null));
	}
	else if (protoC4.equals("UDP")){
		AppProtocol appProto = Application.buildProtocol(this.transport.getPayload()); 
		if (appProto != null)
			this.transport.setApplication(new Application(appProto, false, null));
		else
			this.transport.setApplication(new Application(null, false, null));
	}
}

public String getAllTcpSession(){
	Transport transport = this.transport;
	String tcpStream = "";
	while(transport != null){
		tcpStream += Utils.byteToHex(transport.getPayload());
		transport = transport.getNext();
	}
	return tcpStream;
}

public void applicationLayerForTcp(Transport transport, AppProtocol appProto){
	Transport tmp = transport;
	int offset = 0;
	for (String request : appProto.getRequests()){
		//System.out.println("Request = " + request.length());
		offset = tmp.getPayload().length;
		while(offset < request.length()){
			//System.out.println("Offset = " + offset);
			tmp.setApplication(new Application(appProto, true, null));
			tmp = tmp.getNext();
			offset += tmp.getPayload().length;
		}
		tmp.setApplication(new Application(appProto, true, request));
		tmp = tmp.getNext();
		offset = 0;

	}
}

/*
public boolean isIcmp(){
	return this.networkAccess.getDetails().get("Type").equals("IPV4") && this.transport.getDetails().get("ProtoC4").equals("ICMP");
}

public boolean isTcpOrUdp(){
	return 	this.networkAccess.getDetails().get("Type").equals("IPV4") && 
		(this.transport.getDetails().get("ProtoC4").equals("TCP") || 
		this.transport.getDetails().get("ProtoC4").equals("UDP"));
}
*/

public String detailPrint(){
	return null;
}

public Printable lastLayer(){
	/*if(this.application != null)
		return this.application;
	else*/ 
	if(this.transport != null){
		/*if (this.transport.getApplication() != null)
			return this.transport.getApplication();*/
		return this.transport;
	}
	else if(this.internet != null)
		return this.internet;
	else if(this.networkAccess != null)
		return this.networkAccess;
	else
		return null;
}

public Connexion lastConnexionLayer(){
	if (this.internet != null)
		return this.internet;
	return this.networkAccess;
}

public String tinyPrint(int lineLength, int lnum, int ltime, int lcon, int lproto, int linfo){
	String res = String.format("|%-" + lnum + "." + lnum + "s|", this.header.getNumber());
	res += String.format("%-" + ltime + "." + (ltime - 1) + "s|", (this.header.getTimestamp() - Parser.START_CAPTURE));
	Connexion c = lastConnexionLayer();
	res += String.format("%-" + lcon + "s|", c.getSource());
	res += String.format("%-" + lcon + "s|", c.getDest());
	Printable p = lastLayer();
	res += String.format("%-" + lproto + "s|", p.getProtocol());
	String info = p.tinyPrint();
	if (info !=  null && info.length() > linfo)
		res += String.format("%-" + (linfo - 3) + "." + (linfo - 3) + "s...|", info);
	else
		res += String.format("%-" + linfo + "." + linfo + "s|", info);
	return res;
}
/*DEBUG
public void printConversation(){
	if (this.transport != null && this.transport.isBegin()){
		Transport tmp = this.transport;
		while(tmp != null){
			System.out.println(tmp.numPacket);
			tmp = tmp.getNext();
		}
		System.out.println("\n\n");
	}
}*/

}
