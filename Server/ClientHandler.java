import java.net.*;
import java.io.*;


public class ClientHandler implements Runnable {

    private Socket socket;
    private Board board;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket s, Board b) throws Exception {
        this.socket = s;
        this.board = b;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {

        try {   
            sendServerHandshake();

            String line;

            while ((line = in.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                String response = handleCommand(line);
                out.print(response.endsWith("\n") ? response : response + "\n");
                out.flush();

                if (line.equals("DISCONNECT"))
                    socket.close();
            }

        } catch (Exception e) {
            // NEVER crash server due to client
        } finally {
            try { socket.close(); } catch(Exception ignored){}
        }
    }

    
    private void sendServerHandshake() {

        out.print("BOARD DIMENSIONS: " + board.getBoardDim() + "\n");
        out.print("NOTE DIMENSIONS: " + board.getNoteDim() + "\n");
        out.print("COLORS " + String.join(" ", board.getColors()) + "\n");
        out.print("READY\n");
        out.flush();
    }

    private String handleCommand(String line) {

        try {

            if (line.startsWith("POST "))
                return parsePost(line);

            else if (line.startsWith("PIN "))
                return parsePin(line);

            else if (line.startsWith("UNPIN "))
                return parseUnpin(line);

            else if (line.equals("SHAKE"))
                return board.shake();

            else if (line.equals("CLEAR"))
                return board.clear();

            else if (line.startsWith("GET"))
                return board.get(line);

            else if (line.equals("DISCONNECT"))
                return "SOCKET DISCONNECTED";

            return Board.ErrorCode.INVALID_FORMAT.getMessage()+" "+"Unknown command";

        } catch(Exception e) {
            return Board.ErrorCode.INVALID_FORMAT.getMessage();
        }
    }

    private String parsePost(String line) {

        try {
            // POST x y color message...
            String[] parts = line.split(" ",5);

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            String color = parts[3];
            String msg = parts[4];

            return board.post(x,y,color,msg);

        } catch(Exception e){
            return Board.ErrorCode.INVALID_FORMAT.getMessage()+ " "+"POST requires integer coordinates, color, message, in that order";
        }
    }

    private String parsePin(String line) {
        try {
            String[] p = line.split(" ");
            int x = Integer.parseInt(p[1]);
            int y = Integer.parseInt(p[2]);
            return board.pin(x,y);
        } catch(Exception e){
            return Board.ErrorCode.INVALID_FORMAT.getMessage()+ " "+"PIN requires integer coordinates";
        }
    }

    private String parseUnpin(String line) {
        try {
            String[] p = line.split(" ");
            int x = Integer.parseInt(p[1]);
            int y = Integer.parseInt(p[2]);
            return board.unpin(x,y);
        } catch(Exception e){
            return Board.ErrorCode.INVALID_FORMAT.getMessage()+ " "+"UNPIN requires integer coordinates";
        }
    }
}
