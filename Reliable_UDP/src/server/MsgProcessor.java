package server;

import java.io.IOException;
import java.net.DatagramPacket;

public class MsgProcessor implements Runnable{

	private MessageBroker broker;
	private DatagramPacket packet;
	
	public MsgProcessor(MessageBroker broker, DatagramPacket packet) {
		this.broker = broker;
		this.packet = packet;
	}
	@Override
	public void run() {
		try {
			broker.processPacket(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
