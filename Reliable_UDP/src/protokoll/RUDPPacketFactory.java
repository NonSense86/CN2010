package protokoll;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

public class RUDPPacketFactory {

	public static RUDPPacket createConnectionRequestPacket(RemoteMachine receiver) throws UnsupportedEncodingException {
		RUDPPacket packet = new RUDPPacket(receiver);
		packet.setPacketType(PacketType.CON_CREATE);
		return packet;
	}

	public static RUDPPacket createConnectionReplyPacket(RemoteMachine receiver) {
		RUDPPacket packet = new RUDPPacket(receiver);
		packet.setPacketType(PacketType.CON_ACCEPT);		
		return packet;
	}
	
	public static RUDPPacket createPollPacket(RemoteMachine server) {
		RUDPPacket packet = new RUDPPacket(server);
		packet.setPacketType(PacketType.POLL);				
		return packet;
	}
	
	public static RUDPPacket createPayloadPacket(RemoteMachine server, Object o) throws IOException {
		RUDPPacket packet = new RUDPPacket(server);
		packet.setPacketType(PacketType.PAYLOAD);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		oos.flush();
		oos.close();
		bos.close();
		
		packet.setPayload(bos.toByteArray());

		return packet;
	}
	
	public static RUDPPacket createAckPacket(RUDPPacket rudpPacket, int seqNumber) {
		RUDPPacket packet = new RUDPPacket();
		packet.setPacketType(PacketType.PAYLOAD_ACK);	
		packet.setSeqNumber(seqNumber);
		packet.setReceiver(rudpPacket.getSender());
		return packet;
	}
	
	public static RUDPPacket createKeepAlivePacket(RemoteMachine server) {
		RUDPPacket packet = new RUDPPacket(server);		
		packet.setPacketType(PacketType.KEEP_ALIVE);
		return packet;
	}

}
