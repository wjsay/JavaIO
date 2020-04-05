package asynio;

import java.io.File;
import java.nio.channels.AsynchronousChannel;

public class Server {
    public static void main(String[] args) {
        try {
            File file = new File("周杰伦 - 琴伤.wav");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
