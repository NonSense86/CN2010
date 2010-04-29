package at.ac.tuwien.cn2010;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SuperPeerNeighborStorage {

	private ArrayList<SuperPeerInformation> neighborPeers_ = new ArrayList<SuperPeerInformation>();	
	private final Lock lock_ = new ReentrantLock(); 
	
	public void addSuperPeerNeighbor(SuperPeerInformation information) {
		
		/**
		 * Only add new peer if we dont have a peer with the same URL
		 */
		
		boolean doesExist = false;
		
		for(SuperPeerInformation info: neighborPeers_) {
			if(info.getPeerURL().equals(information.getPeerURL())) {
				doesExist = true;
			}
		}
		
		if(doesExist == false) {
			System.out.println("[SuperPeerNeighborStorage] New Peer: " + information.getPeerURL());
			
			neighborPeers_.add(information);
		}		
	}
	
	public void updateSuperPeerNeighborLastSeen(SuperPeerInformation information) {
		for(SuperPeerInformation info: neighborPeers_) {
			if(info.getPeerURL().equals(information.getPeerURL())) {
				info.setPeerLastSeen(new Date());				
			}
		}
	}
	
	public ArrayList<SuperPeerInformation> getNeighborPeersListReference() {
		return neighborPeers_;
	}
	
	public void acquireLock() {
		lock_.lock();
	}
	
	public void releaseLock() {
		lock_.unlock();
	}
}
