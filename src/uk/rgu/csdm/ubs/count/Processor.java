package uk.rgu.csdm.ubs.count;

import uk.rgu.csdm.ubs.view.DataReadyListener;

import java.util.*;

public class Processor
{
  private static Processor instance;

  private List<Double[][]> leftData = new ArrayList<>();

  private List<Double[][]> rightData = new ArrayList<>();

  private DataReadyListener listener;

  private Processor()
  {

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

  public synchronized  void add(String data, int port)
  {
    if(port == 4343)
    {
      leftData.add(convert(data));
    }
    else
    {
      rightData.add(convert(data));
    }
  }

  public void startProcessing()
  {
    do {

      Double[][] left = leftData.get(0);
      Double[][] right = rightData.get(0);

      Double[][] newFrame = new Double[left.length * 2][];

      System.arraycopy(left, 0, newFrame, 0, left.length);
      System.arraycopy(right, 0, newFrame, left.length, right.length);

      PeakCounter.getInstance().add(newFrame);
      newFrame = upsample(newFrame, 5);
      listener.ready(newFrame);
      leftData.remove(0);
      rightData.remove(0);
    }
    while(leftData.size() > 0 && rightData.size() > 0);
  }

  public void stopProcessing()
  {
    leftData.clear();
    rightData.clear();
  }

  private Double[][] convert(String input)
  {
    String[] temps = input.split(",");
    List<Double[]> f = new ArrayList<>();
    List<Double> r = new ArrayList<>();
    for(String temp: temps)
    {
      r.add(Double.parseDouble(temp));
      if(r.size() == 16)
      {
        f.add(r.toArray(new Double[16]));
        r = new ArrayList<>();
      }
    }
    return f.toArray(new Double[16][16]);
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
}
