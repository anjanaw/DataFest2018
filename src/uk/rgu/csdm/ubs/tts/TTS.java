package uk.rgu.csdm.ubs.tts;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import javazoom.jl.player.advanced.AdvancedPlayer;
import uk.rgu.csdm.ubs.count.Processor;

public class TTS
{

  private static TTS instance;

  private static SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyBeU4Bi2dq4AXYLxYMkk_j3c4BuONTaySQ");

  private TTS()
  {
    synthesizer.setLanguage("en-us");
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
    Thread thread = new Thread(() -> sayText(text));
    thread.start();
  }

  public void sayStartingBit()
  {
    String text = "Please step on the mat and start exercise following the count down. Ready. . . Three. . . Two. . . One. . . Go. . .";
    sayText(text);
  }

  public void sayEndingBit()
  {
    int count = Processor.getInstance().getCount();
    int seconds = Processor.getInstance().getSeconds();
    int avg = (int)(seconds/count);
    final String text = "You have completed "+count+" repetitions in "+seconds+" seconds. On average you took "+avg+" seconds per repetition.";
    Thread thread = new Thread(() -> sayText(text));
    thread.start();
  }

  private void sayText(final String text)
  {
    try
    {
      AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));
      player.play();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static final void main(String[] args)
  {
    TTS.getInstance().sayStartingBit();
  }

}
