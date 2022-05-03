package com.NetworkChatter.server;

///////////////////committed
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
//list stores current clients
	private List<Member> members = new ArrayList<Member>();
	// socket for server
	private DatagramSocket socket;
	// port to run server on
	private int port;
	// boolean to check wether server is running or not
	private boolean running = false;
	// thread to run,manage server,send and receive data
	private Thread run, send, receive;
	// coordinator;
	private Member coordinator;

	// storing message received from packet
	public String message;

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		// running server
		run = new Thread(this, "Server");
		run.start();
	}

	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		receiveData();
	}

	// thread to receive data sent by client
	private void receiveData() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					// byte to store all data received
					byte[] data = new byte[1024];
					// creating packet to receive data
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						// receiving data sent to server socket
						socket.receive(packet);
					} catch (SocketException e) {
					} catch (IOException e) {
						e.printStackTrace();
					}
					// processing data received by user
					processMessage(packet);
				}

			}

		};
		receive.start();
	}

	// close server
	public void close() {
		for (int i = 0; i < members.size(); i++) {
			disconnect(members.get(i).getID());
		}
		running = false;
		socket.close();
	}

	// sending packets to all clients
	public void sendToAll(String message) {
		if (message.startsWith("/message/")) {
			String text = message.substring(9);
			text = text.split("/end/")[0];
		}
		// looping through all clients and sending them packet of data
		for (int i = 0; i < members.size(); i++) {
			Member member = members.get(i);
			send(message.getBytes(), member.IP, member.port);
		}
	}

	// sending packet to client
	public void send(byte[] data, InetAddress IP, int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, IP, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

//sending messages from server to a client
	public void send(String message, InetAddress IP, int port) {
		message += "/end/";
		send(message.getBytes(), IP, port);
	}

	// checking if a ID exists
	private boolean IDexists(String ID) {
		boolean valid = false;
		for (int i = 0; i < members.size(); i++) {
			if (members.get(i).getID().equals(ID)) {
				valid = true;
			}
		}
		;
		return valid;
	}

	// processing the message received by user
	private void processMessage(DatagramPacket packet) {
		// storing text received by user
		message = new String(packet.getData());

		if (message.startsWith("/connect/")) {
			// extracting id
			String ID = message.split("/connect/|/end/")[1];
			// checking for duplicate ID
			boolean idDuplicate = IDexists(ID);

			// return error to user if id already in use
			if (idDuplicate) {
				send("/duplicate/ID DUPLICATION", packet.getAddress(), packet.getPort());

			} else {
				members.add(new Member(ID, packet.getAddress(), packet.getPort()));
				// sending id to client side
				String id = "/connect/" + ID;
				send(id, packet.getAddress(), packet.getPort());
				// assigning coordinator when someone joins
				if (members.size() != 0) {
					assignCoordinator();

				}
				// notifying new user of who coordinator is
				send("the desginated coordinator for this chat is " + coordinator.getID()
						+ " \nTo View current users type '/members' ", packet.getAddress(), packet.getPort());
				// notifying users of new entries to chat
				sendToAll("(ID:" + ID + ") just joined the chat! ");
				System.out.println("(ID:" + ID + ") just joined the chat!");
			}

			// if packet received is a normal message to send to members
		} else if (message.startsWith("/message/")) {
			String ID = message.split(":")[2];

			// sending to all clients
			String mainText = message.substring(9);

			// extracting only text without users name
			String text = mainText.split("/end/")[0].split(": ")[1];

			// if user is responding to coordinators request to see who is active
			if (text.equals("ABC")) {
				// sending active user to coordinator
				send("ID:" + ID + " is active and has responded.", coordinator.getAddress(), coordinator.getPort());
			}

			// for individual chat
			else if (text.startsWith("ID")) {
				try {
					send(mainText, packet.getAddress(), packet.getPort());
					// extracting user to talk to
					String clientToTalkTo = text.substring(3).split(":")[0];
					// checking if user the client wants to talk to exists
					boolean exists = IDexists(clientToTalkTo);
					if (exists) {
						for (int i = 0; i < members.size(); i++) {
							Member client = members.get(i);
							if (members.get(i).getID().equals(clientToTalkTo)) {
								send(mainText, client.getAddress(), client.port);
							}
						}
					} else {
						send("The user you want to talk to does not exist", packet.getAddress(), packet.getPort());

					}
				} catch (StringIndexOutOfBoundsException e) {
					send("Please enter the user you want to talk to", packet.getAddress(), packet.getPort());

				} catch (ArrayIndexOutOfBoundsException e) {
					send("Please enter the user you want to talk to", packet.getAddress(), packet.getPort());

				}

			}

			// if user wants to know current members in chat
			else if (text.startsWith("/members")) {
				send(mainText, packet.getAddress(), packet.getPort());
				String CurrentUsersText = "/members/";
				CurrentUsersText += "==========\n" + "" + "\n\nCurrent users: \n";
				for (int i = 0; i < members.size(); i++) {
					Member c = members.get(i);
					// printing name of coordinator and clients
					if (c.getID().equals(coordinator.getID())) {
						CurrentUsersText += "\nCoordinator: " + "(ID:" + c.getID() + ") IP:port: "
								+ c.IP.toString().trim() + ":" + c.port;
					} else {
						CurrentUsersText += "\n(ID:" + c.getID() + ") IP:Port: " + c.IP.toString().trim() + ":"
								+ c.port;
					}

				}

				CurrentUsersText += "\n" + "\n==========";
				send(CurrentUsersText, packet.getAddress(), packet.getPort());

			}

			// checking if a message is coming from coordinator
			else if (ID.equals(coordinator.getID())) {
				sendToAll(message);

				// if coordinator wants to know who is active
				if (text.equals("who is active?")) {
					sendToAll(
							"The coordinator wants to know Who is Active. please responde with 'ABC' if you are in the chat.");
				}

				// if coordinator sends the '/kick [id]' command to server, then it will kick
				// given user from chat
				if (text.startsWith("/kick")) {
					// extracting name of user to kick
					try {
						String userToKick = text.substring(6);
						// Checking if user exists
						boolean exists = IDexists(userToKick);
						if (exists) {
							disconnect(userToKick);
						}

						if (!exists) {
							send("Client " + userToKick + " doesn't exist! check name.", packet.getAddress(),
									packet.getPort());
						}
						// if user does not enter a ID to kick
					} catch (StringIndexOutOfBoundsException e) {
						send("Please enter a clients name or ID", packet.getAddress(), packet.getPort());
					}

				}
			} else {
				sendToAll(message);
			}

		}

		// a prefix with /disconnect/ indicates a user disconnecting
		else if (message.startsWith("/disconnect/")) {
			String id = message.split("/disconnect/|/end/")[1];
			// Disconnecting the user
			disconnect(id);
		}
	}

	// disconnect user
	private void disconnect(String id) {
		Member client = null;
		// checking if user exists
		boolean existed = false;
		for (int i = 0; i < members.size(); i++) {
			// checking if user exists

			if (members.get(i).getID().equals(id)) {
				client = members.get(i);
				// removing user from list
				members.remove(i);
				existed = true;
				// letting user know they have been kicked
				send("/kicked/You have been disconnected by the coordinator", client.getAddress(), client.getPort());
				break;
			}
		}
		if (!existed) {
			return;
		}
		String message = "";
		message = "User " + " (ID:" + client.getID() + ") @ " + client.IP.toString().trim() + ":" + client.port
				+ " has been disconnected.";
		sendToAll(message);
		System.out.println(message);
		// checking if person disconnected is coordinator
		if (client.getID() == coordinator.getID()) {
			try {
				// assigning new coordinator if coordinator disconnects
				assignCoordinator();
				sendToAll("COORDINATOR disconected, new coordinator is ID:" + coordinator.getID());
				System.out.println("COORDINATOR disconected, new coordinator is ID:");

				// if no active members are online
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("No more members active");
			}

		}

	}

	// assign coordinator
	private void assignCoordinator() {
		if (members.size() == 1) {
			// letting first user know they are first user
			sendToAll("You are the first user!");
		}
		// assigns first person who joins as coordinator
		if (members.size() != 0) {
			Member firstUser = members.get(0);
			coordinator = new Member(firstUser.getID(), firstUser.getAddress(), firstUser.getPort());
		}
	}
}