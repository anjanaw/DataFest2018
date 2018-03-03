package uk.rgu.csdm.ubs.tts;

import javax.sound.sampled.*;
import java.io.File;

import com.darkprograms.speech.recognizer.Languages;
import net.sourceforge.javaflacencoder.FLACFileWriter;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.Recognizer;
import com.darkprograms.speech.recognizer.GoogleResponse;

/**
 * Jarvis Speech API Tutorial
 * @author Aaron Gokaslan (Skylion)
 *
 */
public class STT
{

  public static void main(String[] args)
  {

    AudioFileFormat.Type[] typeArray = AudioSystem.getAudioFileTypes();
    for (AudioFileFormat.Type type : typeArray)
    {
      System.out.println("type: " + type.toString());
    }

    Microphone mic = new Microphone(FLACFileWriter.FLAC);
    File file = new File("/Users/anjana/IdeaProjects/PressureMat/testfile2.flac");
    try
    {
      mic.captureAudioToFile(file);
    }
    catch (Exception ex)
    {
      System.out.println("ERROR: Microphone is not availible.");
      ex.printStackTrace();
    }

    try
    {
      System.out.println("Recording...");
      Thread.sleep(5000);
      mic.close();
    }
    catch (InterruptedException ex)
    {
      ex.printStackTrace();
    }

    mic.close();
    System.out.println("Recording stopped.");

    Recognizer recognizer = new Recognizer(Languages.ENGLISH_UK, "AIzaSyBeU4Bi2dq4AXYLxYMkk_j3c4BuONTaySQ");
    try
    {
      int maxNumOfResponses = 1;
      System.out.println("Sample rate is: " + (int) mic.getAudioFormat().getSampleRate());
      GoogleResponse response = recognizer
          .getRecognizedDataForFlac(file, maxNumOfResponses, (int) mic.getAudioFormat().getSampleRate());
      System.out.println("Google Response: " + response.getResponse());
      System.out
          .println("Google is " + Double.parseDouble(response.getConfidence()) * 100 + "% confident in" + " the reply");
      System.out.println("Other Possible responses are: ");
      for (String s : response.getOtherPossibleResponses())
      {
        System.out.println("\t" + s);
      }
    }
    catch (Exception ex)
    {
      System.out.println("ERROR: Google cannot be contacted");
      ex.printStackTrace();
    }

    file.deleteOnExit();
  }
}
