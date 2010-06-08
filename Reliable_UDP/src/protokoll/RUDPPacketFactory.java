package protokoll;

import java.io.UnsupportedEncodingException;

public class RUDPPacketFactory {

	public static RUDPPacket createConnectionRequestPacket() throws UnsupportedEncodingException {
		RUDPPacket packet = new RUDPPacket();

		packet.setIsSyn(true);
		packet.setIsAck(false);
		packet.setIsNull(false);
		packet.setIsChecksum(false);
		packet.setIsConnection(false);

		packet.setSeqNumber(0);
		packet.setConnectionId(0);

		return packet;
	}

	public static RUDPPacket createConnectionReplyPacket(int connectionId,
			int nextSeqNumber) {
		RUDPPacket packet = new RUDPPacket();

		packet.setIsSyn(true);
		packet.setIsAck(true);
		packet.setIsNull(false);
		packet.setIsChecksum(false);
		packet.setIsConnection(true);

		packet.setSeqNumber(nextSeqNumber);
		packet.setConnectionId(connectionId);

		return packet;
	}
	
	public static RUDPPacket createPayloadPacket(int connectionId, byte[] payload) {
		RUDPPacket packet = new RUDPPacket();

		packet.setIsSyn(false);
		packet.setIsAck(false);
		packet.setIsNull(false);
		packet.setIsChecksum(true);
		packet.setIsConnection(true);		
		
		packet.setConnectionId(connectionId);		
		packet.setPayload(payload);

		return packet;
	}

}
