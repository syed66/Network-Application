package com.NetworkChatter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
//creating client
public class ClientBackend {
	//using UDP to send and receive packets 
	private DatagramSocket socket;

	private String addressOfServer;
	private String ID;
	//storing port of server
	private int portOfServer;
	//storing ip of server
	private InetAddress IPofServer;
	//storing ip of client to listen to
	private InetAddress IPtoListen;
	private String AddressToListen;
	//storing port to listen to 
	private int portTolisten;
	private Thread sendToServer;
	public String message;
	public ClientBackend(String ID, String address, int port,String IPtoListen,int portToListen) {
		this.ID = ID; 
		this.addressOfServer = address;
		this.portOfServer = port; 
		this.AddressToListen =IPtoListen;
		this.portTolisten=portToListen; 

	}

	

	public String getAddressOfServer() {
		return addressOfServer;
	}

	public int getPortOfServer() {
		return portOfServer;
	}
	
	//checking for valid connection
	public boolean openConnection() {
		try {
			//creating a socket to send and receive packets, which binds to port and address entered by user 
			IPtoListen=InetAddress.getByName(AddressToListen);
			//creating socket with address and port given
			socket = new DatagramSocket(portTolisten,IPtoListen);			
			//converting ip inputed to InetAddress of server
			IPofServer = InetAddress.getByName(addressOfServer);
			
			//checking if ip and port of server entered is valid by sending empty packet to given address
			DatagramPacket packet =new DatagramPacket("/test/test/e/".getBytes(),"/test/test/e/".getBytes().length,IPofServer,portOfServer);
			socket.send(packet);

		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false; 
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}
	
	//receiving packets of data
	public String receiveData() {
		byte[] data = new byte[1024]; 
		//creating datagram packet using data received 
		DatagramPacket packet = new DatagramPacket(data, data.length);
 
		try {
			//receiving data sent to socket and storing it in the packet
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		//converting the packet bytes to a string 
		message = new String(packet.getData());
		//returning the message
		return message;
	}
//sending packets of data 
	public void sendToServer(final byte[] data) {
		//creating send thread 
		sendToServer = new Thread("Send") {
			public void run() {
				//converting data to packet and sending it to server ip and port
				DatagramPacket packet = new DatagramPacket(data, data.length, IPofServer, portOfServer);
				try { 
					//sending packet of data to server
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};
		//starting thread
		sendToServer.start();
	}
//closing socket
	public void close() {
		new Thread() {
			public void run() {
				synchronized(socket) {
					socket.close();
			}
		 }
	  };
	}

	public void setID(String ID) {
		this.ID = ID;

	}

	public String getID() {
		return this.ID;
	}

}