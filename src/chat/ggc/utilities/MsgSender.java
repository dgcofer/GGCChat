package chat.ggc.utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import chat.ggc.server.ServerClient;

public class MsgSender extends Thread
{
	private DatagramSocket socket;

	private final byte[] data;
	private final InetAddress address;
	private final int port;

	public MsgSender(DatagramSocket socket, byte[] data, ServerClient client)
	{
		super("Sender");
		this.socket = socket;
		this.data = data;
		this.address = client.getAddress();
		this.port = client.getPort();
	}

	public MsgSender(DatagramSocket socket, byte[] data, InetAddress address, int port)
	{
		super("Sender");
		this.socket = socket;
		this.data = data;
		this.address = address;
		this.port = port;
	}

	@Override
	public void run()
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try
		{
			socket.send(packet);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
