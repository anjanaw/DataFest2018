/*
 * DtwTest.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package com;

import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;
import com.dtw.TimeWarpInfo;


public class DtwTest
{

   // PUBLIC FUNCTIONS
   public static void main(String[] args)
   {
      final TimeSeries tsI = new TimeSeries("C:\\IdeaProjects\\Data\\count\\step\\template.csv", false, false, ',');
      for(int i=0; i<799; i++) {
         final TimeSeries tsJ = new TimeSeries("C:\\IdeaProjects\\Data\\count\\step\\timeseries_" + i + ".csv", false, false, ',');

         final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
         final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, distFn);

         System.out.println(info.getDistance());
         //System.out.println("Warp Path:     " + info.getPath());
      }


   }

}
