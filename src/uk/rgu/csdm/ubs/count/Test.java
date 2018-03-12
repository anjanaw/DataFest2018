package uk.rgu.csdm.ubs.count;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Test
{
    public static final void main(String[] args)
    {
        BufferedReader br;
        try
        {
            br = new BufferedReader(new FileReader("C:/IdeaProjects/Data/count/step/1445_1.csv"));
            String line;
            List<Double[]> data = new ArrayList<>();
            while ((line = br.readLine()) != null)
            {
                List<Double> item = new ArrayList<>();
                String[] temp = line.split(",");
                for(String t : temp)
                {
                    item.add(Double.parseDouble(t));
                }
                data.add(item.toArray(new Double[0]));
                if(data.size() == 32)
                {
                    PeakRCounter.getInstance().add(data.toArray(new Double[0][]));
                    //Thread.sleep(40);
                    data = new ArrayList<>();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
