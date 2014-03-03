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
	
	public ChatClient(String...args)
	{
		this.hostIP = args[0];
		this.name = args[1];
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
				String msg = "/m/" + str + "/e/";
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
		
	}
	
	private void closeClient()
	{
		running = false;
		socket.close();
	}
	
	public static void main(String[] args)
	{
		new ChatClient(args);
	}
}
