package protokoll;

import java.net.InetAddress;

public class RemoteMachine {

	private InetAddress host;
	private int port;
	private String name;
	private long lastTime;
	
	public RemoteMachine(String hostPort) throws Exception {
		String[] s = hostPort.split(":");
		host = InetAddress.getByName(s[0]);
		port = Integer.parseInt(s[1]);	
	}
	
	public RemoteMachine(InetAddress host, int port) {
		this.host = host;
		this.port = port;
		this.lastTime = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return host.getHostAddress() + port;
	}

	public InetAddress getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}	
	
	
}
