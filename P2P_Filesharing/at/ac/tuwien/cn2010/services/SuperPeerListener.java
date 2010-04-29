package at.ac.tuwien.cn2010.services;

import java.util.ArrayList;

import javax.jws.WebService;

import at.ac.tuwien.cn2010.SuperPeerInformation;
import at.ac.tuwien.cn2010.SuperPeerNeighborStorage;

@WebService(targetNamespace="http://client.cn2010.tuwien.ac.at")
public class SuperPeerListener implements ISuperPeerListener {

	private SuperPeerNeighborStorage storage_;
	
	public SuperPeerListener(SuperPeerNeighborStorage storage) {
		this.storage_ = storage;
	}
	
	/**
	 * Returns all known Super Peers to the requestor
	 * This includes not the served super peer
	 */
	@Override
	public synchronized SuperPeerInformationTransferObject[] getAllSuperPeers() {
		
		ArrayList<SuperPeerInformationTransferObject> array = new ArrayList<SuperPeerInformationTransferObject>();
		
		storage_.acquireLock();
		
		try
		{		
			for(SuperPeerInformation peer: storage_.getNeighborPeersListReference()) {
				SuperPeerInformationTransferObject info = new SuperPeerInformationTransferObject();
				
				info.PeerUrl = peer.getPeerURL().toString();
				info.Namespace = peer.getServiceName().getNamespaceURI();
				info.ServiceName = peer.getServiceName().getLocalPart();				
				
				array.add(info);				
			}			
		}		
		finally {
			storage_.releaseLock();			
		}
		
		return (SuperPeerInformationTransferObject[]) array.toArray(new SuperPeerInformationTransferObject[array.size()]);
	}
}
