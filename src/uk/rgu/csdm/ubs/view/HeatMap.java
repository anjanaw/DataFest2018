package uk.rgu.csdm.ubs.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.*;

public class HeatMap extends JPanel implements Constants, DataReadyListener
{
  private Double[][] data;

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

  public HeatMap()
  {
    super();

    updateGradient(Gradient.GRADIENT_GREEN_YELLOW_ORANGE_RED);
    updateData(empty());

    this.setPreferredSize(new Dimension(60 + 1024, 60 + 512));
    this.setDoubleBuffered(true);

    this.bg = Color.white;
    this.fg = Color.black;

    drawData();
  }

  private Double[][] empty()
  {
    Double[][] f = new Double[1024][512];
    for(int i=0; i<1024; i++)
    {
      for(int j=0; j<512; j++)
      {
        f[i][j] = 0.0;
      }
    }
    return f;
  }

  /**
   * Updates the gradient used to display the count. Calls drawData() and
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
   * assigns a color index to each count point, stored in the dataColorIndices
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

  @Override
  public void ready(final Double[][] frame)
  {
    Runnable r = () -> updateData(frame);
    SwingUtilities.invokeLater(r);
  }

  /**
   * Updates the count display, calls drawData() to do the expensive re-drawing
   * of the count plot, and then calls repaint().
   * @param data The count to display, must be a complete array (non-ragged)
   */
  public void updateData(Double[][] data)
  {
    this.data = new Double[data.length][data[0].length];
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
   * Creates a BufferedImage of the actual count plot.
   *
   * After doing some profiling, it was discovered that 90% of the drawing
   * time was spend drawing the actual count (not on the axes or tick marks).
   * Since the Graphics2D has a drawImage method that can do scaling, we are
   * using that instead of scaling it ourselves. We only need to draw the
   * count into the bufferedImage on startup, or if the count or gradient
   * changes. This saves us an enormous amount of time. Thanks to
   * Josh Hayes-Sheen (grey@grevian.org) for the suggestion and initial code
   * to use the BufferedImage technique.
   *
   * Since the scaling of the count plot will be handled by the drawImage in
   * paintComponent, we take the easy way out and draw our bufferedImage with
   * 1 pixel per count point. Too bad there isn't a setPixel method in the
   * Graphics2D class, it seems a bit silly to fill a rectangle just to set a
   * single pixel...
   *
   * This function should be called whenever the count or the gradient changes.
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
   * The overridden painting method, now optimized to simply draw the count
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
}
