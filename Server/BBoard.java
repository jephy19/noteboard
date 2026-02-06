import java.net.*;
import java.util.*;

public class BBoard {

    private static volatile boolean running = true;
    public static void main(String[] args) throws Exception {

        if (args.length < 6) {
            System.out.println("Usage: java BBoard <port> 100 200 5 4 [green, red, orange, purple, yellow]");
            return;
        }
        int port = 0;
        port = Integer.parseInt(args[0]);
        int boardW = Integer.parseInt(args[1]);
        int boardH = Integer.parseInt(args[2]);
        int noteW = Integer.parseInt(args[3]);
        int noteH = Integer.parseInt(args[4]);

        List<String> colors = new ArrayList<>();
        
        for (int i = 5; i < args.length; i++)
            colors.add(args[i].toLowerCase());

        Board board = new Board(boardW, boardH, noteW, noteH, colors);

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("BBoard running on port " + port);


            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    Thread thread = new Thread(new ClientHandler(client, board));
                    thread.start();
                }catch (SocketException e) {
                    if (running) {
                        System.err.println("Socket error: " + e.getMessage());
                    }
                }
                
            }
        } catch (BindException e) {
            System.err.println("Error: Port " + port + " is already in use");
            System.exit(1);
        }
    }

    public static void shutdown() {
        running = false;
    }
}
