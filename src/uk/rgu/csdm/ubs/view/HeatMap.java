package uk.rgu.csdm.ubs.view;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import uk.rgu.csdm.ubs.data.Processor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class HeatMap extends JPanel implements Constants
{
  private double[][] data;

  private int[][] dataColorIndices;

  private double xMin = 1;

  private double xMax = 32;

  private double yMin = 1;

  private double yMax = 16;

  private Color[] colors;

  private Color bg = Color.white;

  private Color fg = Color.black;

  private BufferedImage bufferedImage;

  private Graphics2D bufferedGraphics;

  private Map<String, String> configData;

  private SerialPort serialPortLeft;

  private SerialPort serialPortRight;

  private Map<String, String> usb1Data = new HashMap();

  public HeatMap()
  {
    super();

    this.configData = new HashMap();

    updateGradient(Gradient.GRADIENT_GREEN_YELLOW_ORANGE_RED);
    updateData(new double[1024][512]);

    this.setPreferredSize(new Dimension(60 + 1024, 60 + 512));
    this.setDoubleBuffered(true);

    this.bg = Color.white;
    this.fg = Color.black;

    drawData();
  }

  /**
   * Updates the gradient used to display the data. Calls drawData() and
   * repaint() when finished.
   * @param colors A variable of type Color[]
   */
  private void updateGradient(Color[] colors)
  {
    this.colors = (Color[]) colors.clone();

    if (data != null)
    {
      updateDataColors();

      drawData();

      repaint();
    }
  }

  /**
   * This uses the current array of colors that make up the gradient, and
   * assigns a color index to each data point, stored in the dataColorIndices
   * array, which is used by the drawData() method to plot the points.
   */
  private void updateDataColors()
  {
    double largest = Double.MIN_VALUE;
    double smallest = Double.MAX_VALUE;
    for (int x = 0; x < data.length; x++)
    {
      for (int y = 0; y < data[0].length; y++)
      {
        largest = Math.max(data[x][y], largest);
        smallest = Math.min(data[x][y], smallest);
      }
    }
    double range = largest - smallest;

    dataColorIndices = new int[data.length][data[0].length];

    for (int x = 0; x < data.length; x++)
    {
      for (int y = 0; y < data[0].length; y++)
      {
        double norm = (data[x][y] - smallest) / range; // 0 < norm < 1
        int colorIndex = (int) Math.floor(norm * (colors.length - 1));
        dataColorIndices[x][y] = colorIndex;
      }
    }
  }

  /**
   * Updates the data display, calls drawData() to do the expensive re-drawing
   * of the data plot, and then calls repaint().
   * @param data The data to display, must be a complete array (non-ragged)
   */
  public void updateData(double[][] data)
  {
    this.data = new double[data.length][data[0].length];
    for (int ix = 0; ix < data.length; ix++)
    {
      for (int iy = 0; iy < data[0].length; iy++)
      {
        this.data[ix][iy] = data[ix][iy];
      }
    }

    updateDataColors();

    drawData();

    repaint();
  }

  /**
   * Creates a BufferedImage of the actual data plot.
   *
   * After doing some profiling, it was discovered that 90% of the drawing
   * time was spend drawing the actual data (not on the axes or tick marks).
   * Since the Graphics2D has a drawImage method that can do scaling, we are
   * using that instead of scaling it ourselves. We only need to draw the
   * data into the bufferedImage on startup, or if the data or gradient
   * changes. This saves us an enormous amount of time. Thanks to
   * Josh Hayes-Sheen (grey@grevian.org) for the suggestion and initial code
   * to use the BufferedImage technique.
   *
   * Since the scaling of the data plot will be handled by the drawImage in
   * paintComponent, we take the easy way out and draw our bufferedImage with
   * 1 pixel per data point. Too bad there isn't a setPixel method in the
   * Graphics2D class, it seems a bit silly to fill a rectangle just to set a
   * single pixel...
   *
   * This function should be called whenever the data or the gradient changes.
   */
  private void drawData()
  {
    bufferedImage = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_ARGB);
    bufferedGraphics = bufferedImage.createGraphics();

    for (int x = 0; x < data.length; x++)
    {
      for (int y = 0; y < data[0].length; y++)
      {
        bufferedGraphics.setColor(colors[dataColorIndices[x][y]]);
        bufferedGraphics.fillRect(x, y, 1, 1);
      }
    }
  }

  /**
   * The overridden painting method, now optimized to simply draw the data
   * plot to the screen, letting the drawImage method do the resizing. This
   * saves an extreme amount of time.
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    int width = this.getWidth();
    int height = this.getHeight();

    this.setOpaque(true);

    g2d.setColor(bg);
    g2d.fillRect(0, 0, width, height);

    if (bufferedImage == null)
    {
      drawData();
    }

    g2d.drawImage(bufferedImage, 31, 31, width - 30, height - 30, 0, 0, bufferedImage.getWidth(),
        bufferedImage.getHeight(), null);

    g2d.setColor(fg);
    g2d.drawRect(30, 30, width - 60, height - 60);

    int numXTicks = (width - 60) / 50;
    int numYTicks = (height - 60) / 50;

    String label = "";
    DecimalFormat df = new DecimalFormat("##.##");

    int yDist = (int) ((height - 60) / (double) numYTicks);
    for (int y = 0; y <= numYTicks; y++)
    {
      g2d.drawLine(26, height - 30 - y * yDist, 30, height - 30 - y * yDist);
      label = df.format(((y / (double) numYTicks) * (yMax - yMin)) + yMin);
      int labelY = height - 30 - y * yDist - 4 * label.length();
      g2d.rotate(Math.PI / 2);
      g2d.drawString(label, labelY, -14);
      g2d.rotate(-Math.PI / 2);
    }

    int xDist = (int) ((width - 60) / (double) numXTicks);
    for (int x = 0; x <= numXTicks; x++)
    {
      g2d.drawLine(30 + x * xDist, height - 30, 30 + x * xDist, height - 26);
      label = df.format(((x / (double) numXTicks) * (xMax - xMin)) + xMin);
      int labelX = (31 + x * xDist) - 4 * label.length();
      g2d.drawString(label, labelX, height - 14);
    }

    g2d.drawRect(width - 20, 30, 10, height - 60);
    for (int y = 0; y < height - 61; y++)
    {
      int yStart = height - 31 - (int) Math.ceil(y * ((height - 60) / (colors.length * 1.0)));
      yStart = height - 31 - y;
      g2d.setColor(colors[(int) ((y / (double) (height - 60)) * (colors.length * 1.0))]);
      g2d.fillRect(width - 19, yStart, 9, 1);
    }
  }

  public Map<String, String> getConfigData()
  {
    return configData;
  }

  public void setConfigData(Map<String, String> configData)
  {
    this.configData = configData;
    if(Boolean.parseBoolean(configData.get(IS_ON)))
    {
      String left = configData.get(LEFT_PORT);
      String right = configData.get(RIGHT_PORT);
      connectSerialPort(left, right);
    }
    else
    {
      disconnectSerialPort();
    }
  }

  public void connectSerialPort(String leftPort, String rightPort)
  {
    try
    {
      serialPortLeft = new SerialPort(leftPort);
      serialPortLeft.openPort();
      serialPortLeft.addEventListener(new LeftPortListener());

      serialPortRight = new SerialPort(rightPort);
      serialPortRight.openPort();
      serialPortRight.addEventListener(new RightPortListener());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void disconnectSerialPort()
  {
    try
    {
      serialPortLeft.removeEventListener();
      serialPortLeft.closePort();

      serialPortRight.removeEventListener();
      serialPortRight.closePort();

      updateData(new double[1024][512]);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private class LeftPortListener implements SerialPortEventListener
  {
    @Override public void serialEvent(SerialPortEvent serialPortEvent)
    {
      try
      {
        byte[] data = serialPortLeft.readBytes();
        StringBuilder sb = new StringBuilder();
        for (byte b : data)
        {
          sb.append(String.format("%02x ", b));
        }
        usb1Data.put(USB1, sb.toString());
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  private class RightPortListener implements SerialPortEventListener
  {
    @Override public void serialEvent(SerialPortEvent serialPortEvent)
    {
      try
      {
        byte[] data = serialPortRight.readBytes();
        StringBuilder sb = new StringBuilder();
        for (byte b : data)
        {
          sb.append(String.format("%02x ", b));
        }
        String usb1CurrentData = usb1Data.get(USB1);
        if (usb1CurrentData == null)
        {
          return;
        }
        updateData(Processor.getInstance().processFrame(usb1CurrentData, sb.toString()));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}
