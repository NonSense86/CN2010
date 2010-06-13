package protokoll;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

public class PacketTransmission implements
		IPacketTansmissionHook, IPacketTransmission {

	private static float LOST_PROBABILITY = 1.0f;
	private static Logger logger = Logger.getLogger("PacketTransmission");
	private Map<String, Conversation> conversations;
	private UnackPackets unackPackets;
	
	private IPacketTransmissionNotifications client;	
	private DatagramSocket socket;
	
	private Thread resender;
	
	boolean send = true;

	public PacketTransmission(IPacketTransmissionNotifications client, DatagramSocket socket, float probability) {
		this.client = client;
		this.socket = socket;
		LOST_PROBABILITY = probability;
		conversations = new Hashtable<String, Conversation>();
		unackPackets = new UnackPackets();
		resender = new Thread(new Resender(this));
		resender.start();
	}
	
	public synchronized void multiCastPacket(RUDPPacket packet, Collection<RemoteMachine> receivers) throws IOException {
		for(RemoteMachine rm : receivers) {
			packet.setReceiver(rm);
			sendPacket(packet);
		}
	}
	
	public synchronized void sendPacket(RUDPPacket rudpPacket) throws IOException {
		PacketType type = rudpPacket.getPacketType();
		
		// If payload packet
		if(type == PacketType.PAYLOAD) {
			Conversation c = conversations.get(rudpPacket.getReceiver().toString());
			if(c == null) {
				c = new Conversation();
				conversations.put(rudpPacket.getReceiver().toString(), c);
			}
			rudpPacket.setSeqNumber(c.getNextSeqNumber());
		}
		
		// Save unack packet
		if(type == PacketType.PAYLOAD || type == PacketType.CON_CREATE) {
			Vector<RUDPPacket> v = unackPackets.get(rudpPacket.getReceiver().toString());
			if(v == null) {
				v = new Vector<RUDPPacket>();
				unackPackets.put(rudpPacket.getReceiver().toString(), v);
			}
			v.add(rudpPacket);
		}

		/* put the RDUP packet into a UDP packet and send it */
		byte[] payload = rudpPacket.encodePackage();

		RemoteMachine receiver = rudpPacket.getReceiver();
		DatagramPacket packet = new DatagramPacket(payload, payload.length, receiver.getHost(), receiver.getPort());
		
		send = new Random().nextFloat() < LOST_PROBABILITY;
		if(send)
			socket.send(packet);
		else
			System.out.println("Sending of packet " + type + " for " + rudpPacket.getReceiver() + " failed.");
	}
	
	public synchronized void resendPacket(RUDPPacket rudpPacket) throws IOException {
		/* put the RDUP packet into a UDP packet and send it */
		byte[] payload = rudpPacket.encodePackage();

		RemoteMachine receiver = rudpPacket.getReceiver();
		DatagramPacket packet = new DatagramPacket(payload, payload.length, receiver.getHost(), receiver.getPort());
		
		send = new Random().nextFloat() < LOST_PROBABILITY;
		if(send)
			socket.send(packet);
		else
			System.out.println("Resending of packet " + rudpPacket.getPacketType() + " for " + rudpPacket.getReceiver() + " failed.");
	}

	/**
	 * If a packet is received remove it from the unacked elements
	 */
	@Override
	public synchronized boolean onPacketReceived(RUDPPacket rudpPacket) {
		boolean ignore = false;
		
		PacketType type = rudpPacket.getPacketType();
		// If ack packet
		if(type == PacketType.CON_ACCEPT || type == PacketType.PAYLOAD_ACK) {
			Vector<RUDPPacket> v = unackPackets.get(rudpPacket.getSender().toString());
			for(RUDPPacket p : v) {
				if(p.getSeqNumber() == rudpPacket.getSeqNumber()) {
					v.remove(p);
					break;
				}
			}
		// If Synchronized packet
		} else if(type == PacketType.PAYLOAD) {				
			Conversation c = conversations.get(rudpPacket.getSender().toString());
			if (c == null) {
				c = new Conversation();
			}
			int seqNumber = rudpPacket.getSeqNumber();
			if (seqNumber > c.getAwaitingSeqNumber()) {
				client.onPacketWrongOrder();
				c.setAwaitingSeqNumber(seqNumber + 1);
			} else if(seqNumber < c.getAwaitingSeqNumber()) {
				if(c.getSequenceNumbers().contains(seqNumber)) {
					client.onDuplicatePacket();
					ignore = true;
				} else
					client.onPacketWrongOrder();
			} else {
				c.setAwaitingSeqNumber(seqNumber + 1);
			}
			c.getSequenceNumbers().add(seqNumber);
			
			// Send ack
			RUDPPacket packet = RUDPPacketFactory.createAckPacket(rudpPacket, seqNumber);
			try {
				sendPacket(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return ignore;
		
		// Ignore others (keepalives, pollings ...)

	}

	public UnackPackets getUnackPackets() {
		return unackPackets;
	}
	
	
}
