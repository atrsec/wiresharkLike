import java.util.*;
import java.io.*;

public class Printer{

//TODO Options

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

public String printPackets(ArrayList<Packet> packets){
	String result = "";
	for(Packet p : packets){
		result += "----------------------------------------------------------------\n";
//		result += p.getHeader().printHeader();
		result += p.getHeader().getNumber() + "\n";
		result += p.printPacket();
		/*if (p.getInternet().getNextInternet() != null) {
			result += p.getInternet().getNextInternet().getDetails().get("Identification") + "\n";
			result += p.getInternet().getNextInternet().getDetails().get("Length") + "\n";
		}*/
	/*	if (p.getApplication() != null && p.getApplication().getHttp() != null){
		Http h = p.getApplication().getHttp();
		int i = 1;
		for (String s : h.getRequests()){
			result += i + ": ";
			result += h.contentLength(s) + "\n";
			i++;
		}
		}*/
		result += "----------------------------------------------------------------\n";
	}
	return result;
}
}
