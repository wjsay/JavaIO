package syncnonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private static final int BUF_SIZE = 256;
    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open();){
            serverSocketChannel.configureBlocking(false); // non blocking
            serverSocketChannel.socket().bind(new InetSocketAddress(13579));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while(selector.select() > 0) {  // blocking method. when a client request for accept, selector.selector() > 1 is true
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    switch (key.readyOps()) {
                        case SelectionKey.OP_ACCEPT:  // accept from connection
                            ServerSocketChannel clientChannel = (ServerSocketChannel) key.channel();
                            SocketChannel socketChannel = clientChannel.accept();
                            socketChannel.configureBlocking(false);
                            // 如果注册时添加了ByteBuffer，只要buf不空，OP_READ会被一直调用
                            ConsoleThread thread = new ConsoleThread(socketChannel);
                            thread.start();
                            socketChannel.register(selector, SelectionKey.OP_READ, thread);
                            break;
                        case SelectionKey.OP_READ:  // read from a channel
                            ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            byte[] tmp = new byte[buffer.limit() - buffer.position()];
                            buffer.get(tmp);
                            // buffer.clear();
                            String msg = new String(tmp);
                            if (msg.equals("exit")) {
                                channel.write(ByteBuffer.wrap("exit".getBytes()));
                                ((ConsoleThread)key.attachment()).interrupt();
                                key.cancel();
                            } else {
                                System.out.println(msg);
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
