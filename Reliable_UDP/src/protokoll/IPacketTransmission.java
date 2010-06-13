package protokoll;

import java.io.IOException;
import java.net.InetAddress;

public interface IPacketTransmission {

	public void sendPacket(RUDPPacket rudpPacket) throws IOException;
	
}
