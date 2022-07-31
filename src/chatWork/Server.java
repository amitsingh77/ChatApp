package chatWork;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Server extends JFrame {

	ServerSocket server;
	Socket socket;
	BufferedReader br;
	PrintWriter out;
	
	private JLabel heading=new JLabel("Server Area");
	private JTextArea messageArea =new JTextArea();
	private JTextField messageInput=new JTextField();
	private Font font=new Font("Roboto",Font.PLAIN,20);

	public Server() {
		try {
			server = new ServerSocket(7777);
			System.out.println("server is ready to accept connection");
			System.out.println("waiting...");
			socket = server.accept();

			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out = new PrintWriter(socket.getOutputStream());

			createGUI();
			handleEvents();
			startReading();
			//startWriting();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void handleEvents() {
		messageInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//System.out.println("Key released "+e.getKeyCode());
				if(e.getKeyCode()==10) {
					//System.out.println("You have pressed enter button");
					String contentToSend=messageInput.getText();
					//messageArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
					messageArea.append("Me :"+contentToSend+"\n");
					
					out.println(contentToSend);
					out.flush();
					messageInput.setText("");
					messageInput.requestFocus();
				}
			}
			
		});
	}

	public void createGUI() {
		this.setTitle("Client Messanger[END]");
		this.setSize(600,700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		heading.setFont(font);
		messageArea.setFont(font);
		messageInput.setFont(font);
		
		heading.setIcon(new ImageIcon("chaticon.jpg"));
		heading.setHorizontalAlignment(SwingConstants.CENTER);
		heading.setVerticalTextPosition(SwingConstants.BOTTOM);
		heading.setHorizontalTextPosition(SwingConstants.CENTER);
		heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		messageArea.setEditable(false);
		
		messageInput.setHorizontalAlignment(SwingConstants.CENTER);
		this.setLayout(new BorderLayout());
		
		this.add(heading,BorderLayout.NORTH);
		JScrollPane jScrollPane=new JScrollPane(messageArea);
		this.add(jScrollPane,BorderLayout.CENTER);
		jScrollPane.getVerticalScrollBar().addAdjustmentListener(
				e -> {
				    if ((e.getAdjustable().getValue() - e.getAdjustable().getMaximum()) > -jScrollPane.getHeight() - 20){
				        e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				    }   
				});
		this.add(messageInput,BorderLayout.SOUTH);
		
		this.setVisible(true);
	}

	public void startReading() {
		Runnable r1 = () -> {

			System.out.println("reader started");

			try {
				while (true) {
					String msg = br.readLine();
					if (msg.equals("exit")) {
						System.out.println("client terminated the chat");
						JOptionPane.showMessageDialog(this," Server Terminated ");
						messageInput.setEnabled(false);
						socket.close();
						break;
					}
					//System.out.println("Client : " + msg);
					//messageArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
					//messageArea.setSelectedTextColor(getBackground());
					messageArea.append("Client : " + msg+"\n");
					
				}
			} catch (IOException e) {

				//e.printStackTrace();
				System.out.println("Connection closed");
			}

		};
		new Thread(r1).start();
	}

	public void startWriting() {
		Runnable r2 = () -> {
			System.out.println("Writer started");

			try {
				while (true && !socket.isClosed()) {

					BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
					String content = br1.readLine();

					out.println(content);
					out.flush();
					if (content.equals("exit")) {
						socket.close();
						break;
					}

				}
				System.out.println("Connection lost");

			} catch (IOException e) {

				e.printStackTrace();
			}

		};

		new Thread(r2).start();
	}

	public static void main(String[] args) {
		System.out.println("going to start the server");
		new Server();
	}
}
