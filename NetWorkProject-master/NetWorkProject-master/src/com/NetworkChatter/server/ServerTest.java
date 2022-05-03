package com.NetworkChatter.server;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.NetworkChatter.ClientBackend;
class ServerTest {
	public static Server server;
	public static ClientBackend testClient;
	public static String message_one="/message/21:00  ID:shah: /clients/e/";
	
	

	@BeforeAll
	public static void runBefore() {		
		//opening server 
		server = new Server(8192);
		//creating user to send data to server
		testClient = new ClientBackend("shah","127.0.0.1",8192,"127.0.0.1",111); 
		//sends "test" string
		testClient.openConnection();
		//sending  members request from client to server
		testClient.sendToServer(message_one.getBytes());
	}
									
	@Test
	@DisplayName("checking if message are sent by client and received by server and ")
	void testRun() {
		//checking if message for request of members information was received by server
		assertEquals(server.message.split("/e/")[0],"/message/21:00  ID:shah: /clients");
		//checking if message was sent by server and received by client
		assertEquals(testClient.receiveData().startsWith("/clients"),true);
		//sending normal message for next test from client
		testClient.sendToServer("/message/13:52  ID:shah: hello/e/".getBytes());
		 
	}
	@Test
	void testRun1() {
		//checking if normal message from client received by server
		assertEquals(server.message.split("/e/")[0],"/message/13:52  ID:shah: hello");

	} 
	
	

	@AfterAll
	public static void closeServer() {
		server.close();
	} 
 
}

