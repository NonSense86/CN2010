package server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import protokoll.IPacketTransmissionNotifications;
import protokoll.PacketTansmission;
import protokoll.PacketTansmissionInfo;


public class ServerInstance implements IPacketTransmissionNotifications {

	private int port_;
	private PacketTansmission packetTansmission_;
	private DatagramSocket socket_;

	public ServerInstance(int port) {
		this.port_ = port;
		packetTansmission_ = new PacketTansmission(this);
	}

	public void StartListening() throws SocketException, IOException {
		System.out.println("Server is now listing on port " + port_);

		socket_ = new DatagramSocket(port_);	
		
		packetTansmission_.setSocket(socket_);
		
		MessageBroker broker = new MessageBroker(this);

		while (true) {
			DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
			socket_.receive(packet);			

			broker.ProceedPacket(packet);
		}
	}
	
	public PacketTansmission getPacketTansmission() {
		return this.packetTansmission_;
	}

	@Override
	public void OnDuplicatePacket() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnPacketACKMissing(PacketTansmissionInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnPacketWrongOrder() {
		// TODO Auto-generated method stub
		
	}	
}
