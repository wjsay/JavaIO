package noblocking;

import java.io.*;
import java.net.Socket;

public class ChatThread implements Runnable {
    private Socket socket;
    public ChatThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try (BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
             DataOutputStream writeToRemote = new DataOutputStream(socket.getOutputStream());
             DataInputStream readFromRemote = new DataInputStream(socket.getInputStream());
            ) {
            // localWrite: System.out
            while(!socket.isClosed()) {
                if (readFromRemote.available() > 0) {
                    String msg = readFromRemote.readUTF();
                    if (msg.equals("exit")) {
                        break; // passive Exit
                    }
                    System.out.println("receive: " + msg);
                }
                if (localReader.ready()) {
                    String msg = localReader.readLine();
                    writeToRemote.writeUTF(msg);
                    if (msg.equals("exit")) {
                        break; // Active exit
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
