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
		answers = new ArrayList<String[]>();
		authAnswers = new ArrayList<String[]>();
		additionnals = new ArrayList<String[]>();
		parseHeader(datagram);
		//System.out.println("Neww Packet :");
		System.out.println("Parse query :");
		int offset = parseInfo(datagram, 12, Integer.parseInt(this.header.get("Nb_q")), false, this.queries);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
		System.out.println("Parse answer :");
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_a")), true, this.answers);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
		System.out.println("Parse auth ans :");
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_aa")), true, this.authAnswers);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
		System.out.println("Parse additionnal :");
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_add")), true, this.additionnals);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
	}
		
	public void parseHeader(byte[] datagram) throws Exception {
		header = new LinkedHashMap<>();
		header.put("TransacID", Utils.byteToHex(Arrays.copyOfRange(datagram, 0, 2)));
		header.put("Flags", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 2, 4)) + "");
		header.put("Nb_q", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 4, 6)) + "");
		header.put("Nb_a", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 6, 8)) + "");
		header.put("Nb_aa", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 8, 10)) + "");
		header.put("Nb_add", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 10, 12)) + "");
	}

	public int parseInfo(byte[] datagram, int offset, int nb, boolean isAns, ArrayList<String[]> ans) throws Exception {
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
				String data = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offset, offset + 2)) + "";
				offset += 2;
				//TODO replace 4 by data
				String ipServ = Utils.byteToHex(Arrays.copyOfRange(datagram, offset, offset + Integer.parseInt(data)));
				offset += Integer.parseInt(data);
				ans.add(new String[]{name, type, Class, ttl, data, ipServ});
			}
		}
		return offset;
	}

private String parseName(byte[] datagram, int offsetInit) throws Exception{
	long info = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 1));
	if (info > 64){
		String test = Utils.byteToHex(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 2));
		info = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 2));
	//	System.out.println(info & 0x3F);
	//	System.out.println("Sup 64");
		System.out.println("-------------------------");
		for(Map.Entry<Integer, String> field : compression.entrySet()) 
			System.out.println(field.getKey() + " = " + field.getValue());
		System.out.println((info & 0x3FFF));
		System.out.println(compression.get(info & 0x3FFF));
		System.out.println(compression.get(12));
		return compression.get((int)(info & 0x3FFF));
	}
	//String test1 = Utils.byteToHex(Arrays.copyOfRange(datagram, offsetInit, offsetInit + 2));
	//System.out.println(test1);
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

	//TODO
	//print of type and ipserv

	public ArrayList<String> getRequests(){
		return null;
	}
	public String getProtocol(){
		return "DNS";
	}
	public String tinyPrint(){
		String res = "";
		//Response
		if ((Integer.parseInt(this.header.get("Flags")) >> 15) == 1){
			res += "Response " + this.header.get("TransacID") + " ";
			for (String[] a : this.answers)
				res += a[1] + " " + a[0] + " " + a[5] + " ";
		}
		else{
			res += "Question " + this.header.get("TransacID") + " ";
			for (String[] q : this.queries)
				res += q[1] + " " + q[0] + " ";
		}
		return res;
	}
	public String detailPrint(){
		return "DNS";
	}

}
