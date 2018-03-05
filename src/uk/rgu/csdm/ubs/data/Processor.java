package uk.rgu.csdm.ubs.data;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Processor
{
  private static Processor instance;

  private static final String START_FRAME = "48 00 0A ";

  private static final String START_ROW = "4D ";

  private static final String END_ROW = "0A ";

  private static final String EMPTY = "";

  private static final String[] ROWS = { "10 00 ", "10 01 ", "10 02 ", "10 03 ", "10 04 ", "10 05 ", "10 06 ", "10 07 ",
      "10 08 ", "10 09 ", "10 0A ", "10 0B ", "10 0C ", "10 0D ", "10 0E ", "10 0F " };

  private static final String SPACE = " ";

  private static final String FILE = "/Users/anjana/IdeaProjects/PressureMat/1210.csv";

  private static final double THRESHOLD = 4095;

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

  public double[][] processFrame()
  {
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(FILE));
      String str;
      double[][] frame = new double[32][];
      int j=0;
      while ((str = br.readLine()) != null)
      {
        String[] strr = str.split(",");
        double[] line = new double[16];
        int i=0;
        for(String temp : strr)
        {
          line[i++] = Double.parseDouble(temp);
        }
        frame[j++] = line;
        if(j == 32)
        {
          frame = upsample(frame, 5);
          System.out.println(frame[0].length);
          System.out.println(frame.length);
          return frame;
        }
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }

  public double[][] processFrame(String frameString1, String frameString2)
  {
    double[][] frame1 = processFrame(frameString1);
    double[][] frame2 = processFrame(frameString2);

    double[][] newFrame = new double[frame1.length * 2][];

    System.arraycopy(frame1, 0, newFrame, 0, frame1.length);
    System.arraycopy(frame2, 0, newFrame, frame1.length, frame2.length);

    Counter.getInstance().add(convert(newFrame));
    newFrame = upsample(newFrame, 5);
    return newFrame;
  }

  private Double[][] convert(double[][] input)
  {
    List<Double[]> output = new ArrayList<>();
    for(double[] dd : input)
    {
      List<Double> o = new ArrayList<>();
      for(double d:dd)
      {
        o.add(d);
      }
      output.add(o.toArray(new Double[0]));
    }
    return output.toArray(new Double[0][]);
  }

  /*private void writeFile(double[][] frame)
  {
    try
    {
      BufferedWriter writer = new BufferedWriter(new FileWriter(FILE, true));
      for(double[] line : frame)
      {
        StringBuilder s = new StringBuilder();
        for(double temp : line)
        {
          s.append(temp);
          s.append(",");
        }
        writer.append(s.toString().substring(0, s.length()-1));
        writer.append("\n");
      }
      writer.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }*/

  public double[][] processFrame(String frameString)
  {
    double[][] returnData = new double[16][16];
    if (frameString != null && frameString.length() == 1737)
    {
      frameString = frameString.toUpperCase().replace(START_FRAME, EMPTY);
      String[] splitFrom4D = frameString.split(START_ROW);
      if (splitFrom4D.length == 17)
      {
        splitFrom4D = Arrays.copyOfRange(splitFrom4D, 1, 17);

        for (int i = 0; i < splitFrom4D.length; i++)
        {
          returnData[i] = processRow(splitFrom4D[i].replace(ROWS[i], EMPTY).replace(END_ROW, EMPTY));
        }

      }
    }
    return rotate(returnData);
  }

  public double[] processRow(String rowString)
  {
    double[] row = new double[16];
    if (rowString.length() == 96)
    {
      String[] splitFromSpace = rowString.split(SPACE, -1);
      if (splitFromSpace.length == 33)
      {
        splitFromSpace = Arrays.copyOfRange(splitFromSpace, 0, 32);
        int column = 0;
        for (int i = 1; i < splitFromSpace.length; i += 2)
        {
          String value = splitFromSpace[i] + splitFromSpace[i - 1];
          row[column++] = THRESHOLD - Long.parseLong(value, 16);
        }
      }
    }
    return row;
  }

  public double[][] upsample(double[][] matrix, int times)
  {
    for (int k = 0; k < times; k++)
    {
      int newi = matrix.length * 2;
      int newj = matrix[0].length * 2;
      double[][] upsampledMatrix = new double[newi][newj];

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
