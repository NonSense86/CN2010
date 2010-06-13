package protokoll;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class UnackPackets extends Hashtable<String, Vector<RUDPPacket>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6940035864077307116L;

	public void updatePacket(RUDPPacket packet) {
		Vector<RUDPPacket> v = this.get(packet.getReceiver().toString());
		int i = packet.getResendCount();
		synchronized (v) {
			v.remove(packet);
			if(i < 5) {
				packet.setResendCount(i + 1);
				v.add(packet);
			} else {
				System.out.println("Packet:" + packet.getSeqNumber() + " for " + packet.getReceiver().toString() +
						" not sent after 5 retries.");
			}
		}
		
	}
	
	public Enumeration<RUDPPacket> clientPackets(String client) {
		return this.get(client).elements();
	}
}
