package client;
import java.io.IOException;
import java.net.DatagramPacket;

import protokoll.DatagramPacketTracer;
import protokoll.IPacketTansmissionHook;
import protokoll.PacketType;
import protokoll.RUDPPacket;



public class MessageBroker {

	private IPacketNotification notificationClass_;
	private IPacketTansmissionHook hook_;

	public MessageBroker(IPacketNotification notificationClass, IPacketTansmissionHook hook) {
		this.notificationClass_ = notificationClass;
		this.hook_ = hook;
	}

	public synchronized void processPacket(DatagramPacket packet)
			throws IOException {

		RUDPPacket rudpPacket = new RUDPPacket(packet);

		PacketType type = rudpPacket.getPacketType();
		/**
		 * If ACK = true, SYN = false, NULL = true we got a connection reply
		 * from the server
		 */
		if (type == PacketType.CON_ACCEPT) {
			// TODO: Check authenticity
			notificationClass_.onNewConnectionReply();
			
		} else if (type == PacketType.PAYLOAD)
			processPayloadPacket(packet);
		
		/* inform listener that we received an package */
		hook_.OnPacketReceived(rudpPacket);
			
	}
	
	private synchronized void processPayloadPacket(DatagramPacket packet) {
		
	}
}
