package server;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import protokoll.RemoteMachine;

import common.Msg;


public class ConnectionManager {

	private static Logger logger = Logger.getLogger("ConnectionManager");
	
	private Map<String, RemoteMachine> clients = new Hashtable<String, RemoteMachine>();
	private Map<String, RemoteMachine> servers = new Hashtable<String, RemoteMachine>();
	private Vector<String> clientNames = new Vector<String>();
	private Thread connectionChecker;
	private Map<String, Boolean> checkedNames = new Hashtable<String, Boolean>();
	private Map<String,Vector<Msg>> messageStore = new Hashtable<String, Vector<Msg>>();
	
	public ConnectionManager() {
		connectionChecker = new Thread(new ConnectionChecker(this));
		connectionChecker.start();
	}
	
	public synchronized void saveMsg(Msg msg) {
		// If receiver empty = Multicast
		if(msg.getReceiver() == null) {
			for (RemoteMachine rm : clients.values()) {
				Vector<Msg> clientMsgs = messageStore.get(rm.toString());
				// Don't send to itself
				if(msg.getSender().equals(rm.getName()))
					continue;
				if(clientMsgs == null) {
					clientMsgs = new Vector<Msg>();
					messageStore.put(rm.toString(), clientMsgs);
				}
				clientMsgs.add(msg);
			}
		} else {
			for(RemoteMachine rm : clients.values()) {
				if(rm.getName().equals(msg.getReceiver())) {
					Vector<Msg> clientMsgs = messageStore.get(rm.toString());
					if(clientMsgs == null) {
						clientMsgs = new Vector<Msg>();
						messageStore.put(rm.toString(), clientMsgs);
					}
					clientMsgs.add(msg);
					break;
				}
			}
		}
	}
	
	public List<Msg> readMsgs(String hostPort) {
		Vector<Msg> v = messageStore.get(hostPort);
		List<Msg> msgs = new ArrayList<Msg>();
		if (v == null)
			return msgs;
		synchronized (v) {
			for(Msg m : v) {
				msgs.add(m);
			}
			v.clear();
		}
		return msgs;
	}
	
	public void addClient(RemoteMachine client) {
		String hostport = client.getHost().getHostAddress() + client.getPort();
		if(clients.get(hostport) == null) {
			clients.put(hostport, client);
			logger.info("Client " + hostport + " added");
		} else
			logger.warning("Client " + hostport + " already added");
			
	}
	
	public void removeClient(String hostport) {
		clientNames.remove(clients.get(hostport).getName());
		clients.remove(hostport);
		logger.info("Client " + hostport + " removed");
	}
	
	public void addServer(RemoteMachine server) {
		String hostport = server.getHost().getHostAddress() + server.getPort();
		if(servers.get(hostport) == null) {
			servers.put(hostport, server);
			logger.info("Server " + hostport  +" added");
		} else
			logger.warning("Server " + hostport + " already added");
	}
	
	public void removeServer(String hostport) {
		servers.remove(hostport);
		logger.info("Server" + hostport + " removed");
	}
	
	public void renameClient(String hostport, String name) {
		RemoteMachine rm = clients.get(hostport);
		clientNames.remove(rm.getName());
		rm.setName(name);
		clientNames.add(name);
	}
	
	public void updateClient(String hostport) {
		clients.get(hostport).setLastTime(System.currentTimeMillis());
	}
	
	public void updateServer(String hostport) {
		servers.get(hostport).setLastTime(System.currentTimeMillis());
	}

	// GET/SET
	
	public Map<String, RemoteMachine> getClients() {
		return clients;
	}

	public Map<String, RemoteMachine> getServers() {
		return servers;
	}

	public Vector<String> getClientNames() {
		return clientNames;
	}

	public Map<String, Boolean> getCheckedNames() {
		return checkedNames;
	}

	public Map<String, Vector<Msg>> getMessageStore() {
		return messageStore;
	}
	
	
	
}
