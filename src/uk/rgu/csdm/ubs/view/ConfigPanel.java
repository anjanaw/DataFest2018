package uk.rgu.csdm.ubs.view;

import jssc.SerialPortList;
import uk.rgu.csdm.ubs.count.CountChangeListener;
import uk.rgu.csdm.ubs.count.Processor;
import uk.rgu.csdm.ubs.server.ServerLeft;
import uk.rgu.csdm.ubs.server.ServerRight;
import uk.rgu.csdm.ubs.tts.VoiceListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ConfigPanel extends JPanel implements Constants, CountChangeListener, VoiceListener
{
  private JLabel leftInput;

  private JLabel rightInput;

  private JButton startButton;

  private JButton stopButton;

  private JComboBox leftCombo;

  private JComboBox rightCombo;

  private JLabel count;

  private BufferedImage image;

  private JPanel imagePanel;

  public ConfigPanel()
  {
    super();
    init();
    setData();
  }

  @Override public void countChanged(int count)
  {
    this.count.setText(""+count);
    this.repaint();
  }

  @Override
  public void changed(String voice) {
    if(voice.equals("start"))
    {
      start();
    }
    else if(voice.equals("stop"))
    {
      stop();
    }
  }

  private void init()
  {
    this.setLayout(new GridBagLayout());
    this.setBackground(Color.white);
    this.leftInput = new JLabel("Left input:");
    this.rightInput = new JLabel("Right input:");

    this.leftCombo = new JComboBox();
    this.rightCombo = new JComboBox();

    this.startButton = new JButton("Start");
    ActionListener start = (actionEvent) -> start();
    this.startButton.addActionListener(start);
    this.stopButton = new JButton("Stop");
    ActionListener stop = (actionEvent) -> stop();
    this.stopButton.addActionListener(stop);

    this.count = new JLabel();
    this.count.setFont(new Font("Serif", Font.PLAIN, 100));
    this.count.setText("0");

    this.imagePanel = new JPanel()
    {
      @Override
      protected void paintComponent(Graphics g)
      {
        super.paintComponent(g);
        try
        {
          image = ImageIO.read(new File("images/step.jpg"));
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }

        int x = (this.getSize().width - 110)/2 ;
        g.drawImage(image.getScaledInstance(110, 150, Image.SCALE_DEFAULT), x, 0, null);
      }
    };
    this.imagePanel.setBackground(Color.white);

    this.add(imagePanel,
        new GridBagConstraints(0, 0, 2, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));

    this.add(leftInput,
        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));

    this.add(leftCombo,
        new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));

    this.add(rightInput,
        new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));

    this.add(rightCombo,
        new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));

    this.add(startButton, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    this.add(stopButton, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    this.add(count, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
  }

  private void setData()
  {
    String[] ports = createComboModel(SerialPortList.getPortNames());
    this.leftCombo.setModel(new DefaultComboBoxModel<>(ports));
    this.rightCombo.setModel(new DefaultComboBoxModel<>(ports));
    this.rightCombo.setSelectedIndex(0);
    this.leftCombo.setSelectedIndex(0);
    setStatus(false);
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
      setStatus(true);
      ServerLeft.doProcess = true;
      ServerRight.doProcess = true;
      Processor.getInstance().startProcess();
    }
  }

  private void stop()
  {
    setStatus(false);
    ServerRight.doProcess = false;
    ServerLeft.doProcess = false;
    Processor.getInstance().stopProcess();
  }

  private void setStatus(boolean isOn)
  {
    this.startButton.setEnabled(!isOn);
    this.stopButton.setEnabled(isOn);
  }
}
