package at.ac.tuwien.cn2010;

import java.net.UnknownHostException;

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
		 *  This section is SuperPeer specific
		 *  Apply config results to kernel - at the begin we only know SuperPeers
		 *  We dont have to care about normal Peers
		 */
		for (SuperPeerInformation info : result.getPeersListReference()) {
			System.out.println(String.format("** Applying \n\tpeerurl=%s\n\tservicename=%s\n\tnamespace=%s",
				info.getPeerURL(), info.getServiceName().getLocalPart(), info.getServiceName().getNamespaceURI()));
			
			kernel.getSuperPeerNeighborStorageReference().addSuperPeerNeighbor(info);
		}

		/**
		 *  Startup ...
		 */
		kernel.StartUpAsSuperPeer().StartBoot();
	}
}
