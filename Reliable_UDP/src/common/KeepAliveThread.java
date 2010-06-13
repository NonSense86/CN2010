package common;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;


import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;

public class KeepAliveThread implements Runnable {

	private static Logger logger = Logger.getLogger("KeepAliveThread");
	private static final long SLEEP = 3000;
	private IKeepAlive client;
	
	public KeepAliveThread(IKeepAlive client) {
		this.client = client;

	}
	
	@Override
	public void run() {
		while(true) {
			RUDPPacket packet;
			List<RemoteMachine> servers = client.getActiveConnections();
			for(RemoteMachine rm : servers) {
				packet = RUDPPacketFactory.createKeepAlivePacket(rm);
				
				try {
					client.getPacketTransmission().sendPacket(packet);
					//logger.info("Sending keepalive");
				} catch (IOException e) {
					System.out.println("Problem while sending the package.");
				}
			}
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				// Should not happen
				e.printStackTrace();
			}
		}
		
	}

}
