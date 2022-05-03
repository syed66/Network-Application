package com.NetworkChatter.server;

import java.net.InetAddress;

public class Member {
	//storing clients information
	public String name; 
	//ip
	public InetAddress IP;
	//port 
	public int port;
	//unique identifier
	private String ID;
	public int attempt = 0;  
 
	public Member(final String ID, InetAddress address, int port) {
		this.IP = address;
		this.port = port;
		this.ID = ID;
	}
	 
	  
	
	
	public InetAddress getAddress() {
		return IP;
	}
	public String getID() {
		return ID;
	}
	
	public int getPort() {
		return port;
	}

}