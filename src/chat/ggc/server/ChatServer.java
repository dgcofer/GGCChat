package chat.ggc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatServer
{
	private DatagramSocket socket;
	private volatile boolean running;
	private Thread manage, receive;
	
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
		manageClients();
		receive();
		Scanner input = new Scanner(System.in);
		while(running)
		{
			String text = input.nextLine();
			if(text.equalsIgnoreCase("/quit"))
			{
				closeServer();
			}
		}
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
			System.out.println("A new user connected");
			String name = str.split("/c/|/e/")[1];
			System.out.println("The user " + name + " connected");
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort()));
		}
		else
		{
			System.out.println("User sent " + str.split("/m/|/e/")[1]);
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
