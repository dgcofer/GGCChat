package chat.ggc.client;

import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

/**
 * Made using Eclipse Window Builder
 * @author Derek
 *
 */
@SuppressWarnings("serial")
public class ClientGui extends JFrame
{
	private JPanel contentPane;
	private JTextField txtMessage;

	private JTextArea console;
	
	private Client client;


	/**
	 * Create the frame.
	 */
	public ClientGui(final Client client)
	{
		this.client = client;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("GGC Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.menu);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		setContentPane(contentPane);

		console = new JTextArea();
		console.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		console.setForeground(SystemColor.textText);
		console.setFont(new Font("Arial", Font.PLAIN, 12));
		console.setLineWrap(true);
		console.setEnabled(false);
		
		JScrollPane conScrollPane = new JScrollPane(console);

		txtMessage = new JTextField();
		txtMessage.setFont(new Font("Consolas", Font.PLAIN, 12));
		txtMessage.addKeyListener(new ClientListener());
		txtMessage.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtMessage.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ClientListener());
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(conScrollPane, GroupLayout.DEFAULT_SIZE, 704, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(txtMessage, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(btnSend)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(2)
					.addComponent(conScrollPane, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSend)))
		);
		contentPane.setLayout(gl_contentPane);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				client.close();
			}
		});
	}
	
	public void listen()
	{
		client.send("", Client.CONNECT);
		new Thread(new Runnable() {
			public void run() {
				while(client.isRunning()) {
					synchronized (client) {
						DatagramPacket packet = client.receive();
						String msg = client.processPacket(packet);
						appendToConsole(msg);
					}
				}
			}
		}, "Client-Listener").start();
	}

	public void appendToConsole(final String msg)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				console.append(msg + "\n");
			}
		});
	}
	
	private class ClientListener extends KeyAdapter implements ActionListener
	{
		
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				sendToClient();
			}
		}
		
		public void actionPerformed(ActionEvent e)
		{
			sendToClient();
		}
		
		private void sendToClient()
		{
			String msg = txtMessage.getText();
			client.send(msg, Client.MESSAGE);
			txtMessage.setText("");
		}
	}
}
