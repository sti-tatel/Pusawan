  package Pusawan;
  
  import javax.sound.sampled.*;
  
  public class AudioPlayer {
  
      private static Clip clip;
      private static String currentTrack = "";


public static void playMusic(String filename) {
        if (currentTrack.equals(filename) && clip != null && clip.isRunning()) return;
        playMusicNow(filename);
    }

        private static void playMusicNow(String filename) {
            stopMusic();
            currentTrack = filename;
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    AudioPlayer.class.getResource("/audio/" + filename));
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public static int getVolume() {
        if (clip == null) return 100;
        FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = fc.getValue();
        return (int)((dB - fc.getMinimum()) / (fc.getMaximum() - fc.getMinimum()) * 100);
    }

    public static void setVolume(int percent) {
        if (clip == null) return;
        FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = fc.getMinimum() + (fc.getMaximum() - fc.getMinimum()) * percent / 100f;
        fc.setValue(dB);
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
