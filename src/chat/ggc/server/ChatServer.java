package chat.ggc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

import chat.ggc.utilities.MsgReceiver;
import chat.ggc.utilities.MsgSender;
import chat.ggc.utilities.PacketProcessor;

public class ChatServer implements PacketProcessor
{
	private DatagramSocket socket;
	private volatile boolean running;
	private volatile boolean showRaw;
	private Thread receiver;
	
	private ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
	
	public ChatServer(int port)
	{
		try {
			socket = new DatagramSocket(port);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void runServer()
	{
		System.out.println("Server started");
		//TODO Find way to display the server ip here
		System.out.println("Port: " + socket.getLocalPort());
		running = true;
		showRaw = true;
		receiver = new MsgReceiver(this, socket);
		receiver.start();
		Scanner input = new Scanner(System.in);
		while(running)
		{
			String text = input.nextLine();
			if(text.equalsIgnoreCase("/quit"))
			{
				closeServer();
			}
			else if(text.equals("/show"))
			{
				showRaw = (showRaw)? false : true;
			}
			else if(text.equals("/clients"))
			{
				System.out.println("Clients online");
				System.out.println("================");
				for (ServerClient client: clients)
				{
					System.out.println(client.getName());					
				}
			}
			else
			{
				sendToAll("/m/<Server>: " + text + "/e/");
			}
		}
	}
	
	private void sendToAll(String text)
	{
		byte[] data = text.getBytes();
		for(ServerClient client : clients)
		{
			send(data, client);
		}
	}
	
	private void send(byte[] data, ServerClient client)
	{
		Thread send = new MsgSender(socket, data, client);
		send.start();
	}
	
	public void processPacket(DatagramPacket packet)
	{
		String str = new String(packet.getData());
		if(str.startsWith("/c/"))
		{
			String name = str.split("/c/|/e/")[1];
			System.out.println("The user " + name + " connected");
			InetAddress ip = packet.getAddress();
			int port = packet.getPort();
			ServerClient client = new ServerClient(name, ip, port);
			
			String welcome = "/m/Welcome " + name + "/e/";
			send(welcome.getBytes(), client);
			sendToAll("/m/User " + name + " connected/e/");
			clients.add(client);
		}
		else if(str.startsWith("/m/"))
		{
			String[] split = str.split("/m/|/u/|/e/");
			String name = "<" + split[2] + ">: ";
			String msg = name + split[3];
			if(showRaw){
				System.out.println(msg);
			}
			sendToAll("/m/" + msg + "/e/");
		}
	}
	
	public boolean isRunning()
	{
		return running;
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
			ChatServer chat = new ChatServer(port);
			chat.runServer();
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
