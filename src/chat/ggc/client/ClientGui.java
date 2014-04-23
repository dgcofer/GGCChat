package chat.ggc.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.SystemColor;
import java.awt.Font;

import javax.swing.border.BevelBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
	public ClientGui()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("GGC Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 450);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.menu);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		setContentPane(contentPane);

		console = new JTextArea();
		console.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		console.setForeground(SystemColor.textText);
		console.setFont(new Font("Arial", Font.PLAIN, 11));
		console.setEnabled(false);

		txtMessage = new JTextField();
		txtMessage.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtMessage.setColumns(10);

		JTextArea clientsTxtArea = new JTextArea();
		clientsTxtArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		clientsTxtArea.setEnabled(false);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ClientListener());
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(txtMessage)
						.addComponent(console, GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnSend)
							.addGap(92))
						.addComponent(clientsTxtArea)))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(console, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE)
						.addComponent(clientsTxtArea, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSend)))
		);
		contentPane.setLayout(gl_contentPane);

	}

	public ClientGui(Client client)
	{
		this();
		this.client = client;
	}

	public void setMsg(String msg)
	{
		console.append(msg + "\n\r");
	}
	
	private class ClientListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String msg = txtMessage.getText();
//			setMsg(msg);
			String formattedName = "/m//u/" + client.getUsername() + "/u/";
			client.send(formattedName + msg + "/e/");
			txtMessage.setText("");
		}
	}
}
