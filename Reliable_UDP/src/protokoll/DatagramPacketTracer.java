package protokoll;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatagramPacketTracer {

	/**
	 * Returns the current time as string
	 * 
	 * @return the current time
	 */
	private static String getTimeString() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss S");
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Prints informations from a UDP Packet
	 * 
	 * @param packet
	 *            the packet which should be traced
	 */
	public static void TraceDatagramPacket(DatagramPacket packet) {
		InetAddress address = packet.getAddress();

		int port = packet.getPort();
		int len = packet.getLength();		

		System.out.printf("[%s] UDP Packet: IP=%s:%d length=%d\n",
				getTimeString(), address, port, len);
	}
}
