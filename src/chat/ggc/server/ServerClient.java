package chat.ggc.server;

import java.net.InetAddress;

public class ServerClient
{
	private String name;
	private InetAddress ip;
	private int port;
	
	public ServerClient(String name, InetAddress ip, int port)
	{
		this.name = name;
		this.ip = ip;
		this.port = port;
	}
	
	public String getName()
	{
		return name;
	}
	
	public InetAddress getIP()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
	}
}
