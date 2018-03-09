package uk.rgu.csdm.ubs;

import uk.rgu.csdm.ubs.server.ServerLeft;
import uk.rgu.csdm.ubs.server.ServerRight;
import uk.rgu.csdm.ubs.view.HeatMapFrame;

import javax.swing.*;

public class Main
{
  private static void startListening()
  {
    ServerRight.startListening();
    ServerLeft.startListening();
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
        e.printStackTrace();
      }
    };

    SwingUtilities.invokeLater(runny);
  }

}
