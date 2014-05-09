package chat.ggc.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import chat.ggc.utilities.ChatProtocol;
import chat.ggc.utilities.MsgSender;

public class Client implements ChatProtocol
{	
	private String userName;
	private String serverIP;
	private int port;
	private int ID;
	private InetAddress serverAddress;
	private DatagramSocket socket;
	private volatile boolean running;
	
	public Client(String userName, String serverIP, int port)
	{
		this.userName = userName;
		this.serverIP = serverIP;
		this.port = port;
	}
	
	public void openConnection()
	{
		try {
			socket = new DatagramSocket();
			serverAddress = InetAddress.getByName(serverIP);
			running = true;
		} catch(UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public DatagramPacket receive()
	{
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch(SocketException e){
//			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		return packet;
	}
	
	public String processPacket(DatagramPacket packet)
	{
		String str = new String(packet.getData());
		char protocol = str.charAt(0);
		String subStr = str.substring(1).trim();
		System.out.println(subStr);
		String[] split;
		String result = "";
		switch(protocol)
		{
		case CONNECT://str will look like "cWelcome ClientName/c/clientID/i/"
			split = subStr.split("/i/|/c/");
			result = split[0];
			ID = Integer.parseInt(split[1]);
			break;
		case MESSAGE://str will look like "m<Name>:Message/m/"
			result = subStr.split("/m/")[0];
			break;
		case DISCONNECT://str will look like "dThe user ClientName disconnected/d/"
			result = subStr.split("/d/")[0];
			break;
		}
		return result;
	}
	
	public void send(String msg, char protocol)
	{
		switch(protocol)
		{
		case CONNECT:
			msg = CONNECT + userName + "/c/";
			break;
		case MESSAGE:
			msg = MESSAGE + "" + ID + "/i/" + msg + "/m/";
			break;
		case DISCONNECT:
			msg = DISCONNECT + "" + ID + "/d/";
			break;
		}
		
		Thread send = new MsgSender(socket, msg.getBytes(), serverAddress, port);
		send.start();
	}
	
	public void close()
	{
		send("", DISCONNECT);
		System.out.println("Closing Client");
		running = false;
		new Thread(new Runnable() {
			public void run() {
				synchronized (socket) {
					socket.close();
				}
			}
		}).start();;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public int getID()
	{
		return ID;
	}
}