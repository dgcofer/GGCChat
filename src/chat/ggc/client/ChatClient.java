package chat.ggc.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import chat.ggc.utilities.MsgReceiver;
import chat.ggc.utilities.MsgSender;
import chat.ggc.utilities.PacketProcessor;

public class ChatClient implements PacketProcessor
{
	private String hostIP;
	private String userName;
	private InetAddress serverIP;
	private int serverPort;
	private DatagramSocket socket;
	private volatile boolean running;
	private Thread receiver;
	private ClientGui gui;
	
	public ChatClient(String ip, int port, String username)
	{
		this.hostIP = ip;
		this.serverPort = port;
		this.userName = username;
		gui = new ClientGui();
		gui.setVisible(true);
	}
	
	public void openConnection()
	{
		try
		{
			socket = new DatagramSocket();
			serverIP = InetAddress.getByName(hostIP);
			send("/c/"+ userName + "/e/");
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	public void runClient()
	{
		running = true;
		receiver = new MsgReceiver(this, socket);
		receiver.start();
		Scanner input = new Scanner(System.in);
		while(running)
		{
			String str = input.nextLine();
			if(str.equals("/quit"))
			{
				closeClient();
			}
			else
			{
				String formattedName = "/m//u/" + userName + "/u/";
				send(formattedName + str + "/e/");
			}
		}
	}
	
	private void send(String str)
	{
		Thread send = new MsgSender(socket, str.getBytes(), serverIP, serverPort);
		send.start();
	}
		
	public void processPacket(DatagramPacket packet)
	{
		String str = new String(packet.getData());
		if(str.startsWith("/m/"))
		{
			String msg = str.split("/m/|/e/")[1];
			System.out.println(msg);
			gui.setMsg(msg);
		}
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	private void closeClient()
	{
		System.out.println("Closing Client");
		running = false;
		socket.close();
	}
	
	public static void main(String[] args)
	{
		if (args.length < 3) badArgumentMessage();
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		String username = args[2];
		ChatClient client = new ChatClient(ip, port, username);
		client.openConnection();
		client.runClient();
	}
	
	private static void badArgumentMessage()
	{
		System.out.println("Must pass proper arguments \"java ChatClient IP port username\"");
		System.exit(0);
	}
}
