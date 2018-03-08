package uk.rgu.csdm.ubs.view;

import uk.rgu.csdm.ubs.count.PeakRCounter;
import uk.rgu.csdm.ubs.tts.STT;

import javax.swing.*;
import java.awt.*;

public class HeatMapFrame extends JFrame
{
  private HeatMap heatMap;

  private ConfigPanel configPanel;

  public HeatMapFrame() throws Exception
  {
    super("Pressure Mat");
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.heatMap = new HeatMap();

    this.getContentPane().setLayout(new GridBagLayout());
    this.getContentPane().add(heatMap, new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    this.configPanel = new ConfigPanel();
    this.getContentPane().add(configPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
  }

  public void setData()
  {
    PeakRCounter.getInstance().setListener(this.configPanel);
    STT.getInstance().setVoiceListner(this.configPanel);
    /*Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        STT.getInstance().listen();
      }
    });
    t.start();*/

  }
}
