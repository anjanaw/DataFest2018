package uk.rgu.csdm.ubs.view;

import jssc.SerialPortList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ConfigDialog extends JDialog implements Constants
{
  private static ConfigDialog instance;

  private Map<String, String> configData;

  private JLabel leftInput;

  private JLabel rightInput;

  private JButton startButton;

  private JButton stopButton;

  private JComboBox leftCombo;

  private JComboBox rightCombo;

  private JPanel contentPanel;

  private HeatMapFrame parent;

  private ConfigDialog()
  {
    super();
    init();
  }

  public static ConfigDialog getInstance()
  {
    if (instance == null)
    {
      instance = new ConfigDialog();
    }
    return instance;
  }

  private void init()
  {
    this.getContentPane().setLayout(new GridBagLayout());
    this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    this.setModal(true);
    this.contentPanel = new JPanel();
    this.contentPanel.setLayout(new GridBagLayout());

    this.getContentPane().add(contentPanel,
        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    this.leftInput = new JLabel("Left input:");
    this.rightInput = new JLabel("Right input:");

    this.leftCombo = new JComboBox();
    this.rightCombo = new JComboBox();

    this.startButton = new JButton("Start");
    this.startButton.addActionListener(new ActionListener()
    {
      @Override public void actionPerformed(ActionEvent e)
      {
        start();
      }
    });
    this.stopButton = new JButton("Stop");
    this.stopButton.addActionListener(new ActionListener()
    {
      @Override public void actionPerformed(ActionEvent e)
      {
        stop();
      }
    });

    this.contentPanel.add(leftInput,
        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    this.contentPanel.add(leftCombo,
        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    this.contentPanel.add(rightInput,
        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    this.contentPanel.add(rightCombo,
        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    this.contentPanel.add(startButton,
        new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    this.contentPanel.add(stopButton,
        new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));

    update();
  }

  private void update()
  {
    String[] ports = createComboModel(SerialPortList.getPortNames());
    this.leftCombo.setModel(new DefaultComboBoxModel<String>(ports));
    this.rightCombo.setModel(new DefaultComboBoxModel<String>(ports));

    if (configData != null)
    {
      boolean isoff = !Boolean.parseBoolean(configData.get(IS_ON));
      this.leftCombo.setSelectedItem(configData.get(LEFT_PORT));
      this.rightCombo.setSelectedItem(configData.get(RIGHT_PORT));
      this.leftCombo.setEnabled(isoff);
      this.rightCombo.setEnabled(isoff);
      this.startButton.setEnabled(isoff);
      this.stopButton.setEnabled(!isoff);
    }
    else
    {
      this.rightCombo.setSelectedIndex(0);
      this.leftCombo.setSelectedIndex(0);
      this.stopButton.setEnabled(false);
      this.startButton.setEnabled(true);
    }
  }

  public void showDialog()
  {
    update();
    this.setPreferredSize(new Dimension(400, 150));
    this.setMaximumSize(new Dimension(400, 150));
    this.setMinimumSize(new Dimension(400, 150));
    this.setResizable(false);
    this.setLocationRelativeTo(parent);
    this.setVisible(true);
  }

  private String[] createComboModel(String[] portnames)
  {
    List<String> list = new ArrayList();
    list.add(SELECT);
    list.addAll(Arrays.asList(portnames));

    return list.toArray(new String[list.size()]);
  }

  private void start()
  {
    if (this.leftCombo.getSelectedIndex() == 0 || this.rightCombo.getSelectedIndex() == 0)
    {
      JOptionPane.showMessageDialog(this, "Please select all inputs!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    else
    {
      this.configData.put(LEFT_PORT, (String) leftCombo.getSelectedItem());
      this.configData.put(RIGHT_PORT, (String) rightCombo.getSelectedItem());
      this.configData.put(IS_ON, Boolean.toString(true));
      this.parent.getHeatMap().setConfigData(this.configData);
      this.setVisible(false);
    }
  }

  private void stop()
  {
    this.configData.put(IS_ON, Boolean.toString(false));
    this.parent.getHeatMap().setConfigData(this.configData);
    this.setVisible(false);
  }

  public Map<String, String> getConfigData()
  {
    return configData;
  }

  public void setParent(HeatMapFrame frame)
  {
    this.parent = frame;
    this.configData = frame.getHeatMap().getConfigData();
  }
}
