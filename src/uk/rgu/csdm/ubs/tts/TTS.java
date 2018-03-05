package uk.rgu.csdm.ubs.tts;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class TTS
{

  private static TTS instance;

  private static SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyBeU4Bi2dq4AXYLxYMkk_j3c4BuONTaySQ");

  private TTS()
  {

  }

  public static TTS getInstance()
  {
    if(instance == null)
    {
      instance = new TTS();
    }
    return instance;
  }

  public void speak(String text)
  {
    Thread thread = new Thread(() -> {
      try
      {
        AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));
        player.play();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    });

    thread.setDaemon(false);

    thread.start();

  }

}
