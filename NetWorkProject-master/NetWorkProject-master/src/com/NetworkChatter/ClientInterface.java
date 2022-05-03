package com.NetworkChatter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;


import javax.swing.JOptionPane;

import java.awt.Color;

public class ClientInterface extends JFrame implements Runnable {
	//initialisng variables
	private static final long serialVersionUID = 1L;
	//GUI variables
	private JTextField textMessage;
	private JTextArea history;
	private DefaultCaret caret;
	public JPanel contentPane;
	//thread to listen to data sent to member
	private Thread listenForData, run;
	
	//stores member
	private ClientBackend member; 
	private boolean running = false; 

	private JButton sendButton;
	
	
	public ClientInterface(String ID, String IPofServer, int portOfServer,String IPtoListen,int portTolisten) {
		setTitle("Network Chat Program"); 
		//creating client
		member = new ClientBackend(ID, IPofServer, portOfServer,IPtoListen,portTolisten);
		//creating window
		createWindow();
		//checking if information entered is valid
		boolean connect = member.openConnection();

		if (!connect) {
			JOptionPane.showMessageDialog(contentPane, "Connection failed!, Please double check your login details and retry!");
			dispose();
			System.exit(0);
		}else {
			console("Trying to connect to " + IPofServer + ": " + portOfServer + ",  user: " + ID);
			//a /connect/ prefix in the beginning shows the server that a client is trying to connect
			String connection = "/connect/" + ID + "/end/";
			//sending connection to servers
			member.sendToServer(connection.getBytes());

			running = true;
			run = new Thread(this, "Running");
			run.start();
		}
		

	}
	

	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//setting aestethics for window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550); 
		setLocationRelativeTo(null);
		

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		//creating grid layout
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 28, 815, 30, 7};
		gbl_contentPane.rowHeights = new int[] { 25, 485, 40};
		contentPane.setLayout(gbl_contentPane);
		//creating a JTextArea where the chat history will be stored
		history = new JTextArea();
		history.setForeground(Color.WHITE);
		history.setBackground(Color.BLACK);
		history.setEditable(false);
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.weightx = 1;
		scrollConstraints.weighty = 1;
		scrollConstraints.insets = new Insets(0, 5, 0, 0);
		contentPane.add(scroll, scrollConstraints);
		//creating text input field where users will type
		textMessage = new JTextField();
		textMessage.setForeground(Color.BLACK);
		textMessage.setBackground(Color.WHITE);
		//creating KeyListener, so when enter is pressed, the message inputed by user will be sent to all user
		textMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					//sending message
					send(textMessage.getText(), true);
				}
				//exit and disconnect user if ctrl-c is pressed
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
					//disconnecting user
					String disconnect = "/disconnect/" + member.getID() + "/end/";
					send(disconnect, false);
					running = false;
					member.close();
					//closing window
					dispose();
				}

			}
		});
		GridBagConstraints gbc_textMessage = new GridBagConstraints();
		gbc_textMessage.insets = new Insets(0, 0, 0, 5);
		gbc_textMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_textMessage.gridx = 0;
		gbc_textMessage.gridy = 2;
		gbc_textMessage.gridwidth = 2;
		gbc_textMessage.weightx = 1;
		gbc_textMessage.weighty = 0;
		contentPane.add(textMessage, gbc_textMessage);
		textMessage.setColumns(10);

		sendButton = new JButton("Send");
		//creating action listener to send button so messages are sent
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					//sending messages to users
					send(textMessage.getText(), true);

			}
		});
		GridBagConstraints gbc_Send_btn = new GridBagConstraints();
		gbc_Send_btn.insets = new Insets(0, 0, 0, 5);
		gbc_Send_btn.gridx = 2;
		gbc_Send_btn.gridy = 2;
		gbc_Send_btn.weightx = 0;
		gbc_Send_btn.weighty = 0;
		contentPane.add(sendButton, gbc_Send_btn);
		
		//closing connection when window is closed and informing server
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String disconnect = "/disconnect/" + member.getID() + "/end/";
				send(disconnect, false);
				running = false;
				member.close(); 

			}
		});

		setVisible(true);
		textMessage.requestFocusInWindow();
	}

	public void run() {
		listenForData();
	}
//sending messages to server
	private void send(String message, boolean text) {
		if (message.equals("")) {
			return;
		}
		if (text) {
			//getting time for time stamp
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			message =  sdf.format(cal.getTime())+"  ID:"+member.getID() + ": " + message;
			message = "/message/" + message + "/end/";
			textMessage.setText("");
		}
		
		member.sendToServer(message.getBytes());
		
	}
//listening to messages received from server
	public void listenForData() {
		listenForData = new Thread("Listen") {
			public void run() {
				while (running) {
					String message = member.receiveData();  
					if (message.startsWith("/connect/")) {
						//setting id of the client on 
						member.setID(message.split("/connect/|/end/")[1]);
						//if a duplicate id was inputted
					}else if(message.startsWith("/duplicate/")){
						member.close();
						JOptionPane.showMessageDialog(contentPane, "Duplicate ID please enter again");
						dispose();
						System.exit(0);
						//if server sends a message
					}else if (message.startsWith("/message/")) {
						String text = message.substring(9);
						text = text.split("/end/")[0];
						console(text);
						//if server sends a ping for activity
					} else if (message.startsWith("/ping/")) { 
						String text = "/ping/" + member.getID() + "/end/";
						send(text, false);
					}else if(message.startsWith("/kicked/")) {
						String[] msg = message.split("/kicked/|/end/");
						console(msg[1]);
						//disabled text box if user is removed
						textMessage.setEnabled(false);
						sendButton.setEnabled(false);
						//if clients list is received
					}else if(message.startsWith("/members/")) {
						String[] msg = message.split("/members/|/end/");
						console(msg[1]);
					}
					else {
						console(message.split("/end/")[0]);
					}
				}

			}
		};
		listenForData.start();
	}
//appending messages onto console
	public void console(String message) {
		//appending messages to history so it is visible by all
		history.append(message + "\n");
		history.setCaretPosition(history.getDocument().getLength());
	}
}