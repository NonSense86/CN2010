package client;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import protokoll.Barrier;
import protokoll.IPacketTransmissionNotifications;
import protokoll.PacketTansmission;
import protokoll.PacketTansmissionInfo;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;

public class ClientInstance implements IPacketTransmissionNotifications {

	private int port_;
	private InetAddress host_;
	private PackageListenerThread packageListenerThread_;
	private Barrier barrier_;
	private PacketTansmission packetTansmission_;
	private DatagramSocket clientSocket_;	

	public ClientInstance() {
		barrier_ = new Barrier();
		packetTansmission_ = new PacketTansmission(this);		
	}

	public ClientInstance(InetAddress host, int port) {
		this();

		this.host_ = host;
		this.port_ = port;
	}

	public int OpenConnection() throws IOException {
		
		clientSocket_ = new DatagramSocket();
		
		packetTansmission_.setSocket(clientSocket_);
		
		/*
		 * First - before we send connection request - make sure that we can
		 * receive the reply from the server. So we need a local Port were we
		 * listen on
		 */

		packageListenerThread_ = new PackageListenerThread(barrier_, packetTansmission_, clientSocket_);
		packageListenerThread_.start();

		/* Block until server port is started */
		barrier_.block();

		/* Now send the connection request to the server */
		RUDPPacket packet = RUDPPacketFactory
				.createConnectionRequestPacket();

		packetTansmission_.SendPacket(packet, host_, port_);

		/* Waiting for the server reply and return the connection id */
		barrier_.block();

		return packageListenerThread_.getLastConnectionId();
	}

	/**
	 * Function sends a packet to the server. The function blocks until
	 * receiving an response from the server.
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	public void Send(int connectionId, byte[] payload) throws IOException {
		RUDPPacket packet = RUDPPacketFactory.createPayloadPacket(connectionId,
			payload);

		packetTansmission_.SendPacket(packet, host_, port_);
	}

	/**
	 * Called from the transmission class if an ACK packet is missing Reason:
	 * connection is down or packet got lost
	 */
	@Override
	public void OnPacketACKMissing(PacketTansmissionInfo info) {
		/* Unblock all who are waiting for the ACK */
		barrier_.releaseAll();

		System.out.println("***** Transmission: ACK missing: SEQ="
				+ info.getSeqNumber() + " SENT ON="
				+ info.getTransmissionDate().toString());
	}

	/**
	 * Called from the transmission class in the packet order is wrong
	 */
	@Override
	public void OnPacketWrongOrder() {
		System.out.println("***** Transmission: Packet wrong order");
	}

	@Override
	public void OnDuplicatePacket() {
		System.out.println("***** Transmission: Packet duplicates");

	}
}
