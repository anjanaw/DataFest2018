package uk.rgu.csdm.ubs.count;

import uk.rgu.csdm.ubs.view.DataReadyListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Processor
{
  private static Processor instance;

  private Double[][] leftData = null;

  private Double[][] rightData = null;

  private DataReadyListener listener;

  private static final long WINDOW = 67;

  private Counter counter = PeakRCounter.getInstance();

  private AtomicBoolean isProcess = new AtomicBoolean(false);

  private long startTT = 0;

  private long endTT = 0;

  private static final String FILE = "C:/Shared/PressureMat/resources/";

  private String currentFile = "";

  private Processor()
  {
    Thread t = new Thread(() -> process());
    t.start();
  }

  public static Processor getInstance()
  {
    if (instance == null)
    {
      instance = new Processor();
    }
    return instance;
  }

  public void setDataReadyListener(DataReadyListener readyListener)
  {
    this.listener = readyListener;
  }

  public  void addLeft(String data)
  {
    leftData = convert(data);
  }
  public  void addRight(String data)
  {
    rightData = convert(data);
  }

  public void startProcess()
  {
    startTT = System.currentTimeMillis();
    isProcess.set(true);
    currentFile = FILE+startTT+".csv";
  }

  private void process()
  {
    while(true) {
      if(isProcess.get()) {
        long start = System.currentTimeMillis();
        Double[][] newFrame = new Double[leftData.length * 2][];

        System.arraycopy(leftData, 0, newFrame, 0, leftData.length);
        System.arraycopy(rightData, 0, newFrame, leftData.length, rightData.length);

        counter.add(newFrame);
        write(newFrame);
        listener.ready(newFrame);
        long end = System.currentTimeMillis();
        try {
          Thread.sleep(WINDOW - (end - start));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      else
      {
        long start = System.currentTimeMillis();
        listener.ready(empty(16, 32));
        long end = System.currentTimeMillis();
        try {
          Thread.sleep(WINDOW - (end - start));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void stopProcess()
  {
    isProcess.set(false);
    endTT = System.currentTimeMillis();
  }

  private void write(final Double[][] frame) {
    Thread t = new Thread(() -> {
      try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile, true));
        for (Double[] line : frame) {
          StringBuilder s = new StringBuilder();
          for (Double temp : line) {
            s.append(temp.doubleValue());
            s.append(",");
          }
          writer.append(s.toString().substring(0, s.length() - 1));
          writer.append("\n");
        }
        writer.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    t.start();
  }
  public void clear()
  {
    this.leftData = null;
    this.rightData = null;
    counter.clear();
  }

  private Double[][] convert(String input)
  {
    String[] temps = input.split(",");
    List<Double[]> f = new ArrayList<>();
    List<Double> r = new ArrayList<>();
    double max = 0;
    for(String temp: temps)
    {
      double dd = Double.parseDouble(temp);
      if(max < dd)
      {
        max = dd;
      }
      r.add(dd);
      if(r.size() == 16)
      {
        f.add(r.toArray(new Double[16]));
        r = new ArrayList<>();
      }
    }
    if(max <= 200)
    {
      return empty(16,16);
    }
    return f.toArray(new Double[16][16]);
  }

  private Double[][] empty(int rows, int columns)
  {
    Double[][] f = new Double[columns][rows];
    for(int i=0; i<columns; i++)
    {
      for(int j=0; j<rows; j++)
      {
        f[i][j] = 0.0;
      }
    }
    return f;
  }

  public Double[][] upsample(Double[][] matrix, int times)
  {
    for (int k = 0; k < times; k++)
    {
      int newi = matrix.length * 2;
      int newj = matrix[0].length * 2;
      Double[][] upsampledMatrix = new Double[newi][newj];

      for (int i = 0; i < newi; i++)
      {
        for (int j = 0; j < newj; j++)
        {
          if (i % 2 == 0 && j % 2 == 0)
          {
            upsampledMatrix[i][j] = matrix[i / 2][j / 2];
          }
        }
      }

      for (int i = 0; i < newi; i++)
      {
        for (int j = 0; j < newj; j++)
        {
          if (j % 2 == 1 && i % 2 == 0)
          {
            if (j < newj - 1)
            {
              upsampledMatrix[i][j] = (upsampledMatrix[i][j - 1] + upsampledMatrix[i][j + 1]) / 2;
            }
            else if (j == newj - 1)
            {
              upsampledMatrix[i][j] = upsampledMatrix[i][j - 1] / 2;
            }
          }
          if (i % 2 == 1 && j % 2 == 0)
          {
            if (i < newi - 1)
            {
              upsampledMatrix[i][j] = (upsampledMatrix[i - 1][j] + upsampledMatrix[i + 1][j]) / 2;
            }
            else if (i == newi - 1)
            {
              upsampledMatrix[i][j] = upsampledMatrix[i - 1][j] / 2;
            }
          }
        }
      }

      for (int i = 0; i < newi; i++)
      {
        for (int j = 0; j < newj; j++)
        {
          if (j % 2 == 1 && i % 2 == 1)
          {
            if (j < newj - 1)
            {
              upsampledMatrix[i][j] = (upsampledMatrix[i][j - 1] + upsampledMatrix[i][j + 1]) / 2;
            }
            else if (j == newj - 1)
            {
              upsampledMatrix[i][j] = upsampledMatrix[i][j - 1] / 2;
            }
          }
        }
      }
      matrix = upsampledMatrix;
    }
    return matrix;
  }

  private double[][] rotate(double[][] matrix)
  {
    int n = matrix.length;
    double[][] newMatrix = new double[matrix[0].length][matrix.length];
    for (int i = 0; i < matrix.length; i++)
    {
      for (int j = 0; j < matrix[0].length; j++)
      {
        newMatrix[i][j] = matrix[n - j - 1][i];
      }
    }
    return newMatrix;
  }

  public int getCount()
  {
    return counter.getCount();
  }

  public int getSeconds()
  {
    return (int)((endTT - startTT)/1000);
  }
}
