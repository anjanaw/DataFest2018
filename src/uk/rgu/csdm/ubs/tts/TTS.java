package uk.rgu.csdm.ubs.tts;

import java.io.IOException;

import com.darkprograms.speech.synthesiser.SynthesiserV2;

import javazoom.jl.decoder.JavaLayerException;
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
    System.out.println(text);

    Thread thread = new Thread(() -> {
      try
      {
        AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));
        player.play();

        System.out.println("Successfully got back synthesizer data");

      }
      catch (IOException | JavaLayerException e)
      {

        e.printStackTrace();

      }
    });

    thread.setDaemon(false);

    thread.start();

  }

  public static void main(String[] args)
  {
    new TTS();
  }

}
