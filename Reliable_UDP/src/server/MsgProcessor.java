package server;

import java.io.IOException;

import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;
import common.Msg;
import common.MsgFactory;

public class MsgProcessor implements Runnable{

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
		// Check if checked now
		if(server.getBroker().getCm().getCheckedNames().get(name) != null) {
			msg.setAvailable(false);
			try {
				RUDPPacket p = RUDPPacketFactory.createPayloadPacket(client, msg);
				server.getPacketTransmission().sendPacket(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
