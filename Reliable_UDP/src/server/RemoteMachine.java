package server;

import java.net.InetAddress;

public class RemoteMachine {

	private InetAddress host;
	private int port;
	
	public RemoteMachine(String hostPort) throws Exception {
		String[] s = hostPort.split(":");
		host = InetAddress.getByName(s[0]);
		port = Integer.parseInt(s[1]);	
	}

	public InetAddress getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}	
}
