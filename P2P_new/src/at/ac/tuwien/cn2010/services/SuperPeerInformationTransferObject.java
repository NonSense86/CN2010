package at.ac.tuwien.cn2010.services;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SuperPeerInformationTransferObject {
	
	@XmlElement
	public String PeerUrl;
	
	@XmlElement
	public String ServiceName;
	
	@XmlElement
	public String Namespace;		
}
