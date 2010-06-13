package server;

public class ConnectionChecker implements Runnable {

	private static final long SLEEP = 5000;
	
	private ConnectionManager cm;
	
	public ConnectionChecker(ConnectionManager cm) {
		this.cm = cm;
	}
	
	@Override
	public void run() {
		while(true) {
			for(String k : cm.getClients().keySet()) {
				if(System.currentTimeMillis() - cm.getClients().get(k).getLastTime() > 10000) {
					// Remove name of the client
					cm.removeClient(k);
				}
			}
			
			for(String k : cm.getServers().keySet()) {
				if(System.currentTimeMillis() - cm.getServers().get(k).getLastTime() > 10000)
					cm.removeServer(k);
			}
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
