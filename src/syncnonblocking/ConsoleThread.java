package syncnonblocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ConsoleThread extends Thread {
    private SocketChannel socketChannel;
    public ConsoleThread(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));) {
            while (!isInterrupted()) {
                if (reader.ready()) {
                    String msg = reader.readLine();
                    ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());
                    socketChannel.write(buf);
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("ConsoleThread is over.");
    }
}

