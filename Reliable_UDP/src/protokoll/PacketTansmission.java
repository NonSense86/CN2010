package protokoll;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PacketTansmission extends TimerTask implements
		IPacketTansmissionHook {

	private LinkedList<PacketTansmissionInfo> unackedPackets_ = new LinkedList<PacketTansmissionInfo>();
	private LinkedList<PacketTansmissionInfo> packetHistory_ = new LinkedList<PacketTansmissionInfo>();
	
	private IPacketTransmissionNotifications client_;	
	private int lastSeqId_ = 0;
	private DatagramSocket socket_;

	public PacketTansmission(IPacketTransmissionNotifications client) {
		this.client_ = client;
		new Timer().schedule(this, 1000, 1000);
	}

	public void setSocket(DatagramSocket socket) {
		socket_ = socket;
	}
	
	public synchronized void SendPacket(RUDPPacket rudpPacket, InetAddress address, int port)
			throws IOException {
		/*
		 * if - and only if - ACK is not set we expect an ACK packet from the
		 * other side. if we send an ACK packet we don't expect any reply
		 */
		if (rudpPacket.getIsAck() == false) {
			unackedPackets_.add(new PacketTansmissionInfo(rudpPacket
					.getSeqNumber(), new Date()));
		}

		/* put the RDUP packet into a UDP packet and send it */
		byte[] payload = rudpPacket.encodePackage();

		DatagramPacket packet = new DatagramPacket(payload, payload.length,
				address, port);
		
		socket_.send(packet);

		System.out.println(rudpPacket.toString());
	}

	/**
	 * Every second we check for unacked packets
	 */
	@Override
	public synchronized void run() {
		Iterator<PacketTansmissionInfo> it = unackedPackets_.iterator();
		
		while (it.hasNext()) {

			PacketTansmissionInfo info = it.next();
			
			if(new Date().getTime() - info.getTransmissionDate().getTime() > 2000) {
				client_.OnPacketACKMissing(new PacketTansmissionInfo(info));
			}			
		}
	}

	/**
	 * If a packet is received remove it from the unacked elements
	 */
	@Override
	public synchronized void OnPacketReceived(RUDPPacket rudpPacket) {

		/* is it the seq number we expect? */
		if(rudpPacket.getSeqNumber() != lastSeqId_ + 1) {
			client_.OnPacketWrongOrder();
		}
		
		lastSeqId_ = rudpPacket.getSeqNumber();
		
		/* did we already have a packet with the same seq number? */
		boolean isDuplicate = false;
		
		for(PacketTansmissionInfo info: packetHistory_) {
			if(info.getSeqNumber() == rudpPacket.getSeqNumber()) {
				isDuplicate = true;
			}
		}
		
		/* if we found a duplicate inform client - otherwise add to history */
		if(isDuplicate) {
			client_.OnDuplicatePacket();
		} else {
			packetHistory_.add(new PacketTansmissionInfo(rudpPacket.getSeqNumber(), new Date()));
		}
		
		/* Delete packet from outstanding ack list */
		Iterator<PacketTansmissionInfo> it = unackedPackets_.iterator();

		while (it.hasNext()) {

			PacketTansmissionInfo info = it.next();

			/*
			 * If its an ACK packet check if we can remove it from the unacked
			 * elements
			 */
			if (rudpPacket.getIsAck()) {

				/* Did we get an repsonce? */
				if (info.getSeqNumber() == rudpPacket.getAckNumber()) {
					it.remove();
				}
			}
		}

	}
}
