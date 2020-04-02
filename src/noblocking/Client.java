package noblocking;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("192.168.2.141", 13579);
            new Thread(new ChatThread(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
