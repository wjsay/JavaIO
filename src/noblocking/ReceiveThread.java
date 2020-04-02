package noblocking;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveThread extends Thread {
    private Socket socket;
    private CallbackHandler handler;
    public ReceiveThread(Socket socket, CallbackHandler handler) {
        this.socket = socket;
        this.handler = handler;
        handler.setRead(this);
    }
    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            while (!socket.isClosed()) {
                if (in.available() > 0) {
                    String msg = in.readUTF();
                    if (msg.equals("exit")) {
                        break;
                    }
                    System.out.println("receive: " + msg);
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
