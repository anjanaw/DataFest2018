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

    private static final double[] n_template = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0.0009691697191697192,0.003081597222222222,
            0.005934733669108669,0,0,0.004454746642246642,0,0,0.004524858821733822,0.004507688492063492,0,0.004072229853479854,0.008112026862026863,
            0.01101810515873016,0.0077390491452991456,0.006184657356532356,0.005285599816849817,0.005103880494505495,0.006348252442002442,
            0.005926625457875458,0.005573202838827839,0.005644745879120879,0.006082112332112332,0.006358268467643468,0.005323756105006105,
            0.008794070512820513,0.004516750610500611,0.003415941697191697,0.004768582112332113,0.0021537507631257631,0.002768582112332113,
            0.0011537507631257631,0};

    public static TestPeakCounter getInstance()
    {
        if (instance == null) {
            instance = new TestPeakCounter();
        }
        return instance;
    }

    private TestPeakCounter()
    {
        for(int i=0; i<59; i++)
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
            double diff = getEuclidean(n_sums.get(i), n_template[i], 2);
            diff_list.add(diff);
            diff_sum+=diff;
        }

        diff_queue.add(diff_sum);
        //System.out.println(diff_sum);

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
                    System.out.println(avg+","+test+","+incoming_count+","+count);
                    //listener.countChanged(count);
                }
                else if(first_three++ == 3)
                {
                    match_diff = match_diff/3;
                    count++;
                    //TTS.getInstance().speak(""+count);
                    System.out.println(avg+","+test+","+incoming_count+","+count);
                    //listener.countChanged(count);
                }
                else if(avg-test > match_diff/2)
                {
                    count++;
                    //TTS.getInstance().speak(""+count);
                    System.out.println(avg+","+test+","+incoming_count+","+count);
                    //listener.countChanged(count);
                }
                else
                {
                    System.out.println(avg+","+test+","+incoming_count+","+count);
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
        for(int i=0; i<59; i++)
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