import java.util.*;

public class Internet {

	private Map<String, String> details;
	private byte[] payload;
	private byte[] assembledPayload;
	private boolean isFrag;
	private Internet nextInternet;

	Internet(String protoc3, byte[] datagram) {
		details = new LinkedHashMap<>();
		details.put("ProtoC3", protoc3);
		isFrag = false;
		nextInternet=null;
		this.isFrag = false;
		if (protoc3.equals("0800"))
			getIp(datagram);
		else if (protoc3.equals("0806"))
			getArp(datagram);
	}

	//TODO Handle erreur of packet ARP
	public void getArp(byte[] datagram) {
		details.put("Operation", Utils.byteToHex(Arrays.copyOfRange(datagram, 6, 8)));
		details.put("Mac_source", Utils.byteToMac(Arrays.copyOfRange(datagram, 8, 14)));
		details.put("IP_source", Utils.byteToIP(Arrays.copyOfRange(datagram, 14, 18)));
		details.put("Mac_Dest", Utils.byteToMac(Arrays.copyOfRange(datagram, 18, 24)));
		details.put("IP_dest", Utils.byteToIP(Arrays.copyOfRange(datagram, 24, 28)));
		this.payload = null;
	}

	public void getIp(byte[] datagram) {
		details.put("Version", (datagram[0] >> 4) + "");
		details.put("Length", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 2, 4)) + "");
		details.put("Identification", Utils.byteToHex(Arrays.copyOfRange(datagram, 4, 6)));
		details.put("Dont_fragment", ((datagram[6] & 0x40) >> 6) + "");
		details.put("More_fragment", ((datagram[6] & 0x20) >> 5) + "");
		details.put("Offset", getOffset(Arrays.copyOfRange(datagram, 6, 8)) + "");
		details.put("TTL", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 8, 9)) + "");
		details.put("ProtoC4", Utils.byteToIntBE(Arrays.copyOfRange(datagram, 9, 10)) + "");
		details.put("IP_source", Utils.byteToIP(Arrays.copyOfRange(datagram, 12, 16)));
		details.put("IP_dest", Utils.byteToIP(Arrays.copyOfRange(datagram, 16, 20)));
		this.payload = Arrays.copyOfRange(datagram, 20, datagram.length);
	}

	public long getOffset(byte[] offset){
		offset[0] = (byte)(offset[0] & 0x1F);
		return Utils.byteToIntBE(offset);
	}

	public Map<String, String> getDetails() {
		return this.details;
	}


	public Internet findLastPacketOfChain(){
		Internet i = this;
		while(i.getNextInternet() != null){
			i = i.getNextInternet();
		}
		return i;
	}

	public boolean sameDatagram(Internet internet){
		return 	!this.details.get("Identification").equals("0000") &&
			this.details.get("Identification").equals(internet.getDetails().get("Identification")) &&
				this.details.get("IP_source").equals(internet.getDetails().get("IP_source")) &&
				this.details.get("IP_dest").equals(internet.getDetails().get("IP_dest"));
	}

	public Map<Integer, byte[]> getAllIpFragment(Internet internet){
		Map<Integer, byte[]> fragments = new HashMap<Integer, byte[]>();
		Internet i = this;
		while(i != null){
			fragments.put(Integer.parseInt(i.getDetails().get("Offset")), i.getPayload());
			i = i.getNextInternet();
		}
		return fragments;
	}

	public int getTotalLength(Map<Integer, byte[]> map){
		int max = 0;
		for (Map.Entry<Integer, byte[]> entry : map.entrySet()){
			max = Integer.max(max, entry.getKey() * 8 + entry.getValue().length);
		}
		return max;
	}

	public byte[] mergeIpPayload(){
		Map<Integer, byte[]> fragments = getAllIpFragment(this);
		int lengthTotal = getTotalLength(fragments);
		byte[] assembledPayload = new byte[lengthTotal];
		int i = 0;
		while (fragments.get(i / 8) != null && (i + fragments.get(i/8).length <= lengthTotal)){
			System.arraycopy(fragments.get(i/8), 0, assembledPayload, i, fragments.get(i/8).length);
			i += fragments.get(i/8).length;// / 8;
		}
		if (lengthTotal != i)
			return null;
		return assembledPayload;
	}

	public void setAssembledPayload(){
		byte[] payload = this.mergeIpPayload();
		Internet i = this;
		while(i.getNextInternet() != null){
			i = i.getNextInternet();
		}
		i.assembledPayload = payload;
	}



	public Internet getNextInternet() {
		return nextInternet;
	}

	public void setNextInternet(Internet nextInternet) {
		this.nextInternet = nextInternet;
	}

	public boolean isFrag() {
		return isFrag;
	}

	public void setFrag(boolean frag) {
		isFrag = frag;
	}

	public byte[] getPayload() {
		return payload;
	}

	public byte[] getAssembledPayload() {
		return assembledPayload;
	}
	
	public String getId(){
		return 	this.details.get("Identification") +
			this.details.get("IP_source") +
			this.details.get("IP_dest");
	}
}
