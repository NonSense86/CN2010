package at.ac.tuwien.cn2010.services;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface ISuperPeerListener {
	
	@WebMethod
	public SuperPeerInformationTransferObject[] getAllSuperPeers();

}
