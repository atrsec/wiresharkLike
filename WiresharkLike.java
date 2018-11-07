import java.io.*;
import java.util.*;

class WiresharkLike {

public static void main (String[] args){
		if (args == null || args.length != 3 
				 && args.length != 1
				 || (args.length == 3 && !args[1].equals("-o"))){
			System.err.println("Usage:\njava Wiresharklike PCAPFILE [-o OUTPUTFILE]\n");
			System.exit(1);
		}
		Parser parser = new Parser(args[0]);;
		Printer printer = new Printer();
		parser.parse();
		if (args.length == 3){
			try {
				PrintWriter logFile = new PrintWriter( new BufferedWriter (new FileWriter(args[2])));
				logFile.write(printer.printPackets(parser.getPackets(), null));
				logFile.close();
			}catch(Exception e){
				System.out.println("Impossible to open " + args[2] + " !");
				System.exit(1);
			}
			return;
		}
		System.out.println(printer.printPackets(parser.getPackets(), null));
		boolean loop = true;
		Scanner sc = new Scanner(System.in);
		while(loop){
			String cmd = sc.nextLine();
			try {
				if (cmd.equals("q") || cmd.equals("quit"))
					loop = false;
				else if (cmd.split(" ")[0].equals("d"))
					System.out.println(printer.printPacketDetail(parser.getPackets(), Integer.parseInt(cmd.split(" ")[1]) - 1));
				else if (cmd.split(" ")[0].equals("c"))
					System.out.println(printer.printConversation(parser.getPackets(), Integer.parseInt(cmd.split(" ")[1]) - 1));
				else if (cmd.split(" ")[0].equals("f"))
					System.out.println(printer.printPackets(parser.getPackets(), cmd.split(" ")[1]));
				else
					System.err.println("Syntax error");
			}catch(Exception e){
				e.printStackTrace();
				System.err.println("Syntax error");
			}
			
		}
}
}
