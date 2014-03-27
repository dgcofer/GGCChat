package chat.ggc.utilities;

import java.net.DatagramPacket;

public interface PacketProcessor
{
	void processPacket(DatagramPacket packet);
	boolean isRunning();
}
