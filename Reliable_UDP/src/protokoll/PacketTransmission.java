package protokoll;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

public class PacketTransmission implements
		IPacketTansmissionHook, IPacketTransmission {

	private static Logger logger = Logger.getLogger("PacketTransmission");
	private Map<String, Conversation> conversations;
	private UnackPackets unackPackets;
	
	private IPacketTransmissionNotifications client;	
	private DatagramSocket socket;
	
	private Thread resender;

	public PacketTransmission(IPacketTransmissionNotifications client, DatagramSocket socket) {
		this.client = client;
		this.socket = socket;
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
		
		socket.send(packet);
		
		//System.out.println(rudpPacket.toString());
	}
	
	public synchronized void resendPacket(RUDPPacket rudpPacket) throws IOException {
		/* put the RDUP packet into a UDP packet and send it */
		byte[] payload = rudpPacket.encodePackage();

		RemoteMachine receiver = rudpPacket.getReceiver();
		DatagramPacket packet = new DatagramPacket(payload, payload.length, receiver.getHost(), receiver.getPort());
		
		socket.send(packet);
	}

	/**
	 * If a packet is received remove it from the unacked elements
	 */
	@Override
	public synchronized void OnPacketReceived(RUDPPacket rudpPacket) {

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
				client.OnPacketWrongOrder();
				c.setAwaitingSeqNumber(seqNumber + 1);
			} else if(seqNumber < c.getAwaitingSeqNumber()) {
				if(c.getSequenceNumbers().contains(seqNumber)) {
					client.OnDuplicatePacket();
					return;
				} else
					client.OnPacketWrongOrder();
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
		
		// Ignore others (keepalives, pollings ...)

	}

	public UnackPackets getUnackPackets() {
		return unackPackets;
	}
	
	
}
