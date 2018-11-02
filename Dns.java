import java.io.*;
import java.util.*;

public class Dns implements AppProtocol{
	private Map<String, String> header;
	private ArrayList<String[]> queries;
	private ArrayList<String[]> answers;
	private ArrayList<String[]> authAnswers;
	private ArrayList<String[]> additionnals;
	private Map<Integer, String> compression;

	Dns(byte[] datagram) throws Exception{
		compression = new LinkedHashMap<>();
		queries = new ArrayList<String[]>();
		queries = new ArrayList<String[]>();
		queries = new ArrayList<String[]>();
		queries = new ArrayList<String[]>();
		queries = new ArrayList<String[]>();
		parseHeader(datagram);
		//System.out.println("Neww Packet :");
		//System.out.println("Parse query :");
		int offset = parseInfo(datagram, 12, Integer.parseInt(this.header.get("Nb_q")), false);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}
		System.out.println("Parse answer :");*/
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_a")), true);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}
		System.out.println("Parse auth ans :");*/
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_aa")), true);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}
		System.out.println("Parse additionnal :");*/
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_add")), true);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
	}
		
	public void parseHeader(byte[] datagram) throws Exception {
		header = new LinkedHashMap<>();
		header.put("TransacID", Utils.byteToHex(Arrays.copyOfRange(datagram, 0, 2)));
		header.put("Flags", Utils.byteToHex(Arrays.copyOfRange(datagram, 2, 4)) + "");
		header.put("Nb_q", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 4, 6)) + "");
		header.put("Nb_a", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 6, 8)) + "");
		header.put("Nb_aa", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 8, 10)) + "");
		header.put("Nb_add", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 10, 12)) + "");
	}

	public int parseInfo(byte[] datagram, int offset, int nb, boolean isAns) throws Exception {
		//int nbQ = Integer.parseInt(this.header.get("Nb_q"));
		//int offset = 12;
		for (int i = 0; i < nb; i++){
			String name = parseName(datagram, offset);
			//add the info length of parsename
			//System.out.println(name);
			if (Utils.byteToIntBE(Arrays.copyOfRange(datagram, offset, offset + 1)) > 64)
				offset += 2;
			else {
				//int count = (name.length() - name.replace(".", "").length());
				//System.out.println("Name + info = " + count);
				offset += name.length() + 1;// + count;
				//System.out.println("Offset = " + offset);
			}
			String type = Utils.byteToHex(Arrays.copyOfRange(datagram, offset, offset + 2));
			offset += 2;
			String Class = Utils.byteToHex(Arrays.copyOfRange(datagram, offset, offset+ 2));
			offset += 2;
			if (!isAns)
				this.queries.add(new String[]{name, type, Class});
			else{
				String ttl = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offset, offset+ 4)) + "";
				offset += 4;
				String data = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offset, offset+ 2)) + "";
				offset += 2;
				//TODO replace 4 by data
				String ipServ = Utils.byteToIP(Arrays.copyOfRange(datagram, offset, offset+ 4));
				offset += 4;
				this.queries.add(new String[]{name, type, Class, ttl, data, ipServ});
			}
		}
		return offset;
	}

private String parseName(byte[] datagram, int offsetInit) throws Exception{
	long info = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 1));
	if (info > 64){
		String test = Utils.byteToHex(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 2));
		System.out.println(test);
		info = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 2));
	//	System.out.println(info & 0x3F);
	//	System.out.println("Sup 64");
		return compression.get(info & 0x3F);
	}
	String test1 = Utils.byteToHex(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 2));
	System.out.println(test1);
	//System.out.println("Inf 64");
	String name = "";
	int offset = 1 + offsetInit;
	while (info != 0){
		name += new String(Arrays.copyOfRange(datagram, offset,(int)(offset + info)), "US-ASCII") + ".";
		offset += info;
		info = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offset, offset + 1));
		offset += 1;
	}
	compression.put(offsetInit, name);
	return name;
}

	public ArrayList<String> getRequests(){
		return null;
	}
	public String getProtocol(){
		return "DNS";
	}
	public String tinyPrint(){
		return "DNS";
	}
	public String detailPrint(){
		return "DNS";
	}

}
