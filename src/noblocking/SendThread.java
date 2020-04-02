package noblocking;

import java.io.*;
import java.net.Socket;

public class SendThread extends Thread {
    private Socket socket;
    private CallbackHandler handler;

    public SendThread(Socket socket, CallbackHandler handler) {
        this.socket = socket;
        this.handler = handler;
        handler.setWrite(this);
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!socket.isClosed()) {
                if (reader.ready()) { // no blocking
                    socket.getOutputStream();
                    String msg = reader.readLine();  // blocking
                    out.writeUTF(msg);
                    if (msg.equals("exit")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!socket.isClosed()) {
                try {
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            handler.callback();
        }
    }
}
