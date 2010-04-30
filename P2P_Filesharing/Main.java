import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import at.ac.tuwien.cn2010.PeerKernel;
import at.ac.tuwien.cn2010.SuperPeerInformation;
import at.ac.tuwien.cn2010.client.SuperPeerListenerService;
import at.ac.tuwien.cn2010.client.SuperPeerListener;

public class Main {

	/**
	 * @param args
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException {

		ConfigReader reader = new ConfigReader(args[0]);

		ConfigReaderResult result = reader.ReadConfig();

		/**
		 * This section creates a kernel
		 */
		System.out.println(String.format("** My starup params host=%s port=%d",
				result.getMyAddress(), result.getMyPort()));

		PeerKernel kernel = new PeerKernel(result.getMyAddress(), result
				.getMyPort());

		/**
		 * This section is SuperPeer specific Apply config results to kernel -
		 * at the begin we only know SuperPeers We dont have to care about
		 * normal Peers
		 */
		for (SuperPeerInformation info : result.getPeersListReference()) {
			System.out.println(String.format("** Applying \n\tpeerurl=%s", info
					.getPeerURL()));

			kernel.getSuperPeerNeighborStorageReference().addSuperPeerNeighbor(
					info);
		}

		/**
		 * Startup ...
		 */
		kernel.StartUpAsSuperPeer().StartBoot();
	}
}
