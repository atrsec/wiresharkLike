import java.util.*;
import java.io.*;
import java.lang.*;

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
	int lineLength = 125;
	int lnum = 5;
	int ltime = 10;
	int lcon = 19;
	int lproto = 5;
	int linfo = 60;
	String result = firstLine(lineLength, lnum, ltime, lcon, lproto, linfo);
	for(Packet p : packets){
		//if (p.getInternet() != null)
		//	System.out.println(p.getInternet().getDetails().get("ProtoC4"));
		result += p.tinyPrint(lineLength, lnum, ltime, lcon, lproto, linfo);
		result += "\n";
		for (int i = 0; i < lineLength; i++)
			result += "-";
		result += "\n";
	}
	return result;
}

public String firstLine(int lineLength, int lnum, int ltime, int lcon, int lproto, int linfo){
	String result = "";
	for (int i = 0; i < lineLength; i++)
		result += "-";
	result += "\n";
	result += String.format("|%-" + lnum + "." + lnum + "s|", "N°");
	result += String.format("%-" + ltime + "." + (ltime - 1) + "s|", "TimeStamp");
	result += String.format("%-" + lcon + "s|", "Source");
	result += String.format("%-" + lcon + "s|", "Destination");
	result += String.format("%-" + lproto + "s|", "Proto");
	result += String.format("%-" + linfo + "s|", "Information");
	result += "\n";
	for (int i = 0; i < lineLength; i++)
		result += "-";
	result += "\n";
	return result;
	
}

public String printByProto(ArrayList<Packet> packets, String filter){
	String result = "";
	for(Packet p : packets){
		//result += p.printSrcDst() + "\n\n";
	}
	return result;
}

}
