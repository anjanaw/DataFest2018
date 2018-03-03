package uk.rgu.csdm.ubs.data;

import uk.rgu.csdm.ubs.tts.TTS;

import java.util.*;

public class Counter {
    private static Counter instance;

    private static Queue<Double[][]> queue = new LinkedList<>();
    private static int count = 0;

    private static final int WINDOW = 60;
    private static final int SMOOTHING_WINDOW = 10;

    private static List<Double> diff_queue = new ArrayList<>();

    private static final double MIN = 0.0;
    private static final double MAX = 2096640.0;

    private static final double[] n_template = {4.7504578754578754E-4,5.155868437118437E-4,4.392742673992674E-4,4.6932234432234434E-4,4.621680402930403E-4,
            4.612141330891331E-4,4.96031746031746E-4,4.607371794871795E-4,4.292582417584175E-4,4.163804945054945E-4,4.068414224664225E-4,4.144726800976801E-4,
            4.130418192918193E-4,4.440438034188034E-4,3.982562576312576E-4,3.8776327838827837E-4,4.0302579365079363E-4,3.99210168351648E-4,3.925328144078144E-4,
            3.891941391941392E-4,3.734546703296703E-4,3.682081807081807E-4,3.7870115995115996E-4,3.8442460317460316E-4,3.8251678876678876E-4,3.8156288156288156E-4,
            3.49175824175824E-4,9.691697191697192E-4,0.003081597222222222,0.005934733669108669,1.7313415750915752E-4,2.442002442002442E-4,0.004454746642246642,
            2.3179945054945054E-4,2.1701388888888888-4,0.004524858821733822,0.004507688492063492,1.7647283272283272E-4,0.004072229853479854,0.008112026862026863,
            0.01101810515873016,0.0077390491452991456,0.006184657356532356,0.00528559981849817,0.005103880494505495,0.006348252442002442,0.005926625457875458,
            0.005573202838827839,0.005644745879120879,0.006082112332112332,0.006358268467643468,0.005323756105006105,0.00879407512820513,0.004516750610500611,
            0.003415941697191697,0.004768582112332113,0.0011537507631257631,4.3021214896214895E-4,4.2830433455433455E-4,4.826770451770452E-4};

    public static Counter getInstance() {
        if (instance == null) {
            instance = new Counter();
        }
        return instance;
    }

    private Counter()
    {
        for(int i=0; i<59; i++)
        {
            queue.add(empty());
        }
    }

    private Double[][] empty()
    {
        Double[][] empty = new Double[32][];
        for(int i=0; i<32; i++)
        {
            Double[] d = new Double[16];
            for(int j=0; j<16; j++)
            {
                d[j] = 1.0;
            }
            empty[i] = d;
        }
        return empty;
    }

    public void add(Double[][] incoming)
    {
        queue.add(incoming);
        if(queue.size() == 60)
        {
            count();
        }
    }

    private double sum(List<Double> input)
    {
        double sum = 0;
        for(Double dd : input)
        {
            sum+=dd;
        }
        return sum;
    }

    private double sd(List<Double> input, double mean)
    {
        double sum = 0.0;
        for(Double dd : input)
        {
            double dif = dd - mean;
            sum += Math.pow(dif, 2);
        }

        double div = sum / input.size();

        return Math.sqrt(div);
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

    private List<Double> standardise(List<Double> input, double mean, double sd)
    {
        List<Double> n = new ArrayList<>();
        for(Double dd : input)
        {
            n.add((dd - mean) / sd);
        }
        return n;
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
            double diff = Math.abs(n_sums.get(i) - n_template[i]);
            diff_list.add(diff);
            diff_sum+=diff;
        }

        diff_queue.add(diff_sum);

        queue.remove();
        if(diff_queue.size() > 60) {
            diff_queue.remove(0);
        }
        int index = diff_queue.size()-15;

        if(index-15 >= 0 && index+15 <= diff_queue.size())
        {
            double test = diff_queue.get(index);
            List<Double> subset = diff_queue.subList(index-15, index+15);
            if(test == Collections.max(subset)) {
                count++;
                TTS.getInstance().speak(""+count);
            }
        }
    }


}
