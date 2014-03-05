package chat.ggc.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient
{
	private String hostIP;
	private String name;
	private InetAddress serverIP;
	private int serverPort = 12345;
	private DatagramSocket socket;
	private volatile boolean running;
	
	public ChatClient(String ip, int port, String username)
	{
		this.hostIP = ip;
		this.serverPort = port;
		this.name = username;
		openConnection();
		runClient();
	}
	
	private void openConnection()
	{
		String msg = "/c/" + name + "/e/";
		byte[] data = msg.getBytes();
		try
		{
			socket = new DatagramSocket();
			serverIP = InetAddress.getByName(hostIP);
			DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, serverPort);
			socket.send(packet);
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (SocketException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void runClient()
	{
		running = true;
		receive();
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
				send(str);
			}
		}
	}
	
	private void send(final String str)
	{
		new Thread(new Runnable() {
			public void run()
			{
				String msg = "/m//u/"+ name + "/e/" + str + "/e/";
				byte[] data = msg.getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, serverPort);
				try
				{
					socket.send(packet);
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void receive()
	{
		new Thread(new Runnable() {
			public void run()
			{
				while(running)
				{
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					}catch(SocketException e){
					}catch(IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		}).start();
	}
	
	private void process(DatagramPacket packet)
	{
		String str = new String(packet.getData());
		if(str.startsWith("/m/"))
		{
			String msg = str.split("/m/|/e/")[1];
			System.out.println(msg);
		}
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
		new ChatClient(ip, port, username);
	}
	
	private static void badArgumentMessage()
	{
		System.out.println("Must pass proper arguments \"java ChatClient IP port username\"");
	}
}
