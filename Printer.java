import java.util.*;
import java.io.*;
import java.lang.*;

public class Printer{

//TODO Options
	private final int LINELENGTH = 125;
	private final int LNUM = 5;
	private final int LTIME = 10;
	private final int LCON = 19;
	private final int LPROTO = 5;
	private final int LINFO = 60;

public String printGlobalHeader(byte[][] globalHeader){
	//TODO LE OR BE
	String result = "---------------------- Global Header ---------------------------\n";
	result += "Magic Number: " + Utils.byteToHex(globalHeader[0]) + "\n";
	result += "Version du PCAP: " + Utils.byteToIntLE(globalHeader[1]) + ".";
	result += Utils.byteToIntLE(globalHeader[2]) + "\n";
	result += "Snapshot length: " + Utils.byteToIntLE(globalHeader[3]) + "\n";
	result += "Network: " + Utils.byteToIntLE(globalHeader[4]) + "\n";
	result += "----------------------------------------------------------------\n";
	return result;
}

public String printPackets(ArrayList<Packet> packets, String filter){
	String result = firstLine();
	boolean display = true;
	for(Packet p : packets){
		if (filter != null){
			if (!p.getAllProtocol().contains(filter.toUpperCase()))
				display = false;
		}
		if (display == true){
			result += p.tinyPrint(LINELENGTH, LNUM, LTIME, LCON, LPROTO, LINFO);
			result += "\n";
			for (int i = 0; i < LINELENGTH; i++)
				result += "-";
			result += "\n";
		}
		display = true;
	}
	return result;
}

public String firstLine(){
	String result = "";
	for (int i = 0; i < LINELENGTH; i++)
		result += "-";
	result += "\n";
	result += String.format("|%-" + LNUM + "." + LNUM + "s|", "N°");
	result += String.format("%-" + LTIME + "." + (LTIME - 1) + "s|", "TimeStamp");
	result += String.format("%-" + LCON + "s|", "Source");
	result += String.format("%-" + LCON + "s|", "Destination");
	result += String.format("%-" + LPROTO + "s|", "Proto");
	result += String.format("%-" + LINFO + "s|", "Information");
	result += "\n";
	for (int i = 0; i < LINELENGTH; i++)
		result += "-";
	result += "\n";
	return result;
	
}

public String printPacketDetail(ArrayList<Packet> packets, int number){
	String result = "";
	if (number >= packets.size() || number < 0)
		return "This packet number doesn't exist.";
	for (int i = 0; i < LINELENGTH; i++)
		result += "-";
	result += "\n";
	result += packets.get(number - 0).detailPrint();
	result += "\n";
	for (int i = 0; i < LINELENGTH; i++)
		result += "-";
	result += "\n";
	return result;
}

public String printConversation(ArrayList<Packet> packets, int begin){
	if (begin >= packets.size() || begin < 0)
		return "This packet number doesn't exist.";
	ArrayList<Integer> numPackets = getConvNumber(packets, begin);
	String result = firstLine();
	for(int n : numPackets){
		result += packets.get(n).tinyPrint(LINELENGTH, LNUM, LTIME, LCON, LPROTO, LINFO);
		result += "\n";
		for (int i = 0; i < LINELENGTH; i++)
			result += "-";
		result += "\n";
	}
	return result;
}

public ArrayList<Integer> getConvNumber(ArrayList<Packet> packets, int begin){
	ArrayList<Integer> numPackets = new ArrayList<>();
	Packet p = packets.get(begin);
	if (p.getTransport() == null)
		return numPackets; 
	Transport t = p.getTransport();
	while (t != null){
		numPackets.add(t.num - 1);
		t = t.getNext();
	}
	return numPackets;
}

public static String printLayer(Map<String, String> map){
	String result = "";
	for(Map.Entry<String, String> field : map.entrySet()) {
		result += field.getKey() + " = " + field.getValue() + "\n";
	}
	return result;
}

}
