package client;

import java.io.IOException;

import protokoll.PacketTransmission;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;

public class Poller implements Runnable {

	private static final long SLEEP = 1000;
	
	private ClientInstance client;
	private PacketTransmission pt;
	public Poller(ClientInstance client) {
		this.client = client;
		this.pt = client.getPacketTransmission();
	}
	
	@Override
	public void run() {
		RUDPPacket packet = RUDPPacketFactory.createPollPacket(client.getServers().get(0));
		while(true) {
			packet = RUDPPacketFactory.createPollPacket(client.getServers().get(0));
			try {
				pt.sendPacket(packet);
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
