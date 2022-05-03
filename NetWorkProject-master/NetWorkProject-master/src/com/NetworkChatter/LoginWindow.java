package com.NetworkChatter;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;

public class LoginWindow extends JFrame {
//initialising variables for various text inputs 
	private JPanel contentPane;
	private JTextField ID; 
	private JTextField AddressOfServer;
	private JLabel lblName; 
	private JTextField PortOfServer;
	private JTextField AddressToListen;
	private JTextField PortToListen;


	private JLabel lblPort;
	private JLabel lblPortToListen; 

	//launches the application
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try { 
					LoginWindow frame = new LoginWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//createst the frame
	public LoginWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		//setting aestethics of login window
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(290,400);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setForeground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		ID = new JTextField();
		ID.setBounds(65, 47, 154, 20); 
		contentPane.add(ID);
		ID.setColumns(10); 
		
		//creating ip address input field
		JLabel lblNIPAddress = new JLabel("IP Address (of server):");
		lblNIPAddress.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNIPAddress.setForeground(Color.WHITE);
		lblNIPAddress.setBounds(65, 78, 154, 14);
		contentPane.add(lblNIPAddress);
		
		AddressOfServer = new JTextField();
		AddressOfServer.setBounds(65, 103, 154, 20);
		contentPane.add(AddressOfServer);
		AddressOfServer.setColumns(10);
		//creating ID field
		lblName = new JLabel("ID:");
		lblName.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblName.setForeground(Color.WHITE);
		lblName.setBounds(75, 19, 83, 14);
		
		contentPane.add(lblName);
		//creating port input field
		PortOfServer = new JTextField();
		PortOfServer.setColumns(10);
		PortOfServer.setBounds(65, 159, 154, 20);
		contentPane.add(PortOfServer);
		lblPort = new JLabel("Port (of server):");
		lblPort.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPort.setForeground(Color.WHITE);
		lblPort.setBounds(65, 134, 144, 14);
		contentPane.add(lblPort);
		
		//creating ip address to listen to input field
		JLabel lblNIPAddressToListenTo = new JLabel("IP Address (to listen to):");
		lblNIPAddressToListenTo.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNIPAddressToListenTo.setForeground(Color.WHITE);
		lblNIPAddressToListenTo.setBounds(65, 190, 154, 14);
		contentPane.add(lblNIPAddressToListenTo);
		  
		AddressToListen = new JTextField();
		AddressToListen.setBounds(65, 215, 154, 20);
		contentPane.add(AddressToListen);
		AddressToListen.setColumns(10);
		
		//creating port to listen to input field
		PortToListen = new JTextField();
		PortToListen.setColumns(10);
		PortToListen.setBounds(65, 271, 154, 20);
		contentPane.add(PortToListen);
		lblPortToListen = new JLabel("Port (to listen to):");
		lblPortToListen.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPortToListen.setForeground(Color.WHITE);
		lblPortToListen.setBounds(65, 246, 144, 14);
		contentPane.add(lblPortToListen); 
		//creating login button
		JButton btnNewButton = new JButton("Login");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		//adding action listener to button
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				//if button is pressed, user info is extracts and user is logged in and an attempt to connect to server is made
				String Id = ID.getText();
				String addressOfServer = AddressOfServer.getText(); 
				int portOfServer = Integer.parseInt(PortOfServer.getText());
				String addressToListen = AddressToListen.getText();
				int portToListen = Integer.parseInt(PortToListen.getText());

				login(Id, addressOfServer, portOfServer,addressToListen,portToListen);
			} 
 
			
		});
		btnNewButton.setBounds(98, 311, 89, 23);
		contentPane.add(btnNewButton);
	}
	 
	//once user has entered details, it will close the window and create a client window 
	private void login(String ID, String IPofServer, int portOfServer, String IPtoListen, int portToListen) {		
			dispose(); 
			new ClientInterface(ID, IPofServer, portOfServer,IPtoListen,portToListen);		
	}
}