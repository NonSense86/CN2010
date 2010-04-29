package at.ac.tuwien.cn2010;

import java.net.URL;
import java.util.Date;

import javax.xml.namespace.QName;

public class SuperPeerInformation {	
	
	public SuperPeerInformation() {
		peerLastSeen_ = new Date();
	}
	
	private URL peerURL_;
	
	public URL getPeerURL() {
		return peerURL_;
	}
	
	public void setPeerURL(URL peerURL) {
		peerURL_ = peerURL;
	}
	
	private QName serviceName_;
	
	public QName getServiceName() {
		return serviceName_;
	}
	
	public void setServiceName(QName serviceName) {
		serviceName_ = serviceName;
	}
	
	private Date peerLastSeen_;
	
	public Date getPeerLastSeen() {
		return peerLastSeen_;
	}
	
	public void setPeerLastSeen(Date peerLastSeen) {
		peerLastSeen_ = peerLastSeen;
	}
}
