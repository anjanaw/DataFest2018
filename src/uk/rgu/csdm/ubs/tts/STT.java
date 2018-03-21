package uk.rgu.csdm.ubs.tts;

import java.io.File;

import com.darkprograms.speech.microphone.MicrophoneAnalyzer;
import com.darkprograms.speech.recognizer.Languages;
import net.sourceforge.javaflacencoder.FLACFileWriter;

import com.darkprograms.speech.recognizer.Recognizer;
import com.darkprograms.speech.recognizer.GoogleResponse;

public class STT
{
  private static STT instance;

  private VoiceListener listener;

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

  public void setVoiceListner(VoiceListener listener)
  {
    this.listener = listener;
  }

  public void listen()
  {
    MicrophoneAnalyzer mic = new MicrophoneAnalyzer(FLACFileWriter.FLAC);
    mic.setAudioFile(new File("../audio.flac"));
    while (true)
    {
      mic.open();
      final int THRESHOLD = 57;
      int volume = mic.getAudioVolume();
      boolean isSpeaking = (volume > THRESHOLD);
      if (isSpeaking)
      {
        //System.out.println("is speaking");
        try
        {
          System.out.println("RECORDING...");
          mic.captureAudioToFile(mic.getAudioFile());
          do
          {
            System.out.println(mic.getAudioVolume());
            Thread.sleep(2000);
          }
          while (mic.getAudioVolume() > THRESHOLD);
          System.out.println("Recording Complete!");
          System.out.println("Recognizing...");
          Recognizer rec = new Recognizer(Languages.ENGLISH_UK, "AIzaSyBeU4Bi2dq4AXYLxYMkk_j3c4BuONTaySQ");
          GoogleResponse response = rec.getRecognizedDataForFlac(mic.getAudioFile(), 1, (int) mic.getAudioFormat().getSampleRate());
          if(response != null && response.getResponse() != null)
          {
            System.out.println(response.getResponse());
            if (response.getResponse().contains("stop")) {
//              System.out.println("stop");
              listener.changed(response.getResponse());
            } else if (response.getResponse().contains("start")) {
//              System.out.println("start");
              listener.changed(response.getResponse());
            }
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

  public static final void main(String[] args)
  {
    double count = 14;
    double seconds = 130;
    double avg = seconds/count;
    System.out.printf("%.1f", avg);
  }
}
