import java.io.*;
import java.util.*;

class Parser{

final int GLOBALHEADER_LENGTH = 24;
final int PACKETHEADER_LENGTH = 16;
private byte[] pcap;
private Boolean littleEndian;
private byte[][] globalHeader;
private ArrayList<Packet> packets;

Parser(String filepath){
	try{
		InputStream pcap = new FileInputStream(filepath);
		this.pcap = pcap.readAllBytes();
		pcap.close();
	}
	catch(IOException e){
		System.out.println("Le fichier n'existe pas.");
	}
	this.packets = new ArrayList<>();
}

//Return a two dimensions array of bytes if the pcap is fine and the network is ethernet, null otherwise
//TODO Handle null
public int parseGlobalHeader(){
	byte[][] globalHeaderParsed = new byte[5][4];
	//Array of the whole global header
	byte[] globalHeader = Arrays.copyOfRange(this.pcap, 0, GLOBALHEADER_LENGTH);
	//Magic number
	globalHeaderParsed[0] = Arrays.copyOfRange(globalHeader, 0, 4);
	if (!checkMagicNumber(globalHeaderParsed[0]))
		return 1;
	//Major version number	
	globalHeaderParsed[1] = Arrays.copyOfRange(globalHeader, 4, 6);
	//Minor version number	
	globalHeaderParsed[2] = Arrays.copyOfRange(globalHeader, 6, 8);
	//Length of the pcap
	globalHeaderParsed[3] = Arrays.copyOfRange(globalHeader, 16, 20);
	//Network
	globalHeaderParsed[4] = Arrays.copyOfRange(globalHeader, 20, 24);
	if (Utils.byteToIntLE(globalHeaderParsed[4]) != 1)
		return 2; 
	this.globalHeader = globalHeaderParsed;
	return 0;
} 

public int parseDatagrams(){

	int res = parseGlobalHeader();
	if (res != 0)
		return res;
	int cursor = GLOBALHEADER_LENGTH;
	int count = 1;
	while (this.pcap.length - cursor - PACKETHEADER_LENGTH >= 0){
		Header header = new Header(Arrays.copyOfRange(this.pcap, cursor, cursor + PACKETHEADER_LENGTH), count);
		cursor += PACKETHEADER_LENGTH;
		//Handle if pcap is malformed
		if (this.pcap.length - cursor - header.getLength() < 0)
			return 3;
		parseIp(header, Arrays.copyOfRange(this.pcap, cursor, cursor + header.getLength()));
		cursor += header.getLength();
		count++;
	}
	assembleIp();
	return 0;
	//TODO LSEEK(0) to start if option wthout global header active	
	//TODO Handle error code of global error	
}


public void parseIp(Header header, byte[] packet){
	Packet newPacket = new Packet(header, packet);
	newPacket.parseInternet();

	if (newPacket.getInternet().getDetails().get("ProtoC3").equals("0800") && newPacket.getInternet().getDetails().get("Dont_fragment").equals("0")){
		setPrecIpPacket(newPacket);
	}
	this.packets.add(newPacket);
}



public void assembleIp(){
		ArrayList<String> alreadyDone = new ArrayList<>();
		for(Packet p : this.packets){
			if (p.getInternet().isFrag() && !alreadyDone.contains(p.getInternet().getId())){
				p.getInternet().setAssembledPayload();
				alreadyDone.add(p.getInternet().getId());
			}
		}
}

public void setPrecIpPacket(Packet newPacket){
		for(Packet p : packets){
			if (p.sameIpPacket(newPacket)){
				p.getInternet().findLastPacketOfChain().setNextInternet(newPacket.getInternet());
				newPacket.getInternet().setFrag(true);
				p.getInternet().setFrag(true);
				return;
			}
		}
		newPacket.getInternet().setFrag(false);
}

public void parsePackets(){
	for(Packet p : this.packets){
		if (p.getNetworkAccess().getDetails().get("Type").equals("0800"))
			if(!p.getInternet().isFrag() || p.getInternet().getAssembledPayload() != null){
				p.parseTransport();
		}
	}
	assemblePacket();
}

public void assemblePacket(){
	for (Packet p : this.packets){
		if (p.isTcpPacket() && p.getTransport().isStartOfTcp()){
			findNextTcpPacket(p);
		}
	}
}

public void findNextTcpPacket(Packet packet){
	//packet.getTransport().setAlone(false);
	//System.out.println("---------------" + packet.getHeader().getNumber());
	packet.getTransport().setAlreadyTreat(true);
	for (Packet p : this.packets){
		if (p.isTcpPacket() && packet.isNextTcpPacket(p) && !p.getTransport().getAlreadyTreat()){
		//	System.out.println(p.getHeader().getNumber());
			packet.getTransport().setNext(p.getTransport());
			findNextTcpPacket(p);
			return;
		}
	}
}


public void parseApplication(){
	for (Packet p : this.packets){
		if (p.getTransport() != null && !p.getTransport().getDetails().get("ProtoC4").equals("1")){
			p.parseApplication();
		}
	}
}

public Boolean checkMagicNumber(byte[] magicNumber){
		if (Utils.byteToHex(magicNumber).equals("D4C3B2A1")){
			this.littleEndian = true;
			return true;
		} else if(Utils.byteToHex(magicNumber).equals("A1B2C3D4")){
			this.littleEndian = false;
			return true;
		} else
			return false;
}

	public byte[][] getGlobalHeader(){
		return this.globalHeader;
	}

	public ArrayList<Packet> getPackets(){
		return this.packets;
	}

}
