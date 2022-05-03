package com.NetworkChatter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.NetworkChatter.server.Server;

//////////////Testing for valid/invalid client parameter inputs, test takes around 5 seconds 
class ClientBackendTest {
	public static Server server;
	@BeforeAll
	public static void runBefore() {	
		//openign server
		server=new Server(8192);
		//creating dummy user
		 new ClientInterface("abdill","127.0.0.1",8192,"127.0.0.1",111);
	}
	@Test
	void testOpenConnection() { 
		//testing for correct cases,returns true if valid, false otherwise
		//valid inputs, should return true
		assertSame(new ClientBackend("jake","127.0.0.1",8192,"127.0.0.1",445).openConnection(), true);
		assertSame(new ClientBackend("bob","localhost",8192,"localhost",5040).openConnection(), true);
	} 
	
	//testing wrong inputs, should not run, returns false and throws exceptions
	@Test
	void testOpenConnection2() {
		//port already used in runBefore, should return false
		assertSame(new ClientBackend("shahzeb","127.0.0.1",8192,"127.0.0.1",111).openConnection(), false);
		//incorrect server address, should return false
		assertSame(new ClientBackend("tejal","127.0.0",8192,"127.0.0.1",135).openConnection(), false);
		//invalid server address, should return false
		assertSame(new ClientBackend("seth","fdgdf",8192,"127.0.0.1",132).openConnection(), false);
		//invalid client address, should return false 
		assertSame(new ClientBackend("john","127.0.0.1",8192,"fgdre",136).openConnection(), false);
		
	}
	@AfterAll
	public static void closeServer() {
		//closing server
		server.close();
	}
	

}
 