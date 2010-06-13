package client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


import protokoll.IPacketTansmissionHook;



public class PackageListenerThread extends Thread implements
		IPacketNotification {


	private int lastConnectionId_;	
	private IPacketTansmissionHook hook_;
	private DatagramSocket socket_;
	private ClientInstance client;

	public PackageListenerThread(ClientInstance client) {
		this.client = client;
			
		this.hook_ = client.getPacketTransmission();
		this.socket_ = client.getClientSocket();
	}

	public void run() {
		try {
			

			System.out.println("Client listening on port " + socket_.getLocalPort());

			// waiting for reply packets
			MessageBroker broker = new MessageBroker(this, hook_);

			while (true) {
				DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
				
				socket_.receive(packet);
				broker.processPacket(packet);				
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized int getLastConnectionId() {
		return lastConnectionId_;
	}

	@Override
	public synchronized void onNewConnectionReply() {
		client.setConnected(true);
	}
}
