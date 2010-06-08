package server;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import protokoll.DatagramPacketTracer;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;


public class MessageBroker {

	private ConnectionManager connectionManager_;
	private ServerInstance serverInstance_;

	public MessageBroker(ServerInstance serverInstance) {
		this.connectionManager_ = new ConnectionManager();
		this.serverInstance_ = serverInstance;
	}

	public synchronized void ProceedPacket(DatagramPacket packet)
			throws IOException {

		RUDPPacket rudpPacket = new RUDPPacket();
		rudpPacket.decodePackage(packet.getData());

		/* DEBUG */
		DatagramPacketTracer.TraceDatagramPacket(packet);
		System.out.println(rudpPacket);
		/* DEBUG END */

		/**
		 * if SYN = true, ACK = false and connectionid = 0 the client wants a
		 * new connection
		 */
		if (rudpPacket.getIsSyn() == true && rudpPacket.getIsAck() == false
				&& rudpPacket.getIsConnection() == false) {

			System.out.println(packet.getPort());
			ProceedNewConnectionRequest(rudpPacket, packet.getAddress(), packet.getPort());
		}

		/**
		 * if NULL = true client wants to signal that he is alive - so
		 * proceeding the payload is not necessary
		 */
		if (rudpPacket.getIsNull() == true && rudpPacket.getIsConnection()) {
			ProceedKeepalivePacket(rudpPacket, packet.getAddress());
		}

		/**
		 * if SYN = false, ACK = false, NULL = false and connection = true, the
		 * client sends a payload message
		 */
		if (rudpPacket.getIsSyn() == false && rudpPacket.getIsAck() == false
				&& rudpPacket.getIsConnection() == true) {
			ProceedPayloadPacket(rudpPacket, packet.getAddress());
		}
	}

	private void ProceedNewConnectionRequest(RUDPPacket rudpPacket,
			InetAddress client, int clientListenPort) throws NumberFormatException, IOException {

		/* register the client connection for further use */
		ConnectionInstance connectionInstance = connectionManager_
				.RegisterNewClientConnection(client, clientListenPort);

		/* reply with connection reply */
		RUDPPacket replyPacket = RUDPPacketFactory.createConnectionReplyPacket(
				connectionInstance.getConnectionId(),
				rudpPacket.getSeqNumber() + 1);

		serverInstance_.getPacketTansmission().SendPacket(replyPacket,
				connectionInstance.getClientAddress(),
				connectionInstance.getClientPort());

	}

	private void ProceedKeepalivePacket(RUDPPacket rudpPacket,
			InetAddress client) {

	}

	private void ProceedPayloadPacket(RUDPPacket rudpPacket, InetAddress client) {

		try {
			System.out.println(new String(rudpPacket.getPayload(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
