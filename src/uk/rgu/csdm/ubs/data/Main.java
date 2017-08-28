package uk.rgu.csdm.ubs.data;

import uk.rgu.csdm.ubs.heat.HeatMapDemo;

import javax.swing.*;

public class Main
{

  private static void createAndShowGUI() throws Exception
  {
    HeatMapDemo hmd = new HeatMapDemo();
    hmd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hmd.setSize(800, 600);
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
