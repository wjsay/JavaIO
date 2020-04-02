package noblocking;

public class CallbackHandler {
    private Thread read;
    private Thread write;

    public void callback() {
        if (read != null && !read.isInterrupted()) {
            read.interrupt();
            System.out.println("handler: " + "close in stream");
        }
        if (write != null && !write.isInterrupted()) {
            write.interrupt();
            System.out.println("handler: " + "close out stream");
        }
    }

    public void setRead(Thread read) {
        this.read = read;
    }

    public void setWrite(Thread write) {
        this.write = write;
    }
}
