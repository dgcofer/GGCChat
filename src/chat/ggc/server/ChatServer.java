package chat.ggc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatServer
{
	private DatagramSocket socket;
	private volatile boolean running;
	private Thread manage, receive, send;
	
	private ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
	
	public ChatServer(int port)
	{
		try {
			socket = new DatagramSocket(port);
			runServer();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void runServer()
	{
		System.out.println("Server started");
		running = true;
//		manageClients();//TODO not sure if I need this thread
		receive();
		Scanner input = new Scanner(System.in);
		while(running)
		{
			String text = input.nextLine();
			if(text.equalsIgnoreCase("/quit"))
			{
				closeServer();
			}
			else
			{
				sendToAll("/m/" + text + "/e/");
			}
		}
	}
	
	private void sendToAll(String text)
	{
		byte[] data = text.getBytes();
		for(ServerClient client : clients)
		{
			send(data, client.getIP(), client.getPort());
		}
	}
	
	private void send(final byte[] data, final InetAddress ip, final int port)
	{
		send = new Thread(new Runnable() {
			public void run()
			{
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		send.start();
	}
	
	private void manageClients()
	{
		manage = new Thread(new Runnable() {
			public void run()
			{
				while(running)
				{
				}
			}
		}, "Manage");
		manage.start();
	}
	
	private void receive()
	{
		receive = new Thread(new Runnable() {
			public void run()
			{
				while(running)
				{
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					}catch (SocketException e){	
					}catch(IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		}, "Receive");
		receive.start();
	}
	
	private void process(DatagramPacket packet)
	{
		String str = new String(packet.getData());
		if(str.startsWith("/c/"))
		{
			String name = str.split("/c/|/e/")[1];
			System.out.println("The user " + name + " connected");
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort()));
		}
		else if(str.startsWith("/m/"))
		{
			String name = "<" + str.split("/u/|/e/")[1] + ">: ";
			String msg = name + str.split("/m/|/e/")[2];
			System.out.println(msg);
			sendToAll("/m/" + msg + "/e/");
		}
	}
	
	private void closeServer()
	{
		System.out.println("Closing Server");
		running = false;
		socket.close();
	}
	
	public static void main(String[] args)
	{
		if(args.length < 1) badArgumentMessage();
		
		int port = 0;
		
		try{
			port = Integer.parseInt(args[0]);
			new ChatServer(port);
		}catch(NumberFormatException e)
		{
			badArgumentMessage();
		}
	}
	
	private static void badArgumentMessage()
	{
		System.out.println("Must pass a port number \"java ChatServer port\"");
		System.exit(0);
	}
}
