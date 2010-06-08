package client;
import java.io.IOException;
import java.net.DatagramPacket;

import protokoll.DatagramPacketTracer;
import protokoll.IPacketTansmissionHook;
import protokoll.RUDPPacket;



public class MessageBroker {

	private IPacketNotification notificationClass_;
	private IPacketTansmissionHook hook_;

	public MessageBroker(IPacketNotification notificationClass, IPacketTansmissionHook hook) {
		this.notificationClass_ = notificationClass;
		this.hook_ = hook;
	}

	public synchronized void ProceedPacket(DatagramPacket packet)
			throws IOException {

		RUDPPacket rudpPacket = new RUDPPacket();
		rudpPacket.decodePackage(packet.getData());

		/* inform listener that we received an package */
		hook_.OnPacketReceived(rudpPacket);
		
		/* DEBUG */
		DatagramPacketTracer.TraceDatagramPacket(packet);
		System.out.println(rudpPacket);
		/* DEBUG END */

		/**
		 * If ACK = true, SYN = true, NULL = false we got a connection reply
		 * from the server
		 */
		if (rudpPacket.getIsAck() == true && rudpPacket.getIsSyn() == true
				&& rudpPacket.getIsNull() == false) {
			
			notificationClass_.OnNewConnectionReply(rudpPacket.getConnectionId());
		}
	}
}
