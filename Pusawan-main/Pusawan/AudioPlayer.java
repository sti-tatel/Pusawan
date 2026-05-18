  package Pusawan;
  
  import javax.sound.sampled.*;
  import java.io.File;
  
  public class AudioPlayer {
  
      private static Clip clip;
  
      public static void playMusic() {
          try {
              File audioFile = new File("Pusawan-main/Pusawan/class/audio/bgmusic.wav");
  
              AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
  
              clip = AudioSystem.getClip();
              clip.open(audioStream);
  
              // Loop forever
              clip.loop(Clip.LOOP_CONTINUOUSLY);
  
              // Start music
              clip.start();
  
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  
      public static void stopMusic() {
          if (clip != null) {
              clip.stop();
          }
      }
  }
