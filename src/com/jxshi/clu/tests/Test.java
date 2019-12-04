package com.jxshi.clu.tests;

import java.util.Random;

import com.jxshi.clu.utils.DataLoader;
import com.jxshi.clu.utils.DistCalculator;
import com.jxshi.clu.utils.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

	public static void main(String[] args) {
//		double[] arr = new double[10];
//		long seed = System.currentTimeMillis();
//		Random rand = new Random(seed);
//		for (int i = 0; i < 10; i++) {
//			double d = 10.0 + (20.0 - 10.0) * rand.nextDouble();
//			arr[i] = d;
//			System.out.println(arr[i]);
//		}
//		Arrays.sort(arr);
//		System.out.print("\n\n");
//		for (int i = 0; i < 10; i++) {
//			System.out.println(arr[i]);
//		}
		
		// read data
		String filePath = "D:\\Users\\Jinxin Shi\\eclipse-workspace\\Clustering\\data\\datasets\\2d-3c-no123.data";
		DataLoader dataLoader = new DataLoader(filePath, ",", 0, 1, 2); // select column{0,1} as features and column{2} as class labels
		int[] class_labels = new int[10000];
		ArrayList<double[]> dataset = dataLoader.parseData(class_labels);
		
//		// compute epsilon and delta
//		double[] params = new double[2];
//		params = chooseInitEpsilon(3, dataset);
//		System.out.println("Initial epsilon = " + params[0]);
//		System.out.println("Step = " + params[1]);
		
		// sample 10 times and get the average epsilon
		int sampleTimes = 100;
		double aveE = 0.0;
		double aveD = 0.0;
		for (int i = 0; i < sampleTimes; i++) {
			double[] currParams = chooseInitEpsilon(3, dataset);
			aveE += currParams[0];
			aveD += currParams[1];
		}
		aveE /= sampleTimes;
		aveD /= sampleTimes;
		System.out.println("\nAverage epsilon = " +aveE);
		System.out.println("Average Step = " + aveD);
		
//		List<Integer> eNeighborhood = new ArrayList<Integer>();
//		for (int i = 0; i < 10; i++) {
//			eNeighborhood.add(i);
//			System.out.println(eNeighborhood.get(i));
//		}
	}
	
	/**
	 * chooseInitEpsilon()
     * @author jxshi21
     */
	private static double[] chooseInitEpsilon(int neighborSize, List<double[]> data) {
		int dataSize = data.size();
		int k = neighborSize;
		double[] params = new double[2];
		DistCalculator disCalculator = new DistCalculator();
		// create initial point set
		List<Point> points = new ArrayList<Point>();
		points = new ArrayList<Point>();
		for (int i = 0; i < dataSize; i++) {
			points.add(new Point(i, data.get(i)));
		}
		// get a sample of initial point set (sample_rate: 10%)
		int sampleSize = (int) (0.01 * dataSize);
		List<Point> sample = new ArrayList<Point>();		
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		for (int i = 0; i < sampleSize; ) {
			int currIdx = rand.nextInt(dataSize);
			if (!sample.contains(points.get(currIdx))) {
				sample.add(points.get(currIdx));
				i++;
//				System.out.println("sampled point {" + currIdx + "}"); // debug
			}
		}
		// TODO: compute average distance of k-nearest neighbors
		// TODO: compute average distance of k+1-nearest neighbors
		double aveDistK = 0.0;
		double aveDistKPlus1 = 0.0;
		
		for (int i = 0; i < sampleSize; i++) {
			double[] dist = new double[sampleSize-1];
			int count = 0;
			for (int j = 0; j < sampleSize; j++) {
				if ( i != j ) {
					dist[count] = disCalculator.calEuclideanDist(sample.get(i), sample.get(j));
					count++;
				}
			}
			Arrays.sort(dist);
			double totalDist = 0.0;
			double distK = 0.0;
			double distKPlus1 = 0.0;
			for (int m = 0; m < k+1 ; m++) {
				totalDist += dist[m];
				if (m == k-1) {
					distK = totalDist / k;
				}
				if (m == k) {
					distKPlus1 = totalDist / (k+1);
				}
			}
			aveDistK += distK;
			aveDistKPlus1 += distKPlus1;
		}
		// TODO: compute initial epsilon and step 
		aveDistK /= sampleSize;
		aveDistKPlus1 /= sampleSize;
//		System.out.println("average distance of k-nearest neighbors: " + aveDistK); // debug
//		System.out.println("average distance of k+1-nearest neighbors: " + aveDistKPlus1); // debug
		
		params[0] = aveDistK;
		params[1] = Math.abs(aveDistKPlus1 - aveDistK);
		return params;
	}

}
