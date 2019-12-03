package com.jxshi.clu.utils;

import java.util.Arrays;

/**
 * Point Class
 * @author jxshi21
 * @param None
 * @date 2019/12/02
 */

public class Point {
	// for all clustering models
	private int id = -1;					// index in data set, -1 indicates it's not in the data set
	private double[] coords;				// coordinates of the point
	private int classId = -1;				// -1 indicates the point does not have a class id
	private int clusterId = -1;				// -1 indicates the point does not belong to any cluster
	private boolean outlierFlag = false;	// true indicates that the point is an outlier
	// for KMeans
	private double disToCtr;				// distance to center point	
	
	/**
	 * Constructors
     * @author jxshi21
     */
	public Point(double[] coords) {
		this.coords = coords;
	}

	public Point(int id, double[] coords) {
		this.id = id;
		this.coords = coords;
	}
	
	public Point(int id, double[] coords, int classId) {
		this.id = id;
		this.coords = coords;
		this.classId = classId;
	}
	
	/**
	 * Get() & Set()
     * @author jxshi21
     */
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }
	
	public double[] getCoords() { return this.coords; }
	public void setCoords(double[] coords) { this.coords = coords; }
	
	public int getClassId() { return this.classId; }
	public void setClassId(int classId) { this.classId = classId; }
	
	public int getClusterId() { return this.clusterId; }
	public void setClusterId(int clusterId) { this.clusterId = clusterId; }
	
	public boolean getOutlierFlag() {return this.outlierFlag; }
	public void setOutlierFlag(boolean outlierFlag) { this.outlierFlag = outlierFlag; }
	
	public double getDisToCtr() { return this.disToCtr; }
	public void setDisToCtr(double disToCtr) { this.disToCtr = disToCtr; }
	
	/**
	 * override
	 * @author jxshi21
	 */
	@Override
	public String toString() {
		String pointMsg = "point{" + this.id + "}\t(";
		for (int i = 0; i < this.coords.length-1; i++) {
			pointMsg += this.coords[i] + ",";
		}
		pointMsg += this.coords[this.coords.length-1] + ")";
//		pointMsg = pointMsg.trim() + "\tbelongs to cluster{" + this.clusterId + "}\n"
		return pointMsg;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass())
			return false;
		Point point = (Point) obj;
		if (!Arrays.equals(this.coords, point.coords))
			return false;
		for (int i = 0; i < this.coords.length; i++) {
			if (Double.compare(this.coords[i], point.coords[i]) != 0)
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int length = this.coords.length;
		int result = 0;
		for (int i = 0; i < length; i++) {
			double x = this.coords[i];
			long temp = x != 0.0d ? Double.doubleToLongBits(x) : 0L;
			result = 31 * result + (int) (temp ^ (temp >>> 32));
		}
		return result; 
	}
}
