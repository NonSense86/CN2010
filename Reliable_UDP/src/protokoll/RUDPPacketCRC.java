package protokoll;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class RUDPPacketCRC {
	
	public static boolean CheckCRC(RUDPPacket packet) throws IOException  {		
		
		if(packet.getIsChecksum()) {
			if(packet.getChecksum() == getCRCOfHeaderAndPayload(packet)) {
				return true;
			}
		} else {
			if(packet.getChecksum() == getCRCOfHeader(packet)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static long GalculateCRCForPacket(RUDPPacket packet) throws IOException  {
		if(packet.getIsChecksum()) {
			return getCRCOfHeaderAndPayload(packet);
		} else {
			return getCRCOfHeader(packet);
		}
	}
	
	public static long getCRCOfHeader(RUDPPacket packet) throws IOException {
		ByteArrayOutputStream buffStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(buffStream);

		outStream.writeBoolean(packet.getIsSyn());
		outStream.writeBoolean(packet.getIsAck());
		outStream.writeBoolean(packet.getIsNull());
		outStream.writeBoolean(packet.getIsChecksum());
		outStream.writeBoolean(packet.getIsConnection());

		outStream.writeInt(packet.getSeqNumber());
		outStream.writeInt(packet.getAckNumber());
		outStream.writeInt(packet.getConnectionId());

		outStream.flush();

		CRC32 crc = new CRC32();

		crc.update(buffStream.toByteArray());

		return crc.getValue();
	}

	public static long getCRCOfHeaderAndPayload(RUDPPacket packet) throws IOException {
		ByteArrayOutputStream buffStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(buffStream);

		outStream.writeBoolean(packet.getIsSyn());
		outStream.writeBoolean(packet.getIsAck());
		outStream.writeBoolean(packet.getIsNull());
		outStream.writeBoolean(packet.getIsChecksum());
		outStream.writeBoolean(packet.getIsConnection());
		
		outStream.writeInt(packet.getSeqNumber());
		outStream.writeInt(packet.getAckNumber());
		outStream.writeInt(packet.getConnectionId());		

		outStream.writeInt(packet.getPayloadLength());
		//outStream.write(packet.getPayload());

		outStream.flush();

		CRC32 crc = new CRC32();

		crc.update(buffStream.toByteArray());

		return crc.getValue();
	}
}
