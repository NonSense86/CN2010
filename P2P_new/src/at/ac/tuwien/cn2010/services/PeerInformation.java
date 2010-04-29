package at.ac.tuwien.cn2010.services;

import java.net.InetAddress;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PeerInformation {
	
	@XmlElement
	public String PeerAdress;
	
	@XmlElement
	public int PeerPort;
	
	@XmlElement
	public boolean IsSuperPeer;	
}
