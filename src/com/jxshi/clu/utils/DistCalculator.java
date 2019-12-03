package com.jxshi.clu.utils;

/**
 * Cluster Class
 * @author jxshi21
 * @param None
 * @date 2019/12/02
 */

public class DistCalculator {
	/**
	 * calculate Euclidean distance
	 * @author jxshi21
	 */
	public double calEuclideanDist(Point p1, Point p2) {
		double dist = 0.0;
		double[] p1_localArray = p1.getCoords();
		double[] p2_localArray = p2.getCoords();
		int length1 = p1_localArray.length;
		int length2 = p2_localArray.length;
		if (length1 != length2) {
			throw new IllegalArgumentException("Dimension of two points must be equal!");
		}
		for (int i = 0; i < length1; i++) {
			dist += Math.pow(p1_localArray[i] - p2_localArray[i], 2);
		}
		return Math.sqrt(dist);
	}
	
	/**
	 * calculate Manhattan distance
	 * @author jxshi21
	 */
	public double calManhattanDist(Point p1, Point p2) {
		double dist = 0.0;
		double[] p1_localArray = p1.getCoords();
		double[] p2_localArray = p2.getCoords();
		for (int i = 0; i < p1_localArray.length; i++) {
			dist += Math.abs(p1_localArray[i]-p2_localArray[i]);
		}
		return dist;
	}
}
