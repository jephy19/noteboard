package client;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame {

	private JTextArea output;
	private JTextField commandField;
	private ClientConnection connection = new ClientConnection();

	public ClientGUI() {
		setTitle("Bulletin Board Client");
		setSize(700, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		output = new JTextArea();
		output.setEditable(false);

		commandField = new JTextField();
		JButton sendButton = new JButton("Send");

		sendButton.addActionListener(e -> sendCommand());

		add(new JScrollPane(output), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(commandField, BorderLayout.CENTER);
		bottom.add(sendButton, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		connect();
	}

	private void connect() {
		try {
			connection.connect("localhost", 12345, msg -> SwingUtilities.invokeLater(() -> output.append(msg + "\n")));
			output.append("Connected to server\n");
		} catch (Exception e) {
			output.append("ERROR Unable to connect\n");
		}
	}

	private void sendCommand() {
		String cmd = commandField.getText();
		String error = CommandValidator.validate(cmd);

		if (error != null) {
			output.append(error + "\n");
			return;
		}

		connection.send(cmd);
		commandField.setText("");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
	}
}
