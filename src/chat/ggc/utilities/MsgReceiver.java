package chat.ggc.utilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MsgReceiver extends Thread
{
	private PacketProcessor processor;
	private DatagramSocket socket;
	
	public MsgReceiver(PacketProcessor processor, String threadName)
	{
		super(threadName);
		this.processor = processor;
		this.socket = processor.getSocket();
	}
	
	@Override
	public void run()
	{
		while(processor.isRunning())
		{
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch(SocketException e) {
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			processor.processPacket(packet);
		}
	}
}
