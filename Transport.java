import java.io.*;
import java.util.*;

public class Transport {
	private Map<String, String> details;
	private byte[] payload;
	private byte[] AssembledPayload;
	private Transport nextTransport;
	private boolean alone;
	private boolean alreadyTreat;

Transport(String protoC4, byte[] datagram){
	this.details = new HashMap<>();
	this.details.put("ProtoC4", protoC4);
	this.alone = true;
	this.alone = false;
	if (protoC4.equals("1"))
		getICMP(datagram);
	else if (protoC4.equals("6"))
		getTCP(datagram);
	else if (protoC4.equals("17"))
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

//TODO Delete if not use
public boolean isAlone(){
	return this.alone;
}

public void setAlone(boolean b){
	this.alone = b;
}

public boolean isStartOfTcp(){
	return 	this.details.get("ProtoC4").equals("6") &&
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

}
