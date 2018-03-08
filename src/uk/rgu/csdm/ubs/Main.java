package uk.rgu.csdm.ubs;

import uk.rgu.csdm.ubs.server.Server;
import uk.rgu.csdm.ubs.view.HeatMapFrame;

import javax.swing.*;

public class Main
{
  private static void startListening()
  {
    Server.startListening(4343);
    Server.startListening(4344);
  }

  private static void createAndShowGUI() throws Exception
  {
    HeatMapFrame hmd = new HeatMapFrame();
    hmd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hmd.setSize(1200, 600);
    hmd.setData();
    hmd.setVisible(true);
  }

  public static void main(String[] args)
  {
    startListening();
    Runnable runny = () ->
    {
      try
      {
        createAndShowGUI();
      }
      catch (Exception e)
      {
        System.err.println(e);
        e.printStackTrace();
      }
    };

    SwingUtilities.invokeLater(runny);
  }

}
