import java.net.InetAddress;
import java.net.UnknownHostException;

import at.ac.tuwien.cn2010.PeerKernel;
import at.ac.tuwien.cn2010.client.PeerInformation;
import at.ac.tuwien.cn2010.client.SuperPeerListenerService;
import at.ac.tuwien.cn2010.client.SuperPeerListener;



public class main {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		
		InetAddress address = InetAddress.getByName("127.0.0.1");
		
		PeerKernel kernel = new PeerKernel(address, 34545);		

		kernel.StartUpAsSuperPeer().StartBoot();
		
		SuperPeerListenerService service = new SuperPeerListenerService();
		SuperPeerListener foo = service.getSuperPeerListenerPort();
		
		for(PeerInformation info: foo.getAllSuperPeers())
		{			
			
			System.out.println(info.getPeerPort());
			System.out.println(info.getPeerAdress());
		}
	}

}
