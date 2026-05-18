  package Pusawan;
  
  import javax.sound.sampled.*;
  
  public class AudioPlayer {
  
      private static Clip clip;
  
      public static void playMusic() {
          try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                AudioPlayer.class.getResource("/audio/morningMood.wav"));
  
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
