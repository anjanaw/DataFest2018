package uk.rgu.csdm.ubs.view;

import uk.rgu.csdm.ubs.data.CountChangeListener;
import uk.rgu.csdm.ubs.data.Counter;
import uk.rgu.csdm.ubs.data.Processor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    this.configPanel.setData(this);
    this.heatMap.setConfigData(null);
    Counter.getInstance().setListener(this.configPanel);
  }

  public HeatMap getHeatMap()
  {
    return this.heatMap;
  }
}
