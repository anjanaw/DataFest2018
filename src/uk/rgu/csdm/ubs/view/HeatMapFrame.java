package uk.rgu.csdm.ubs.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HeatMapFrame extends JFrame
{
  private HeatMap heatMap;

  private JMenuBar menuBar;

  private JMenu menu;

  private JMenuItem fullScreen;

  private JMenuItem configure;

  private JMenuItem exit;

  public HeatMapFrame() throws Exception
  {
    super("Pressure Mat");

    this.heatMap = new HeatMap();

    this.menu = new JMenu("Preferences");
    this.fullScreen = new JMenuItem("Enter full screen");
    this.fullScreen.addActionListener(new ActionListener()
    {
      @Override public void actionPerformed(ActionEvent e)
      {
        enterFullScreen();
      }
    });
    this.configure = new JMenuItem("Configuration");
    this.configure.addActionListener(new ActionListener()
    {
      @Override public void actionPerformed(ActionEvent e)
      {
        configure();
      }
    });
    this.exit = new JMenuItem("Exit");
    this.exit.addActionListener(new ActionListener()
    {
      @Override public void actionPerformed(ActionEvent e)
      {
        exit();
      }
    });

    this.menu.add(configure);
    this.menu.add(fullScreen);
    this.menu.add(exit);

    this.menuBar = new JMenuBar();
    this.menuBar.add(menu);

    this.setJMenuBar(menuBar);

    this.getContentPane().add(heatMap, BorderLayout.CENTER);
  }

  private void enterFullScreen()
  {
    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    this.setVisible(true);
  }

  private void configure()
  {
    ConfigDialog instance = ConfigDialog.getInstance();
    instance.setParent(this);
    instance.showDialog();
  }

  private void exit()
  {
    dispose();
  }

  public HeatMap getHeatMap()
  {
    return this.heatMap;
  }
}
