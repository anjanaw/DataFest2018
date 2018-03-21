package uk.rgu.csdm.ubs.count;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestPeakCounter implements Counter {
    private static TestPeakCounter instance;

    private List<Double[][]> queue = new LinkedList<>();
    private int count = 0;
    private int incoming_count = 0;

    private static final int WINDOW = 60;
    private static final int SMOOTHING_WINDOW = 10;

    private List<Double> diff_queue = new ArrayList<>();

    private double match_diff;

    private static final double MIN = 0.0;
    private static final double MAX = 2096640.0;

    private CountChangeListener listener;

    private int first_three = 0;

    private static final double[] n_template_1 = {0.00982458380295,0.00929931113224,0.00896757155772,0.00901889910303,0.00898756692026,0.00897197420635,0.00876534323049,
            0.00824003387104,0.00755898081619,0.00698223384521,0.00621136675824,0.00531546445008,0.00446468929041,0.00337767534282,0.00228108563445,0.00139017299474,
            0.00066435967878,0.00044532790927,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.00118288162158,0.00224223226026,
            0.00340159640039,0.00431360506481,0.00539775729079,0.00581322056683,0.00598382320137,0.00615174755800,0.00631780078895,0.00654666514042,0.00662943493472,
            0.00671990936414,0.00671741452991,0.00679431412604,0.00709978456373,0.00762116822814,0.00834602763689,0.00840417928759,0.00711706495961,0.00672299121818,
            0.00484926064854};

    public static TestPeakCounter getInstance()
    {
        if (instance == null) {
            instance = new TestPeakCounter();
        }
        return instance;
    }

    private TestPeakCounter()
    {
        for(int i=0; i<30; i++)
        {
            queue.add(empty());
        }
    }

    public void setListener(CountChangeListener listener)
    {
        this.listener = listener;
    }

    private Double[][] empty()
    {
        Double[][] empty = new Double[32][];
        for(int i=0; i<32; i++)
        {
            Double[] d = new Double[16];
            for(int j=0; j<16; j++)
            {
                d[j] = .0;
            }
            empty[i] = d;
        }
        return empty;
    }

    public void add(Double[][] incoming)
    {
        queue.add(incoming);
        incoming_count++;
        if(queue.size() == 60)
        {
            count();
        }
    }

    private List<Double> normalise(List<Double> input)
    {
        List<Double> n = new ArrayList<>();
        for(Double dd : input)
        {
            n.add((dd - MIN) / (MAX-MIN));
        }
        return n;
    }

    private double getManhatten(double frame, double template)
    {
        return Math.abs(frame - template);
    }

    private double getEuclidean(double frame, double template, double power)
    {
        return Math.pow(frame - template, power);
    }

    private boolean noDuplicates(List<Double> subset, double min)
    {
        for(Double temp : subset.subList((subset.size()/2)+1, subset.size()))
        {
            if(temp.doubleValue() == min)
                return false;
        }
        return true;
    }

    private void count()
    {
        List<Double> sums = new ArrayList<>();

        for(Double[][] elem : queue)
        {
            double _sum = 0.0;
            for (Double[] ele : elem)
            {
                for (Double el : ele)
                {
                    _sum += el;
                }
            }
            sums.add(_sum);
        }

        List<Double> n_sums = normalise(sums);

        List<Double> diff_list = new ArrayList<>();
        double diff_sum = 0;
        for (int i = 0; i < n_sums.size(); i++)
        {
            //double diff = getManhatten(n_sums.get(i), n_template[i]);
            double diff = getEuclidean(n_sums.get(i), n_template_1[i], 2);
            diff_list.add(diff);
            diff_sum+=diff;
        }

        diff_queue.add(diff_sum);
        System.out.println(diff_sum+","+incoming_count);

        queue.remove(0);
        if(diff_queue.size() > 60) {
            diff_queue.remove(0);
        }
        int index = diff_queue.size()-15;

        if(index-15 >= 0 && index+15 <= diff_queue.size())
        {
            double test = diff_queue.get(index);
            List<Double> subset = diff_queue.subList(index-15, index+15);
            double avg = getAverage(subset);
            double min = Collections.min(subset);
            if(test == min && noDuplicates(subset, min))
            {
                if(first_three < 3)
                {
                    match_diff += avg - test;
                    count++;
                    first_three++;
                    //TTS.getInstance().speak(""+count);
                    //System.out.println(avg+","+test+","+incoming_count+","+count);
                    //listener.countChanged(count);
                }
                else if(first_three++ == 3)
                {
                    match_diff = match_diff/3;
                    count++;
                    //TTS.getInstance().speak(""+count);
                    //System.out.println(avg+","+test+","+incoming_count+","+count);
                    //listener.countChanged(count);
                }
                else if(avg-test > match_diff/2)
                {
                    count++;
                    //TTS.getInstance().speak(""+count);
                    //System.out.println(avg+","+test+","+incoming_count+","+count);
                    //listener.countChanged(count);
                }
                else
                {
                    //System.out.println(avg+","+test+","+incoming_count+","+count);
                }
            }
        }
    }

    private double getAverage(List<Double> list)
    {
        double sum = 0;
        for(Double temp : list)
        {
            sum +=temp;
        }
        return sum/list.size();
    }

    public int getCount()
    {
        return count;
    }

    public void clear()
    {
        this.queue.clear();
        for(int i=0; i<30; i++)
        {
            queue.add(empty());
        }
        count = 0;
        listener.countChanged(count);
        incoming_count = 0;
        diff_queue.clear();

        match_diff = 0;
        int first_three = 0;
    }
}