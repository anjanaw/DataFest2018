package uk.rgu.csdm.ubs.view;

import uk.rgu.csdm.ubs.count.CountChangeListener;
import uk.rgu.csdm.ubs.count.Processor;
import uk.rgu.csdm.ubs.server.ServerLeft;
import uk.rgu.csdm.ubs.server.ServerRight;
import uk.rgu.csdm.ubs.tts.TTS;
import uk.rgu.csdm.ubs.tts.VoiceListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class ConfigPanel extends JPanel implements Constants, CountChangeListener, VoiceListener
{
  private JButton startButton;

  private JButton stopButton;

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

    this.count = new JLabel();
    this.count.setFont(new Font("Serif", Font.PLAIN, 110));
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

    this.add(startButton, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    this.add(stopButton, new GridBagConstraints(1, 3, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));

    this.add(count, new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
  }

  private void setData()
  {
    setStatus(false);
  }

  private void start()
  {
    setStatus(true);
    ServerLeft.doProcess = true;
    ServerRight.doProcess = true;
    TTS.getInstance().sayStartingBit();
    Processor.getInstance().startProcess();
  }

  private void stop()
  {
    setStatus(false);
    Processor.getInstance().stopProcess();
    ServerRight.doProcess = false;
    ServerLeft.doProcess = false;
    TTS.getInstance().sayEndingBit();
    Processor.getInstance().clear();
  }

  private void setStatus(boolean isOn)
  {
    this.startButton.setEnabled(!isOn);
    this.stopButton.setEnabled(isOn);
  }
}
