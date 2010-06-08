package client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import protokoll.Barrier;
import protokoll.IPacketTansmissionHook;



public class PackageListenerThread extends Thread implements
		IPacketNotification {

	private Barrier barrier_;
	private int lastConnectionId_;	
	private IPacketTansmissionHook hook_;
	private DatagramSocket socket_;

	public PackageListenerThread(Barrier barrier, IPacketTansmissionHook hook, DatagramSocket socket) {
		this.barrier_ = barrier;	
		this.hook_ = hook;
		this.socket_ = socket;
	}

	public void run() {
		try {
			

			System.out.println("Client listeing on port " + socket_.getLocalPort());

			// waiting for reply packets
			MessageBroker broker = new MessageBroker(this, hook_);

			// Signal to the client that we are ready to receive reply packets
			barrier_.releaseAll();

			while (true) {
				DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
				
				socket_.receive(packet);
				broker.ProceedPacket(packet);				
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
	public synchronized void OnNewConnectionReply(int newConnectionId) {
		lastConnectionId_ = newConnectionId;

		barrier_.releaseAll();
	}
}
