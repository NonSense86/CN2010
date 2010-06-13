package server;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protokoll.PacketType;
import protokoll.RUDPPacket;
import protokoll.RUDPPacketFactory;
import protokoll.RemoteMachine;

import common.Msg;
import common.MsgFactory;
import common.MsgType;


public class MessageBroker {

	private ConnectionManager cm;
	private ServerInstance serverInstance;
	private List<String> serverList;
	private ExecutorService pool = Executors.newCachedThreadPool();
	

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
		PacketType type = rudpPacket.getPacketType();

		// Connection request
		if (type == PacketType.CON_CREATE) {

			//System.out.println(packet.getPort());
			System.out.println("New connection from: " + packet.getPort());
			processNewConnectionRequest(rudpPacket);
		} else if (type == PacketType.CON_ACCEPT) {
			processNewConnectionReply(rudpPacket);
		// Keepalive packet
		} else if (type == PacketType.KEEP_ALIVE) {
			processKeepalivePacket(rudpPacket);
		// Payload packet
		} else if (type == PacketType.PAYLOAD) {
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
			pool.execute(new MsgProcessor(serverInstance, msg, rudpPacket.getSender()));	
		}
		
		if(msg.getMsgType() == MsgType.CHECKNAME) {
			Msg m = MsgFactory.createCheckNameReplyMsg(msg.getPayload());
			System.out.println("Checking name " +  msg.getPayload());
			if(cm.getClientNames().contains(msg.getPayload()) || cm.getCheckedNames().get(msg.getPayload())) {
				m.setAvailable(false);
			} else {
				m.setAvailable(true);
			}
			System.out.println("name avaialble: " + m.isAvailable());
			RUDPPacket p = RUDPPacketFactory.createPayloadPacket(rudpPacket.getSender(), m);
			serverInstance.getPacketTransmission().sendPacket(p);
		}
		
		if(msg.getMsgType() == MsgType.CHECKNAME_REPLY) {
			if(!msg.isAvailable()) {
				cm.getCheckedNames().put(msg.getPayload(), msg.isAvailable());
			}
		}
	}

	public ConnectionManager getCm() {
		return cm;
	}
	
	
}
