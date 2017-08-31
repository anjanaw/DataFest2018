package uk.rgu.csdm.ubs;

import uk.rgu.csdm.ubs.view.HeatMapFrame;

import javax.swing.*;

public class Main
{

  private static void createAndShowGUI() throws Exception
  {
    HeatMapFrame hmd = new HeatMapFrame();
    hmd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hmd.setSize(1200, 600);
    hmd.setVisible(true);
  }

  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
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
      }
    });
  }

}
