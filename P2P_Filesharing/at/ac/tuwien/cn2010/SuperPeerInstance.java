package at.ac.tuwien.cn2010;

public class SuperPeerInstance implements IPeer {
	
	/**
	 * Starts the SuperPeer. After starting the peer, he is listening
	 * and waits for Super Peer Requests and Peer Requests. Also a 
	 * scheduler is started to discover the environment
	 */
	@Override
	public void Run() {
		System.out.println("[SuperPeerInstance] Run");
		
	}

	@Override
	public void Stop() {
		System.out.println("[SuperPeerInstance] Stop");
		
	}

}
