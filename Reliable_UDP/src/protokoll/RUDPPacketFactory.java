package protokoll;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

public class RUDPPacketFactory {

	public static RUDPPacket createConnectionRequestPacket(RemoteMachine receiver) throws UnsupportedEncodingException {
		RUDPPacket packet = new RUDPPacket(receiver);
		
		packet.setPacketType(PacketType.CON_CREATE);

		packet.setIsSyn(true);
		packet.setIsAck(false);
		packet.setIsNull(true);
		packet.setIsChecksum(false);
		packet.setIsConnection(false);

		packet.setSeqNumber(0);
		packet.setConnectionId(0);

		return packet;
	}

	public static RUDPPacket createConnectionReplyPacket(RemoteMachine receiver) {
		RUDPPacket packet = new RUDPPacket(receiver);

		packet.setPacketType(PacketType.CON_ACCEPT);
		
		packet.setIsSyn(false);
		packet.setIsAck(true);
		packet.setIsNull(true);
		packet.setIsChecksum(false);
		packet.setIsConnection(true);

		packet.setSeqNumber(0);
		
		return packet;
	}
	
	public static RUDPPacket createPollPacket(RemoteMachine server) {
		RUDPPacket packet = new RUDPPacket(server);
				
		packet.setIsSyn(false);
		packet.setIsAck(false);
		packet.setIsNull(false);
		packet.setIsChecksum(false);
		packet.setIsConnection(true);
		
		return packet;
	}
	
	public static RUDPPacket createPayloadPacket(RemoteMachine server, Object o) throws IOException {
		RUDPPacket packet = new RUDPPacket(server);

		packet.setPacketType(PacketType.PAYLOAD);
		
		packet.setIsSyn(true);
		packet.setIsAck(false);
		packet.setIsNull(false);
		packet.setIsChecksum(true);
		packet.setIsConnection(true);		
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		oos.flush();
		oos.close();
		bos.close();
		
		packet.setPayload(bos.toByteArray());

		return packet;
	}
	
	public static RUDPPacket createAckPacket(int seqNumber) {
		RUDPPacket packet = new RUDPPacket();
		
		packet.setPacketType(PacketType.PAYLOAD_ACK);
		
		packet.setIsSyn(false);
		packet.setIsAck(true);
		packet.setIsNull(true);
		packet.setIsChecksum(false);
		packet.setIsConnection(true);
		
		packet.setSeqNumber(seqNumber);
		
		return packet;
	}
	
	public static RUDPPacket createKeepAlivePacket(RemoteMachine server) {
		RUDPPacket packet = new RUDPPacket(server);
		
		packet.setPacketType(PacketType.KEEP_ALIVE);
		
		packet.setIsSyn(false);
		packet.setIsAck(false);
		packet.setIsNull(true);
		packet.setIsChecksum(false);
		packet.setIsConnection(true);
		
		return packet;
	}

}
