package uk.rgu.csdm.ubs.count;

import com.dtw.TimeWarpInfo;
import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DTWCounter implements Counter {
    private static DTWCounter instance;

    private static List<Double[][]> incoming_queue = new LinkedList<>();
    private static int count = 0;

    private static final int CHECK_WINDOW = 30;
    private static final int CHECK_WINDOW_2 = CHECK_WINDOW/2;

    private static List<Double> distance_queue = new LinkedList<>();

    private static final double MIN = 0.0;
    private static final double MAX = 2096640.0;

    private static final TimeSeries tsI = new TimeSeries("C:\\IdeaProjects\\Data\\count\\step\\template.csv", false, false, ',');
    private static final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");

    public static DTWCounter getInstance() {
        if (instance == null) {
            instance = new DTWCounter();
        }
        return instance;
    }

    private DTWCounter()
    {

    }

    public void add(Double[][] incoming)
    {
        incoming_queue.add(incoming);
        count();
    }

    private double[] normalise(double[] input)
    {
        double[] n = new double[input.length];
        AtomicInteger counter = new AtomicInteger();
        for(double dd : input)
        {
            n[counter.getAndIncrement()] = (dd - MIN) / (MAX-MIN);
        }
        return n;
    }

    private void count() {
        double[] sums = new double[incoming_queue.size()];
        AtomicInteger counter = new AtomicInteger();
        for (Double[][] elem : incoming_queue) {
            double _sum = 0.0;
            for (Double[] ele : elem) {
                for (Double el : ele) {
                    _sum += el;
                }
            }
            sums[counter.getAndIncrement()] = _sum;
        }

        double[] n_sums = normalise(sums);

        final TimeSeries tsJ = new TimeSeries(n_sums);
        final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, distFn);

        distance_queue.add(info.getDistance());
        //System.out.println(distance_queue);

        int index = distance_queue.size() - CHECK_WINDOW_2;

        if (index - CHECK_WINDOW_2 >= 0 && index + CHECK_WINDOW_2 <= distance_queue.size()) {
            double test = distance_queue.get(index);
            List<Double> subset = distance_queue.subList(index - CHECK_WINDOW_2, index + CHECK_WINDOW_2);
            if (test == Collections.min(subset)) {
                count++;
                System.out.println(count);
                System.out.println(test);
                int end = incoming_queue.size() - CHECK_WINDOW;
                for (int i = 0; i < end; i++) {
                    incoming_queue.remove(0);
                }
                System.out.println(distance_queue);
                /*end = distance_queue.size()-CHECK_WINDOW_2;

                for(int i=0; i<end; i++)
                {
                    distance_queue.remove(0);
                }*/
                distance_queue.clear();
            }
        }
    }

    public int getCount()
    {
        return count;
    }

    public void clear()
    {

    }

}
