import java.util.*;

public class Packet {

	private Header header;
	private byte[] packet;
	private NetworkAccess networkAccess;
	private Internet internet;
	private Transport transport;
	private Application application;

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
			this.transport = new Transport(this.internet.getDetails().get("ProtoC4"), this.internet.getPayload());
		else
			this.transport = new Transport(this.internet.getDetails().get("ProtoC4"), this.internet.getAssembledPayload());
	}

	public String printPacket(){
		String result = "";// Utils.addTab(printLayer(this.networkAccess.getDetails()), 1);
		//if (this.internet != null) {
		//	result += Utils.addTab(printLayer(this.internet.getDetails()), 2);
			//result += "Is frag = " + this.internet.isFrag();
		//	if (this.internet.getDetails().get("ProtoC3").equals("0800"))
		//		result += Utils.addTab(Utils.byteToHex(this.internet.getPayload()), 2);
			//if (this.internet.getAssembledPayload() != null)
			//	result += Utils.addTab(Utils.byteToHex(this.internet.getAssembledPayload()), 2);
	//		}
		//if (this.transport != null){
		//	result += Utils.addTab(printLayer(this.transport.getDetails()), 3);
		//	if (this.transport.getPayload() != null)
		//		result += Utils.addTab(Utils.byteToHex(this.transport.getPayload()), 3);
		//}
		if (this.application != null){
			if (this.application.getHttp() != null)
				result += this.application.getHttp().print();
		}
		return result;
	}

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

	public Application getApplication() {
		return application;
	}
	
	public boolean isTcpPacket(){
		return 	this.transport != null &&
			this.transport.getDetails().get("ProtoC4").equals("6");
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
		if (protoC4.equals("6") && this.transport.isStartOfTcp()){
			//new application with all the tcp conv
			String tcpStream = getAllTcpSession();
		//	System.out.println(this.header.getNumber());
			this.application = new Application(Utils.hexToByteArray(tcpStream), this.header.getNumber());
		}
		else if (protoC4.equals("17"))
			this.application = new Application(this.transport.getPayload(), this.header.getNumber());
		else
			this.application = null;
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
}
