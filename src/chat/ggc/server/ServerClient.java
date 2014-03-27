package chat.ggc.server;

import java.net.InetAddress;

public class ServerClient
{
	private String name;
	private InetAddress address;
	private int port;
	
	public ServerClient(String name, InetAddress address, int port)
	{
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	public String getName()
	{
		return name;
	}
	
	public InetAddress getAddress()
	{
		return address;
	}
	
	public int getPort()
	{
		return port;
	}
}
