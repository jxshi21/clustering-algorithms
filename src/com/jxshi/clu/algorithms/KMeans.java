package com.jxshi.clu.algorithms;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Set;

import com.jxshi.clu.utils.*;

import java.util.HashSet;

/**
 * KMeans Clustering Model
 * @author jxshi21
 * @param None
 * @date 2019/12/02
 */

public class KMeans {
	// for all clustering models
	private List<double[]> data = null;		// initial data set
	private int dataSize = 0;				// size of data set
	private int dim = 0;					// dimension of data set
	private List<Point> points = null;		// initial point set
	private Set<Cluster> clusters = null;	// clustering results
	public static DistCalculator disCalculator = new DistCalculator();
	// for KMeans only
	private int k;							// number of clusters
	private double threshold;				// center variation threshold
	private int iterMax;					// maximum iterations
	private int iterRun = 0;				// actual iterations
	
	/**
	 * Constructors
     * @author jxshi21
     */
	public KMeans(int k, double threshold, int iterMax, List<double[]> data) {
		// field initialization
		this.k = k;
		this.threshold = threshold;
		this.iterMax = iterMax;
		this.data = data;
		this.dataSize = data.size();
		this.dim = data.get(0).length;
		// field validity check
		checkParams();
		// create initial point set
		points = new ArrayList<Point>();
		for (int i = 0; i < this.dataSize; i++) {
			this.points.add(new Point(i, data.get(i)));
		}
	}
	
	/**
	 * Get() & Set()
     * @author jxshi21
     */
	public int getNumOfClusters() { return this.k; }
	public void setNumOfClusters(int k) { this.k = k; }
	public Set<Cluster> getClusterSet() { return this.clusters; }
	
	/**
	 * checkParams()
     * @author jxshi21
     */
	public void checkParams() {
		if (this.k == 0) throw new IllegalArgumentException("[ERROR] KMeans.k must be an integer > 0!");
		if (Double.compare(this.threshold, 0) == 0) throw new IllegalArgumentException("[ERROR] KMeans.threshold must be a double > 0.0!");
		if (this.iterMax == 0) throw new IllegalArgumentException("[ERROR] KMeans.iterMax must be an integer > 0!");
		if (this.data == null) throw new IllegalArgumentException("[ERROR] An original data set is required!");
//		if (this.labels == null) throw new IllegalArgumentException("[ERROR] An original label set is required!");
		if (this.dataSize == 0) throw new IllegalArgumentException("[ERROR] Data set can't be empty!");
		if (this.dim < 1) throw new IllegalArgumentException("[ERROR] KMeans.dim must be an integer > 0!");
	}
	
	/**
	 * clustering()
     * @author jxshi21
     */
	public void clustering() {
		// TODO: clustering process
		chooseRandCtrs();
		boolean iterContinue = true;
		while (iterContinue) {
			assignCluster();
			iterContinue = calClusterCenters();
			this.iterRun++;
			if (this.iterRun >= this.iterMax) {
				iterContinue = false;
				System.out.println("Not converged but reached max iterations. Clutering Stopped.");
			}
		}
		System.out.println("Iterred " + this.iterRun + " times.");
	}
	
	/**
	 * chooseRandCtrs()
     * @author jxshi21
     */
	public void chooseRandCtrs() {
		// TODO: choose random initial centers & create initial clusters
		double[] minValues = new double[this.dim]; // minimum of each dimension
		double[] maxValues = new double[this.dim]; // maximum of each dimension
		for (int d = 0; d < this.dim; d++) {
			minValues[d] = this.data.get(0)[d];
			maxValues[d] = this.data.get(0)[d];
			for (int i = 1; i < this.dataSize; i++) {
				if (this.data.get(i)[d] < minValues[d]) minValues[d] = this.data.get(i)[d];
				if (this.data.get(i)[d] > maxValues[d]) maxValues[d] = this.data.get(i)[d];
			}
			System.out.println("Dim{" + d + "} range: (" + minValues[d] + ", " + maxValues[d] + ")"); // debug
		}
		// TODO: choose random initial centers & create initial clusters
		this.clusters = new HashSet<Cluster>();
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		for (int i = 0; i < this.k; i++) {
			double[] coords = new double[this.dim];
			for (int d = 0; d < this.dim; d++) {
				double min = minValues[d];
				double max = maxValues[d];
				coords[d] = min + ((max - min) * rand.nextDouble());
			}
			Point randCenter = new Point(coords);
			System.out.println("Center{" + i + "} chosen: " + randCenter.toString()); // debug
			Cluster newCluster = new Cluster(i, randCenter);
			this.clusters.add(newCluster);
		}
	}
	
	/**
	 * assignCluster()
     * @author jxshi21
     */
	public void assignCluster() {
		// TODO: assign each Point to its nearest Cluster
		for (Point p : this.points) {
			double minDist = Integer.MAX_VALUE;
			for (Cluster cluster : this.clusters) {
				double dist = disCalculator.calEuclideanDist(p, cluster.getCenter());
				if (dist < minDist) {
					minDist = dist;
					p.setClusterId(cluster.getId());
					p.setDisToCtr(dist);
				}
			}
		}
		for (Cluster cluster : this.clusters) {
			cluster.getMembers().clear();
			for (Point point : this.points) {
				if (point.getClusterId() == cluster.getId()) {
					cluster.addMember(point);
				}
			}
		}
	}
	
	/**
	 * calClusterCenters()
     * @author jxshi21
     */
	public boolean calClusterCenters() {
		// TODO: calculate new center Point for each Cluster
		//       and check if the process should go on
		boolean iterContinue = false;
		for (Cluster cluster : this.clusters) {
			List<Point> members = cluster.getMembers();
			double[] newCenterCoords = new double[this.dim];
			for (int d = 0; d < this.dim; d++) {
				for (int i = 0; i < members.size(); i++) {
					newCenterCoords[d] += members.get(i).getCoords()[d];
				}
				newCenterCoords[d] /= members.size();
			}
			Point newCenter = new Point(newCenterCoords);
			double distVariation = disCalculator.calEuclideanDist(newCenter, cluster.getCenter());
			if ( Double.compare(distVariation, this.threshold) > 0) {
				iterContinue = true;
			}
		}
		return iterContinue;
	}

}