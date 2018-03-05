package uk.rgu.csdm.ubs.tts;

import javax.sound.sampled.*;
import java.io.File;

import com.darkprograms.speech.microphone.MicrophoneAnalyzer;
import com.darkprograms.speech.recognizer.Languages;
import net.sourceforge.javaflacencoder.FLACFileWriter;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.Recognizer;
import com.darkprograms.speech.recognizer.GoogleResponse;

public class STT
{
  private static STT instance;

  private STT()
  {

  }

  public static STT getInstance()
  {
    if(instance == null)
    {
      instance = new STT();
    }
    return instance;
  }

  public void listen()
  {
    MicrophoneAnalyzer mic = new MicrophoneAnalyzer(FLACFileWriter.FLAC);
    mic.setAudioFile(new File("AudioTestNow.flac"));
    while (true)
    {
      mic.open();
      final int THRESHOLD = 8;
      int volume = mic.getAudioVolume();
      boolean isSpeaking = (volume > THRESHOLD);
      if (isSpeaking)
      {
        try
        {
          System.out.println("RECORDING...");
          mic.captureAudioToFile(mic.getAudioFile());
          do
          {
            Thread.sleep(2000);
          }
          while (mic.getAudioVolume() > THRESHOLD);
          System.out.println("Recording Complete!");
          System.out.println("Recognizing...");
          Recognizer rec = new Recognizer(Languages.ENGLISH_UK, "AIzaSyBeU4Bi2dq4AXYLxYMkk_j3c4BuONTaySQ");
          GoogleResponse response = rec.getRecognizedDataForFlac(mic.getAudioFile(), 1, (int) mic.getAudioFormat().getSampleRate());
          if(response.getResponse().contains("stop"))
          {

          }
          else if (response.getResponse().contains("start"))
          {

          }
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        finally
        {
          mic.close();
        }
      }
    }
  }
}
