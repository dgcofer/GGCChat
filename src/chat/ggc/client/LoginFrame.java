package chat.ggc.client;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This class was built using Eclipse WindowBuilder Pro
 * @author Derek
 *
 */
@SuppressWarnings("serial")
public class LoginFrame extends JFrame implements ActionListener
{
	private static Font arial = new Font("Arial Black", Font.PLAIN, 12);
	
	private JTextField txtUsername;
	private JTextField txtServIP;
	private JLabel lblServPort;
	private JTextField txtServPort;

	/**
	 * Create the frame.
	 */
	public LoginFrame()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		getContentPane().setFont(arial);
		setTitle("GGC Chat Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 270, 297);
		setResizable(false);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(arial);
		lblUsername.setBounds(88, 21, 71, 14);
		getContentPane().add(lblUsername);
		
		txtUsername = new JTextField();
		txtUsername.setFont(arial);
		txtUsername.setBounds(52, 52, 149, 20);
		getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		JLabel lblServIP = new JLabel("Server IP");
		lblServIP.setFont(arial);
		lblServIP.setBounds(88, 83, 64, 14);
		getContentPane().add(lblServIP);
		
		txtServIP = new JTextField();
		txtServIP.setFont(arial);
		txtServIP.setBounds(52, 108, 149, 20);
		getContentPane().add(txtServIP);
		txtServIP.setColumns(10);
		
		lblServPort = new JLabel("Server Port");
		lblServPort.setFont(arial);
		lblServPort.setBounds(88, 139, 74, 14);
		getContentPane().add(lblServPort);
		
		txtServPort = new JTextField();
		txtServPort.setFont(arial);
		txtServPort.setBounds(52, 164, 149, 20);
		getContentPane().add(txtServPort);
		txtServPort.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setFont(arial);
		btnConnect.setBounds(83, 212, 89, 23);
		btnConnect.addActionListener(this);
		getContentPane().add(btnConnect);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String userName = txtUsername.getText();
		String IP = txtServIP.getText();
		int port = 12345;//The default port
		try {
			port = Integer.parseInt(txtServPort.getText());
		}catch(NumberFormatException ex) {
			ex.printStackTrace();
			txtServPort.setText("Must be a number");
		}
		dispose();
		ChatClient client = new ChatClient(IP, port, userName);
		client.createGui();
		client.openConnection();
		client.runClient();
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
