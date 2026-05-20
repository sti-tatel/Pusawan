  package Pusawan;
  
  import javax.sound.sampled.*;
  
  public class AudioPlayer {
  
      private static Clip clip;
      private static String currentTrack = "";

        public static void playMusic(String filename) {
            stopMusic();
            currentTrack = filename;
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    AudioPlayer.class.getResource("/audio/" + filename));
  
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

        public static boolean isPlaying(String filename) {
            return clip != null && clip.isRunning() && currentTrack.equals(filename);
        }


    public static void playSound(String filename) {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    AudioPlayer.class.getResource("/audio/" + filename));
                
                // We use a local Clip variable here so it doesn't overwrite the music clip!
                Clip sfxClip = AudioSystem.getClip();
                sfxClip.open(audioStream);
                sfxClip.start(); // Play once, no looping
                
            } catch (Exception e) {
                System.err.println("Could not play sound: " + filename);
                e.printStackTrace();
            }
        }
  }
