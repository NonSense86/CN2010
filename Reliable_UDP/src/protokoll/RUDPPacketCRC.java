package protokoll;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class RUDPPacketCRC {
	
	public static boolean CheckCRC(RUDPPacket packet) throws IOException  {		
		
		if(packet.getChecksum() == calculateCRCForPacket(packet)) {
			return true;
		} else		
			return false;
	}
	
	public static long calculateCRCForPacket(RUDPPacket packet) throws IOException  {
		ByteArrayOutputStream buffStream = new ByteArrayOutputStream();
		DataOutputStream outStream = new DataOutputStream(buffStream);
		
		outStream.writeUTF(packet.getPacketType().name());
		outStream.writeInt(packet.getSeqNumber());
		outStream.writeInt(packet.getPayloadLength());
		//outStream.write(packet.getPayload());

		outStream.flush();

		CRC32 crc = new CRC32();

		crc.update(buffStream.toByteArray());

		return crc.getValue();
	}
}
