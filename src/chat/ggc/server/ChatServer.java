package chat.ggc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import chat.ggc.utilities.ChatProtocol;
import chat.ggc.utilities.MsgReceiver;
import chat.ggc.utilities.MsgSender;
import chat.ggc.utilities.PacketProcessor;

public class ChatServer implements PacketProcessor, ChatProtocol
{
	private DatagramSocket socket;
	private volatile boolean running;
	private volatile boolean showRaw;
	private Thread receiver;
	
	private ServerClient[] clients;
	
	public ChatServer(int port, int numOfClients)
	{
		try {
			socket = new DatagramSocket(port);
		} catch (IOException e){
			e.printStackTrace();
		}
		clients = new ServerClient[numOfClients];
	}
	
	public void runServer()
	{
		System.out.println("Server started");
		//TODO Find way to display the server ip here
		System.out.println("Port: " + socket.getLocalPort());
		printHelp();
		running = true;
		showRaw = true;
		receiver = new MsgReceiver(this, "Server-Receiver");
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
				System.out.println("ID\tName");
				System.out.println("================");
				for (int i = 0; i < clients.length; i++)
				{
					if(clients[i] != null) {
						System.out.println(i + "\t" + clients[i].getName());
					}
				}
			}
			else if(text.equals("/help"))
			{
				printHelp();
			}
			else
			{
				sendToAll(MESSAGE + "<Server>: " + text + "/m/");
			}
		}
	}
	
	private void printHelp()
	{
		System.out.println("Here is a list of all available commands:");
		System.out.println("=========================================");
		System.out.println("/show - enables raw mode.");
		System.out.println("/clients - shows all connected clients.");
//		System.out.println("/kick [users ID or username] - kicks a user.");
		System.out.println("/help - shows this help message.");
		System.out.println("/quit - shuts down the server.");
	}
	
	private void sendToAll(String text)
	{
		byte[] data = text.getBytes();
		for(ServerClient client : clients)
		{
			if(client != null) {
				send(data, client);
			}
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
		char protocol = str.charAt(0);
		String subStr = str.substring(1);
		String[] split;
		int clientID;
		switch(protocol)
		{
		case CONNECT://str will look like: "cClientName/c/"
			String name = subStr.split("/c/")[0];
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			ServerClient client = new ServerClient(name, address, port);
			handleConnect(subStr, client);
			break;
		case MESSAGE://str will look like: "mClientID/i/Message/m/"
			split = subStr.split("/m/|/i/");
			clientID = Integer.parseInt(split[0]);
			ServerClient c = clients[clientID];
			String msg = "<" + c.getName() + ">: " + split[1];
			if(showRaw) {
				System.out.println(msg);
			}
			sendToAll(MESSAGE + msg + "/m/");
			break;
		case DISCONNECT://str will look like "dClientID/d/
			clientID = Integer.parseInt(subStr.split("/d/")[0]);
			handleDisconnect(clientID);
			break;
		}
	}
	
	private void handleConnect(String str, ServerClient client)
	{
		int clientID = addClient(client);
		if(clientID == -1) {
			String full = ERROR + "Sorry the current server is full/e/";
			send(full.getBytes(), client);
		}else {
			System.out.println("The user " + client.getName() + " connected");
			String welcome = CONNECT + "Welcome " + client.getName() + "/c/" + clientID + "/i/";
			send(welcome.getBytes(), client);
			sendToAll(MESSAGE + "User " + client.getName() + " connected/m/");
		}
	}
	
	private void handleDisconnect(int clientID)
	{
		sendToAll(DISCONNECT + "The user " + clients[clientID].getName() + " disconnected/d/");
		System.out.println("The user " + clients[clientID].getName() + " disconnected");
		clients[clientID] = null;
	}
	
	private int addClient(ServerClient client)
	{
		for(int i = 0; i < clients.length; i++)
		{
			if(clients[i] == null) {
				clients[i] = client;
				return i;
			}
		}
		return -1;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public DatagramSocket getSocket()
	{
		return socket;
	}
	
	private void closeServer()
	{
		sendToAll(MESSAGE + "<Server>: Server has been shutdown/m/");
		System.out.println("Closing Server");
		running = false;
		new Thread(new Runnable() {
			public void run() {
				socket.close();
			}
		}).start();
	}
	
	public static void main(String[] args)
	{
		if(args.length < 2) badArgumentMessage();
		
		int port = 0;
		int numOfClients = 0;
		
		try{
			port = Integer.parseInt(args[0]);
			numOfClients = Integer.parseInt(args[1]);
			ChatServer chat = new ChatServer(port, numOfClients);
			chat.runServer();
		}catch(NumberFormatException e)
		{
			badArgumentMessage();
		}
	}
	
	private static void badArgumentMessage()
	{
		System.out.println("Must pass a port number and number of clients \"java ChatServer port clients\"");
		System.exit(1);
	}
}
