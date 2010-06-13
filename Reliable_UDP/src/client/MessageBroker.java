package client;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

import common.Msg;
import common.MsgType;

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
		
		/* inform listener that we received an package */
		// If true, ignore because its duplicate
		if(hook_.onPacketReceived(rudpPacket))
			return;
		
		/**
		 * If ACK = true, SYN = false, NULL = true we got a connection reply
		 * from the server
		 */
		if (type == PacketType.CON_ACCEPT) {
			// TODO: Check authenticity
			notificationClass_.onNewConnectionReply();
			
		} else if (type == PacketType.PAYLOAD)
			processPayloadPacket(rudpPacket);
		
		
			
	}
	
	private synchronized void processPayloadPacket(RUDPPacket packet) throws IOException {
		// Get Msg object
		ByteArrayInputStream bis = new ByteArrayInputStream(packet.getPayload());
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bis));
		Msg msg = null;
		try {
			msg = (Msg)ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ois.close();
		
		if(msg.getMsgType() == MsgType.RENAME) {
			// If name ok
			if(msg.isAvailable())
				notificationClass_.onNameReply(true, msg.getPayload());
			else
				notificationClass_.onNameReply(true, null);
		}
		
		if(msg.getMsgType() == MsgType.UNICAST) {
			for(Msg m : msg.getMessages()) {
				System.out.println(m.getSender() + ": " + m.getPayload());
			}
		}
	}
}
