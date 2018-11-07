import java.io.*;
import java.util.*;

public class Dns implements AppProtocol{
	private Map<String, String> header;
	private ArrayList<String[]> queries;
	private ArrayList<String[]> answers;
	private ArrayList<String[]> authAnswers;
	private ArrayList<String[]> additionnals;
	private Map<Integer, String> compression;
	private final Map<String, String> DNS_TYPES = Collections.unmodifiableMap(new TreeMap<String, String>() {{
		put("1", "A");
		 put("2", "NS");
	 put("3", "MD");
	 put("4", "MF");
	 put("5", "CNAME");
	 put("6", "SOA");
	 put("7", "MB");
	 put("8", "MG");
	 put("9", "MR");
	 put("10", "NULL");
	 put("11", "WKS");
	 put("12", "PTR");
	 put("13", "HINFO");
	 put("14", "MINFO");
	 put("15", "MX");
	 put("16", "TXT");
	 put("17", "RP");
	 put("18", "AFSDB");
	 put("19", "X25");
	 put("20", "ISDN");
	 put("21", "RT");
	 put("22", "NSAP");
	 put("23", "NSAP-PTR");
	 put("24", "SIG");
	 put("25", "KEY");
	 put("26", "PX");
	 put("27", "GPOS");
	 put("28", "AAAA");
	 put("29", "LOC");
	 put("30", "NXT");
	 put("31", "EID");
	 put("32", "NIMLOC");
	 put("33", "SRV");
	 put("34", "ATMA");
	 put("35", "NAPTR");
	 put("36", "KX");
	 put("37", "CERT");
	 put("38", "A6");
	 put("39", "DNAME");
	 put("40", "SINK");
	 put("41", "OPT");
	 put("42", "APL");
	 put("43", "DS");
	 put("44", "SSHFP");
	 put("45", "IPSECKEY");
	 put("46", "RRSIG");
	 put("47", "NSEC");
	 put("48", "DNSKEY");
	 put("49", "DHCID");
	 put("50", "NSEC3");
	 put("51", "NSEC3PARAM");
	 put("52", "TLSA");
	 put("53", "SMIMEA");
	 put("54", "Unassigned");
	 put("55", "HIP");
	 put("56", "NINFO");
	 put("57", "RKEY");
	 put("58", "TALINK");
	 put("59", "CDS");
	 put("60", "CDNSKEY");
	 put("61", "OPENPGPKEY");
	 put("62", "CSYNC");
	 put("99", "SPF");
	 put("100", "UINFO");
	 put("101", "UID");
	 put("102", "GID");
	 put("103", "UNSPEC");
	 put("104", "NID");
	 put("105", "L32");
	 put("106", "L64");
	 put("107", "LP");
	 put("108", "EUI48");
	 put("109", "EUI64");
	 put("249", "TKEY");
	 put("250", "TSIG");
	 put("251", "IXFR");
	 put("252", "AXFR");
	 put("253", "MAILB");
	 put("254", "MAILA");
	 put("255", "*");
	 put("256", "URI");
	 put("257", "CAA");
	 put("258", "AVC");
	 put("259", "DOA");
	 put("32768", "TA");
	 put("32769", "DLV");
    	}});

	Dns(byte[] datagram) throws Exception{
		compression = new LinkedHashMap<>();
		queries = new ArrayList<String[]>();
		answers = new ArrayList<String[]>();
		authAnswers = new ArrayList<String[]>();
		additionnals = new ArrayList<String[]>();
		parseHeader(datagram);
		//System.out.println("Neww Packet :");
		//System.out.println("Parse query :");
		int offset = parseInfo(datagram, 12, Integer.parseInt(this.header.get("Nb_q")), false, this.queries);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
		//System.out.println("Parse answer :");
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_a")), true, this.answers);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
		//System.out.println("Parse auth ans :");
		offset = parseInfo(datagram, offset, Integer.parseInt(this.header.get("Nb_aa")), true, this.authAnswers);
		/*for(Map.Entry<Integer, String> field : compression.entrySet()) {
			System.out.println(field.getKey() + " = " + field.getValue());
		}*/
		//System.out.println("Parse additionnal :");
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
			String type = Utils.byteToIntBE(Arrays.copyOfRange(datagram, offset, offset + 2)) + "";
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
				String ipServ = "";
				if (DNS_TYPES.get(type).equals("A"))
					ipServ = Utils.byteToIP(Arrays.copyOfRange(datagram, offset, offset + Integer.parseInt(data)));
				else if (DNS_TYPES.get(type).equals("CNAME")){
					this.compression.put(offset, Utils.byteToHex(Arrays.copyOfRange(datagram, offset, offset + Integer.parseInt(data))));
					ipServ = Utils.byteToHex(Arrays.copyOfRange(datagram, offset, offset + Integer.parseInt(data)));
				//	ipServ = parseName(Arrays.copyOfRange(datagram, offset, offset + Integer.parseInt(data)), 0);
				}
				else
					ipServ = Utils.byteToHex(Arrays.copyOfRange(datagram, offset, offset + Integer.parseInt(data)));
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
	//	System.out.println("-------------------------");
	//	for(Map.Entry<Integer, String> field : compression.entrySet()) 
	//		System.out.println(field.getKey() + " = " + field.getValue());
		//System.out.println((info & 0x3FFF));
		//System.out.println(compression.get(info & 0x3FFF));
		//System.out.println(compression.get(12));
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

	public String printQuery(String[] query){
		String res = "Name: " + query[0] + "\n";
		res += "Type: " + DNS_TYPES.get(query[1]) + "\n";
		res += "Class: " + query[2] + "\n";
		return res;
	}

	public String printAnswer(String[] ans){
		String res = "Name: " + ans[0] + "\n";
		res += "Type: " + DNS_TYPES.get(ans[1]) + "\n";
		res += "Class: " + ans[2] + "\n";
		res += "Time To live: " + ans[3] + "\n";
		res += "Data length: " + ans[4] + "\n";
		res += DNS_TYPES.get(ans[1]) + ": " + ans[5] + "\n";
		return res;
		
	}

	public ArrayList<String> getRequests(){
		return null;
	}
	public String getProtocol(){
		return "DNS";
	}
	public String tinyPrint(){
		String res = "";
		//Response
		res += "Response " + this.header.get("TransacID") + " ";
		if ((Integer.parseInt(this.header.get("Flags")) >> 15) == 1){
			for (String[] a : this.answers)
				res += DNS_TYPES.get(a[1]) + " " + a[0] + " " + a[5] + " ";
		}
		else{
			for (String[] q : this.queries)
				res += DNS_TYPES.get(q[1]) + " " + q[0] + " ";
		}
		return res;
	}
	public String detailPrint(){
		String res = "";
		res += Printer.printLayer(this.header);
		for (String [] q : this.queries){
			res += "Query :\n";
			res += Utils.addTab(printQuery(q), 1);
		}
		for (String [] a : this.answers){
			res += "Answer :\n";
			res += Utils.addTab(printAnswer(a), 1);
		}
		for (String [] aa : this.authAnswers){
			res += "Answer :\n";
			res += Utils.addTab(printAnswer(aa), 1);
		}
		for (String [] ad : this.additionnals){
			res += "Answer :\n";
			res += Utils.addTab(printAnswer(ad), 1);
		}
		return res;
	}

}
