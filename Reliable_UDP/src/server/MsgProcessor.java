package server;

import java.io.IOException;
import java.util.Map;

import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;

import common.Msg;
import common.MsgFactory;

public class MsgProcessor implements Runnable{

	private static final long SLEEP = 5000;
	
	private ServerInstance server;
	private Msg msg;
	private RemoteMachine client;
	
	public MsgProcessor(ServerInstance server, Msg msg, RemoteMachine client) {
		this.server = server;
		this.msg = msg;
		this.client = client;
	}
	
	@Override
	public void run() {
		String name = msg.getPayload();
		ConnectionManager cm = server.getBroker().getCm();
		try {
			// Check if checked now
			if(cm.getCheckedNames().get(name) != null) {
				msg.setAvailable(false);
				RUDPPacket p = RUDPPacketFactory.createPayloadPacket(client, msg);
				server.getPacketTransmission().sendPacket(p);
			} else {
				cm.getCheckedNames().put(name, true);
				
				// Check local
				if(cm.getClientNames().contains(name)) {
					msg.setAvailable(false);
					RUDPPacket p = RUDPPacketFactory.createPayloadPacket(client, msg);
					server.getPacketTransmission().sendPacket(p);
					cm.getCheckedNames().remove(name);
				// Check global
				} else {
					Msg m = MsgFactory.createCheckNameMsg(name);
					RUDPPacket p = RUDPPacketFactory.createPayloadPacket(null, m);
					server.getPacketTransmission().multiCastPacket(p, cm.getServers().values());
					
					// Wait for checking on other machines
					try {
						Thread.sleep(SLEEP);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// If name available
					if(cm.getCheckedNames().get(name)) {
						msg.setAvailable(true);	
						cm.renameClient(client.toString(), name);
					// If name not available
					} else {
						msg.setAvailable(false);
					}
					p = RUDPPacketFactory.createPayloadPacket(client, msg);
					server.getPacketTransmission().sendPacket(p);
					cm.getCheckedNames().remove(name);
						
				}				
			}		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
