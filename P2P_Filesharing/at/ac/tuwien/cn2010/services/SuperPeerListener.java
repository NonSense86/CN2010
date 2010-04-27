package at.ac.tuwien.cn2010.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.jws.WebService;

@WebService(targetNamespace="http://client.cn2010.tuwien.ac.at")
public class SuperPeerListener implements ISuperPeerListener {

	@Override
	public PeerInformation[] getAllSuperPeers() {
		
		PeerInformation[] array = new PeerInformation[10];
		
		ArrayList<PeerInformation> foo = new ArrayList<PeerInformation>();
		
		PeerInformation i1 = new PeerInformation();
		i1.PeerPort = 123;
		try {
			i1.PeerAdress = InetAddress.getByName("localhost").getHostAddress().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		foo.add(i1);
		
		return (PeerInformation[]) foo.toArray(new PeerInformation[foo.size()]);
	}

}
