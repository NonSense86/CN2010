package server;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;

import common.Msg;
import common.MsgType;


public class MessageBroker {

	private ConnectionManager cm;
	private ServerInstance serverInstance;
	private List<String> serverList;
	

	public MessageBroker(ServerInstance serverInstance) {
		this.cm = new ConnectionManager();
		this.serverInstance = serverInstance;
		serverList = new ArrayList<String>();
		fillServerList();		
	}
	
	private void fillServerList() {
		for(RemoteMachine rm : serverInstance.getServers()) {
			serverList.add(rm.toString());
		}
	}

	public synchronized void processPacket(DatagramPacket packet)
			throws IOException {

		RUDPPacket rudpPacket = new RUDPPacket(packet);

		/* DEBUG */
		//DatagramPacketTracer.TraceDatagramPacket(packet);
		//System.out.println(rudpPacket);
		/* DEBUG END */

		// Connection request
		if (rudpPacket.getIsSyn() == true && rudpPacket.getIsAck() == false && rudpPacket.getIsConnection() == false) {

			//System.out.println(packet.getPort());
			System.out.println("New connection from: " + packet.getPort());
			processNewConnectionRequest(rudpPacket);
		} else if (rudpPacket.getIsAck() && !rudpPacket.getIsSyn() && rudpPacket.getIsNull()) {
			processNewConnectionReply(rudpPacket);
		// Keepalive packet
		} else if (rudpPacket.getIsNull() && !rudpPacket.getIsAck()) {
			processKeepalivePacket(rudpPacket);
		// Payload packet
		} else if (!rudpPacket.getIsNull() && rudpPacket.getIsConnection() && !rudpPacket.getIsAck()) {
			processPayloadPacket(rudpPacket);
		}
		
		serverInstance.getPacketTransmission().OnPacketReceived(rudpPacket);

		

		
	}

	private void processNewConnectionRequest(RUDPPacket rudpPacket) throws NumberFormatException, IOException {
		String hostport = rudpPacket.getSender().toString();
		if(serverList.contains(hostport))
			cm.addServer(rudpPacket.getSender());
		else
			cm.addClient(rudpPacket.getSender());
	
		/* reply with connection reply */
		RUDPPacket replyPacket = RUDPPacketFactory.createConnectionReplyPacket(rudpPacket.getSender());
		
		serverInstance.getPacketTransmission().sendPacket(replyPacket);
	}
	
	private void processNewConnectionReply(RUDPPacket rudpPacket) {
		String hostport = rudpPacket.getSender().toString();
		if(serverList.contains(hostport))
			cm.addServer(rudpPacket.getSender());
	}

	private void processKeepalivePacket(RUDPPacket rudpPacket) {
		String hostport = rudpPacket.getSender().toString();
		if(serverList.contains(hostport))
			cm.updateServer(hostport);
		else
			cm.updateClient(hostport);
	}

	private void processPayloadPacket(RUDPPacket rudpPacket) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(rudpPacket.getPayload());
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bis));
		Msg msg = null;
		try {
			msg = (Msg)ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ois.close();
		
		if(msg.getMsgType() == MsgType.RENAME) {
			if(cm.getClientNames().contains(msg.getPayload())) {
				System.out.println(msg.getPayload());
			} else {
				
			}
				
		}
	}

	public ConnectionManager getCm() {
		return cm;
	}
	
	
}
