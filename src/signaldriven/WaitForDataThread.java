package signaldriven;

import sun.misc.Signal;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WaitForDataThread implements Runnable {
    private String urlString;
    private String filename;
    public WaitForDataThread(String url, String filename) {
        urlString = url;
        this.filename = filename;
    }

    @Override
    public void run() {
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(1000 * 3);
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename));
//            byte[] buf = new byte[1024 * 20];
//            int length = -1;
//            while ((length = bis.read(buf)) != -1) {
//                bos.write(buf, 0, length);
//            }
//            bis.close();
//            bos.close();
//            conn.disconnect();
//            Signal.raise(new Signal("INT"));  // 下载完时发送INT信号
//        } catch (IOException e){
//            e.printStackTrace();
//        }
        //github下载数据太慢了，几乎连接不上
        JOptionPane.showMessageDialog(null, "内核准备数据，应用进程播放歌曲。点击发送信号通知应用进程准备好了");
        Signal.raise(new Signal("INT"));  // 下载完时发送INT信号
    }
}
