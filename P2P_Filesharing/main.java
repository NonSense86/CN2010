import at.ac.tuwien.cn2010.PeerKernel;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PeerKernel kernel = new PeerKernel();		

		kernel.StartUpAsSuperPeer().StartBoot();
	}

}
