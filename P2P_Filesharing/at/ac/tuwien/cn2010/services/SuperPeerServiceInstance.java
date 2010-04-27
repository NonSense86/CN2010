package at.ac.tuwien.cn2010.services;

import java.net.InetAddress;

import javax.xml.ws.Endpoint;

import at.ac.tuwien.cn2010.IPeer;

public class SuperPeerServiceInstance implements IPeer {
	
	private InetAddress address_;
	private int port_;
	private Endpoint endpoint_;
	
	public SuperPeerServiceInstance(InetAddress address, int port) {
		this.address_ = address;
		this.port_ = port;
	}
	
	/**
	 * Starts the SuperPeer. After starting the peer, he is listening
	 * and waits for Super Peer Requests and Peer Requests. Also a 
	 * scheduler is started to discover the environment
	 */
	@Override
	public void Run() {
		System.out.println("[SuperPeerInstance] Run ...");
		
		String strUrl = String.format("http://%s:%d/SuperPeerService", address_.getHostAddress(), port_);
		
		System.out.println("[SuperPeerInstance] Publish endpoint at " + strUrl);
		
		endpoint_ = Endpoint.publish(strUrl, new SuperPeerListener());		
	}

	@Override
	public void Stop() {
		System.out.println("[SuperPeerInstance] Stop");
		
		endpoint_.stop();
	}

}