package com.NetworkChatter.server;

public class RunServer {
	private int port;
	private Server server;
	public static void main(String[] args) {
		int port;  

		if (args.length != 1) {
			System.out.println("Please run the file like this:    java RunServer.java [port to run on]");
			return; 
		}
		port =  Integer.parseInt(args[0]);
		//creating server using port inputed in command line
		new RunServer(port);
		
	} 
	public RunServer(int port) {
		this.port = port;
		//creating server using the port inputed
		server = new Server(port);
	} 
}