package signaldriven;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class SignalDrivenIOTest {
    public static void main(String[] args) {
        SignalDrivenIOTest signalDrivenIOTest = new SignalDrivenIOTest();
        signalDrivenIOTest.run();
    }

    private String localSong = "华晨宇 - 好想爱这个世界啊.wav";
    private String remoteDownloadLink = "https://github.com/zzuwenjie/ucaslife/raw/master/sources/%E5%91%A8%E6%9D%B0%E4%BC%A6%20-%20%E7%90%B4%E4%BC%A4.wav";
    private String storePath = "周杰伦 - 琴伤.wav";
    private String SIGIO = "INT";
    private Clip clip;
    private Clip preferClip;
    public void run() {
        new Thread(new WaitForDataThread(remoteDownloadLink, storePath)).start(); // 我不知道怎么让内核准备数据，故下载一首歌
        Signal.handle(new Signal(SIGIO), signalHandler); // 下载好了通知我执行handler
        File file = new File(localSong);
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
            preferClip = AudioSystem.getClip();
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private SignalHandler signalHandler = (signal)->{
        if (signal.getName().equals(SIGIO)) {
            handleSIGIO();
        }
    };

    private void handleSIGIO() {
        File file = new File(storePath);
        long pausePosition = 0;
        if (!file.exists())
            return; // 不应该没准备好数据
        if (clip != null) {
            pausePosition = clip.getMicrosecondPosition();
            clip.stop();
        }
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
            preferClip.open(inputStream);
            preferClip.start();
            JOptionPane.showMessageDialog(null, "应用进程复制数据中。点击复制完毕，返回成功指示");
            preferClip.close();
           // 假设将数据拷到内核用户空间，阻塞主线程。
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        clip.setMicrosecondPosition(pausePosition);
        clip.start();
        JOptionPane.showMessageDialog(null, "接着听音乐中...不如学习会儿？");
        clip.close();
    }
}