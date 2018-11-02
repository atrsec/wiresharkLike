import java.io.*;
import java.util.*;

public class Transport implements Printable{
	private Map<String, String> details;
	private byte[] payload;
	//private byte[] AssembledPayload;
	private Transport nextTransport;
	private boolean begin;
	private boolean alreadyTreat;
	//TODO
	private boolean error;
	//TODO Better
	private Application application;
	public int num;
	private final Map<String, String> ICMP_TYPE = Collections.unmodifiableMap(new TreeMap<String, String>(){{
			put("0", "Echo reply");
			put("3", "Destination unreachable");
			put("4", "Source quench");
			put("5", "Redirect");
			put("8", "Echo");
			put("9", "Router advertisement");
			put("10", "Router selection");
			put("11", "Time exceeded");
			put("12", "Parameter problem");
			put("13", "Timestamp");
			put("14", "Timestamp reply");
			put("15", "Information request");
			put("16", "Information reply");
			put("17", "Address mask request");
			put("18", "Address mask reply");
			put("30", "Traceroute");
	}});

Transport(String protoC4, byte[] datagram, int num){
	this.num = num;
	this.details = new LinkedHashMap<>();
	this.details.put("ProtoC4", protoC4);
	this.begin = true;
	if (protoC4.equals("ICMP"))
		getICMP(datagram);
	else if (protoC4.equals("TCP"))
		getTCP(datagram);
	else if (protoC4.equals("UDP"))
		getUDP(datagram);
}

public void getICMP(byte[] datagram){
	this.details.put("Type", (datagram[0] & 0xFF) + "");
	this.details.put("Code", (datagram[1] & 0xFF) + "");
	this.details.put("Identifier", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 4, 6)) + "");
	this.details.put("Sequence_number", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 6, 8)) +"");
}

public void getTCP(byte[] datagram){
	this.details.put("Port_source", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 0, 2)) + "");
	this.details.put("Port_dest", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 2, 4)) + "");
	this.details.put("Num_seq", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 4, 8)) + "");
	this.details.put("Num_ack", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 8, 12)) + "");
	int offset = (datagram[12] & 255) >> 4;// & 0x0F) >> 4;
	this.details.put("Offset", offset + "");
	this.details.put("FLAG_URG", ((datagram[13] >> 5) & 0x01) + "");
	this.details.put("FLAG_ACK", ((datagram[13] >> 4) & 0x01) + "");
	this.details.put("FLAG_PSH", ((datagram[13] >> 3) & 0x01) + "");
	this.details.put("FLAG_RST", ((datagram[13] >> 2) & 0x01) + "");
	this.details.put("FLAG_SYN", ((datagram[13] >> 1) & 0x01) + "");
	this.details.put("FLAG_FIN", (datagram[13] & 0x01) + "");
	this.details.put("Windows", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 14, 16)) + "");
	this.payload = Arrays.copyOfRange(datagram, offset * 4, datagram.length);
}

public void getUDP(byte[] datagram){
	this.details.put("Port_source", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 0, 2)) + "");
	this.details.put("Port_dest", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 2, 4)) + "");
	this.details.put("Length", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 4, 6)) + "");
	this.details.put("Transport_length", "8");
	this.payload = Arrays.copyOfRange(datagram, 8, datagram.length);
}

public Map<String, String> getDetails() {
	return details;
}

public byte[] getPayload(){
	return this.payload;
}

public void setError(boolean b){
	this.error = b;
}

public boolean getError(){
	return this.error;
}

public boolean isBegin(){
	return this.begin;
}

public void setBegin(boolean b){
	this.begin = b;
}
public Application getApplication(){
	return this.application;
}

public void setApplication(Application application){
	this.application = application;
}

public boolean isStartOfTcp(){
	return 	this.details.get("ProtoC4").equals("TCP") &&
		this.details.get("FLAG_SYN").equals("1") &&
		this.details.get("FLAG_FIN").equals("0") &&
		this.details.get("FLAG_PSH").equals("0") &&
		this.details.get("FLAG_RST").equals("0") &&
		this.details.get("FLAG_ACK").equals("0") &&
		this.details.get("FLAG_URG").equals("0");
}

public boolean sameTcpConnexion(Transport transport){
	return 	this != transport &&
		(this.details.get("Port_source").equals(transport.getDetails().get("Port_source")) &&
		this.details.get("Port_dest").equals(transport.getDetails().get("Port_dest"))) ||
		(this.details.get("Port_source").equals(transport.getDetails().get("Port_dest")) &&
		this.details.get("Port_dest").equals(transport.getDetails().get("Port_source")));
		
}

public long getNextSequenceNumber(){
	long actualSeq = Long.parseLong(this.details.get("Num_seq"));
	if (this.details.get("FLAG_SYN").equals("1") || this.details.get("FLAG_FIN").equals("1"))
		return actualSeq + 1;
	return actualSeq + this.payload.length;
}

public Transport getNext(){
	return nextTransport;
}

public void setNext(Transport transport){
	this.nextTransport = transport;
}

public void setAlreadyTreat(boolean b){
	this.alreadyTreat = b;
}
public boolean getAlreadyTreat(){
	return this.alreadyTreat;
}

public boolean isTcp(){
	return this.details.get("ProtoC4").equals("TCP");
}

public String tinyPrint(){
	if (this.application == null || (this.application.isPartial() && this.application.getReassembledPacket() == null)){
		if (this.details.get("ProtoC4").equals("ICMP")){
			return ICMP_TYPE.get(this.details.get("Type"));
		}
		else/*if (!this.details.get("ProtoC4").equals("ICMP"))*/{
			String res = this.details.get("Port_source") + " => " + 
				this.details.get("Port_dest") + " [ ";
			if (!this.details.get("ProtoC4").equals("UDP")){
				if (this.details.get("FLAG_SYN").equals("1"))
					res += "SYN ";
				if (this.details.get("FLAG_FIN").equals("1"))
					res += "FIN ";
				if (this.details.get("FLAG_PSH").equals("1"))
					res += "PSH ";
				if (this.details.get("FLAG_ACK").equals("1"))
					res += "ACK ";
				if (this.details.get("FLAG_RST").equals("1"))
					res += "RST ";
				if (this.details.get("FLAG_URG").equals("1"))
					res += "URG ";
				res += "] Seq=";
				res += this.details.get("Num_seq");
				res += " Ack=" + this.details.get("Num_ack");
				res += " Win=" + this.details.get("Windows");
			res += " Len=" + this.payload.length;
			}
			return res;
		}
	}
	return application.tinyPrint();
}
public String detailPrint(){
	return null;
}
public String getProtocol(){
	if (this.application == null || (this.application.isPartial() && this.application.getReassembledPacket() == null))
		return this.details.get("ProtoC4");
	return application.getProtocol();
}

}
