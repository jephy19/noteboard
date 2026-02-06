package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientConnection {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public void connect(String host, int port, Consumer<String> onMessage) throws IOException {

		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

		Thread listener = new Thread(() -> {
			try {
				String line;
				while ((line = in.readLine()) != null) {
					onMessage.accept(line);
				}
			} catch (IOException e) {
				onMessage.accept("Disconnected from server");
			}
		});

		listener.setDaemon(true);
		listener.start();
	}

	public void send(String command) {
		if (out != null) {
			out.println(command);
		}
	}

	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException ignored) {
		}
	}
}
