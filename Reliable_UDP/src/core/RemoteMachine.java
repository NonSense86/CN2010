package core;

public class RemoteMachine {

	private String host;
	private int port;
	
	public RemoteMachine(String hostPort) throws Exception {
		String[] s = hostPort.split(":");
		host = s[0];
		port = Integer.parseInt(s[1]);	
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}	
	
	
	
}
