package noblocking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(13579)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(()->{
                    System.out.println(socket.getInetAddress().getHostName() + " on line!");
                    new ChatThread(socket).run();
                    System.out.println(socket.getInetAddress().getHostName() + " off line!");
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
