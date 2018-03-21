package uk.rgu.csdm.ubs.tts;

import com.darkprograms.speech.synthesiser.SynthesiserV2;
import javazoom.jl.player.advanced.AdvancedPlayer;
import uk.rgu.csdm.ubs.count.Processor;

import javax.sound.sampled.*;
import java.io.*;


public class TTS
{

  private static TTS instance;

  private static final SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyCDBS8S8zogZ6og1ldnHwWI7SmE9jRQ3ac");

  private TTS()
  {
    synthesizer.setLanguage("en-us");
  }

  private static final long HALF_WINDOW = 2000;

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

  private void sayText(final String text)
  {
    try
    {
      InputStream is=synthesizer.getMP3Data(text);
      FileOutputStream outStream=new FileOutputStream("resources/start.mp3");
      int read=0;
      byte []bytes = new byte[8192];
      while((read=is.read(bytes))!=-1){
        outStream.write(bytes,0,read);
      }
      outStream.flush();
      outStream.close();

      /*AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));
      player.play();*/
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void sayStartingBit()
  {
    playAudioFile("resources\\start.mp3");
  }

  private Integer[] getTimes(int seconds)
  {
    Integer[] times = new Integer[3];
    times[0] = seconds/3600;
    int remainder = seconds - times[0] * 3600;
    times[1] = remainder / 60;
    times[2] = remainder - times[1] * 60;
    return times;
  }

  public void sayEndingBit()
  {
    int count = Processor.getInstance().getCount();
    int seconds = Processor.getInstance().getSeconds();
    int avg = seconds/count;
    String next_pace = null;
    String next_pace_text = "";
    if(avg > 4)
    {
      next_pace = "speed up";
    }
    if(avg < 4)
    {
      next_pace = "slow down";
    }
    if(next_pace != null)
    {
      next_pace_text =  "Next time try to "+next_pace+" and maintain the pace to 4 seconds per repetition.";
    }
    String averageText =  "On average you took " + avg + " seconds per repetition. "+next_pace_text;

    Integer[] times = getTimes((int)seconds);
    if(times[0] == 0 && times[1] == 0) {
      final String text = "You have completed " + count + " repetitions in " + times[2] + " seconds. "+averageText;
      speak(text);
    }else if(times[0] == 0) {
      final String text = "You have completed " + count + " repetitions in "+times[1]+" minutes and " + times[2] + " seconds. "+averageText;
      speak(text);
    }else{
      final String text = "You have completed " + count + " repetitions in "+times[0]+" hours, "+times[1]+" minutes and "+ times[2] +" seconds. "+averageText;
      speak(text);
    }
  }

  public void sayHelp()
  {
    Thread t = new Thread(() ->
    {
      int x = 0;
      while(x++ < 3) {
        long start = System.currentTimeMillis();
        playAudioFile("resources\\Step_up.mp3");
        long end = System.currentTimeMillis();
        if(end-start < 2000)
        {
          try {
            Thread.sleep(HALF_WINDOW - (end - start));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        start = System.currentTimeMillis();
        playAudioFile("resources\\Step_down.mp3");
        end = System.currentTimeMillis();
        System.out.println(end-start);
        if(end-start < 2000)
        {
          try {
            Thread.sleep(HALF_WINDOW - (end - start));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });
    t.start();
  }

  private void playAudioFile(String file)
  {
    try
    {
      AdvancedPlayer player = new AdvancedPlayer(new BufferedInputStream(new FileInputStream(new File(file))));
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
