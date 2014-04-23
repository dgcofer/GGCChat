package chat.ggc.utilities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface PacketProcessor
{
	void processPacket(DatagramPacket packet);
	boolean isRunning();
	DatagramSocket getSocket();
}
