import java.io.*;
import java.util.*;

class WiresharkLike {

public static void main (String[] args){
		Parser parser = new Parser(args[0]);;
		Printer printer = new Printer();
		parser.parse();
	/*	int res_parsing = parser.parseDatagrams();
		if (res_parsing != 0){
			System.err.println("Erreur dans le parsing, code = " + res_parsing);
			return;
		}
		parser.parsePackets();
		parser.parseApplication();*/
		
//		System.out.println(printer.printGlobalHeader(parser.getGlobalHeader()));
		System.out.println(printer.printPackets(parser.getPackets()));
	//	System.out.println(printer.printByProto(parser.getPackets(), "TODO"));
	/*	for(Packet p : parser.getPackets()){
			p.printConversation();
		}*/
		//Packet p = parser.getPackets().get(17);
		//System.out.println(p.getApplication().getHttp().tinyPrint());
}
}
