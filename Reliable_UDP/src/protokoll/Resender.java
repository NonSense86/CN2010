package protokoll;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Logger;

public class Resender implements Runnable {

	private static Logger logger = Logger.getLogger("Resender");
	private static final long SLEEP = 1000;
	
	private UnackPackets packets;
	private PacketTransmission pt;
	
	public Resender(PacketTransmission pt) {
		this.pt = pt;
		this.packets = pt.getUnackPackets();
	}
	
	@Override
	public void run() {
		while(true) {
			Set<String> keys = packets.keySet();
			for(String k : keys) {
				Enumeration<RUDPPacket> e = packets.clientPackets(k);
				while(e.hasMoreElements()) {
					RUDPPacket p = e.nextElement();
					packets.updatePacket(p);
					try {
						pt.resendPacket(p);
						logger.info("Resending packet");
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
