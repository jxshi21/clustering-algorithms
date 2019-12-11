package com.jxshi.clu.algorithms;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;

import com.jxshi.clu.utils.*;

/**
 * Sync Clustering Mode
 * @author jxshi21
 * @param 
 * @date 2019/12/04
 */

public class Sync {
	// for all clustering models
	private List<double[]> data = null;		// initial data set
	private int dataSize = 0;				// size of data set
	private int dim = 0;					// dimension of data set
	private List<Point> points = null;		// initial point set
	private Set<Cluster> clusters = null;	// clustering results
	private List<Point> outliers = null;	// outliers
	public static DistCalculator disCalculator = new DistCalculator();
	// for KMeans only
	private double epsilon;					// e-neighborhood size
//	private double delta;					// e-neighborhood size step (not used here)
	private double lambda;					// stop threshold
	private int numOfClusters;				// number of clusters
	private int clock = 0;					// time stamp
	
	/**
	 * Constructors
     * @author jxshi21
     */
	public Sync(double lambda, List<double[]> data) {
		// field initialization
		this.lambda = lambda;
		this.data = data;
		this.dataSize = data.size();
		this.dim = data.get(0).length;
		// field validity check
		checkParams();
		// create initial point set and outlier set
		this.points = new ArrayList<Point>();
		for (int i = 0; i < this.dataSize; i++) {
			this.points.add(new Point(i, data.get(i)));
		}
		this.outliers = new ArrayList<Point>();
		// compute initial epsilon and step
		double[] params = chooseInitEpsilon(3, 0.01);
		this.epsilon = params[0];
//		this.delta = params[1]; // (not used here)
	}
	
	public Sync(double epsilon, double lambda, List<double[]> data) {
		// field initialization
		this.lambda = lambda;
		this.data = data;
		this.dataSize = data.size();
		this.dim = data.get(0).length;
		// field validity check
		checkParams();
		// create initial point set and outlier set
		this.points = new ArrayList<Point>();
		for (int i = 0; i < this.dataSize; i++) {
			this.points.add(new Point(i, data.get(i)));
		}
		this.outliers = new ArrayList<Point>();
		// set initial epsilon
		this.epsilon = epsilon;
	}
	
	/**
	 * Get() & Set()
     * @author jxshi21
     */
	public int getNumOfClusters() {  return this.numOfClusters; }
	public int getTimeStamp() { return this.clock; }
	public Set<Cluster> getClusterSet() { return this.clusters; }
	public List<Point> getOutliers() { return this.outliers; }
	public List<Point> getPoints() { return this.points; }
	public double getEpsilon() { return this.epsilon; }
	
	/**
	 * checkParams()
     * @author jxshi21
     */
	private void checkParams() {
		if (Double.compare(this.lambda, 0)<=0 || Double.compare(this.lambda, 1)>=0) {
			throw new IllegalArgumentException("[ERROR] Sync.lambda must be a double between (0,1)!");
		}
		if (this.data == null) throw new IllegalArgumentException("[ERROR] An original data set is required!");
		if (this.dataSize == 0) throw new IllegalArgumentException("[ERROR] Data set can't be empty!");
		if (this.dim < 1) throw new IllegalArgumentException("[ERROR] Sync.dim must be an integer > 0!");
	}
	
	/**
	 * clustering()
     * @author jxshi21
     */
	public void clustering() {
		System.out.println("\n[Process Info] Start clustering......"); // debug
		boolean loopFlag = true;
		while (loopFlag) {
			// update coordinates of each Point
			System.out.println("Time stamp (" + this.clock + ")"); // debug
			List<double[]> newCoords = new ArrayList<double[]>();
			for (int i = 0; i < this.dataSize; i++) {
				// Compute Nb(p)
				List<Integer> eNeighborhood = computeENneighborhood(this.points.get(i), this.points);
				// Obtain new coords of p using Eq.(6)
				double[] newCoord = computeNewCoord(this.points.get(i), eNeighborhood);
				newCoords.add(newCoord);
			}			
			// update coordinates of each Point
			for (int i = 0; i < this.dataSize; i++) {
				this.points.get(i).setCoords(newCoords.get(i));
			}
			System.out.println("Coordiantes updated."); // debug
			
			// compute local order and loopFlag
			double rc = computeLocalOrder();
			System.out.println("rc = " + rc + "."); // debug
			if (Double.compare(rc, this.lambda) > 0) {
				loopFlag = false;
				findClusters(); // find synchronized clusters
				
			}
			this.clock++;
		}
		System.out.println("\n[Process Info] Clustering finished."); // debug
	}
	
	
	/**
	 * chooseInitEpsilon()
     * @author jxshi21
     */
	private double[] chooseInitEpsilon(int neighborSize, double sampleRate) {
		System.out.println("[Process Info] Choosing initial epsilon and delta......"); // debug
		int k = neighborSize;
		double[] params = new double[2];
		// get a sample of initial point set (suggested sample_rate: 1%)
		int sampleSize = (int) (sampleRate * this.dataSize);
		List<Point> sample = new ArrayList<Point>();		
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		for (int i = 0; i < sampleSize; ) {
			int currIdx = rand.nextInt(this.dataSize);
			if (!sample.contains(points.get(currIdx))) {
				sample.add(points.get(currIdx));
				i++;
			}
		}
		// compute average distance of k-nearest and k+1-nearest neighbors
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
		// compute initial epsilon and step
		aveDistK /= sampleSize;
		aveDistKPlus1 /= sampleSize;
		System.out.println("average distance of k-nearest neighbors: " + aveDistK); // debug
		System.out.println("average distance of k+1-nearest neighbors: " + aveDistKPlus1); // debug
		params[0] = aveDistK;
		params[1] = Math.abs(aveDistKPlus1 - aveDistK);
		System.out.println("Initial epsilon = " + params[0]); // debug
		System.out.println("Delta = " + params[1]); // debug
		return params;
	}

	/**
	 * computeENneighborhood()
     * @author jxshi21
     */
	private List<Integer> computeENneighborhood(Point x, List<Point> points) {		
		List<Integer> eNeighborhood = new ArrayList<Integer>();
		for (Point y : points) {
			if ( x.getId() != y.getId() ) {
				double dist = disCalculator.calEuclideanDist(x, y);
				if ( Double.compare(dist, this.epsilon) <= 0) {
					eNeighborhood.add(y.getId());
				}
			}	
		}
		return eNeighborhood;
	}
	
	/**
	 * computeNewCoord()
     * @author jxshi21
     */
	private double[] computeNewCoord(Point x, List<Integer> eNeighborhood) {
		double[] newCoord = new double[this.dim];
		int numOfNeighbors = eNeighborhood.size();
		for (int i = 0; i < this.dim; i++) {
			double xi = x.getCoords()[i];
			double sum = 0.0;
			for (int j = 0; j < numOfNeighbors; j++) {
				double yi = this.points.get(eNeighborhood.get(j)).getCoords()[i];
				sum += Math.sin(yi - xi);
			}
			newCoord[i] = xi + (sum / numOfNeighbors);
		}
		return newCoord;
	}
	
	/**
	 * computeLocalOrder()
     * @author jxshi21
     */
	private double computeLocalOrder() {
		double rc = 0.0;
		for (Point x : this.points) {
			List<Integer> eNeighborhood = computeENneighborhood(x, this.points);
			int numOfNeighbors = eNeighborhood.size();
			double rp = 0.0;
			for (int i = 0; i < numOfNeighbors; i++) {
				Point y = this.points.get(eNeighborhood.get(i));
				rp += Math.exp(0.0 - disCalculator.calEuclideanDist(x, y));
			}
			if (numOfNeighbors != 0) rp /= numOfNeighbors;
			rc += rp;
		}
		rc /= this.dataSize;
		return rc;
	}

	/**
	 * findCluster()
     * @author jxshi21
     */
	private void findClusters() {
		System.out.println("\n[Process Info] Finding clusters......"); // debug
		this.clusters = new HashSet<Cluster>();
		int count = 0; // count number of clusters
		for (Point x : this.points) {
//			System.out.print("for point {" + x.getId() + "} find synchronized point:"); // debug
			if (x.getClusterId() == -1) { // for each unallocated point x
				int clusterId = -1;
				List<Integer> syncPoints = new ArrayList<Integer>();
				for (Point y : this.points) { // find points synchronized with x
					if (x.getId() != y.getId()) {
						if (isSynchronized(x, y)) {
//							System.out.print("\t{" + y.getId() + "}"); // debug
							syncPoints.add(y.getId());
							if (y.getClusterId() != -1) {
								clusterId = y.getClusterId();
							}
						}
					}
				}
				if (syncPoints.size() == 0) { // if syncPoints is empty
					x.setClusterId(-2); // -2 means outlier
					x.setOutlierFlag(true); // mark x as an outlier
					outliers.add(x);
				} else {
					if (clusterId != -1) {
						// get target cluster
						Iterator<Cluster> iterator = this.clusters.iterator();
						while (iterator.hasNext()) {
							Cluster next = (Cluster) iterator.next();
							if (next.getId() == clusterId) {
								// add members to this cluster
								x.setClusterId(clusterId);
								next.addMember(x);
								for (Integer i : syncPoints) {
									this.points.get(i).setClusterId(clusterId);
									next.addMember(this.points.get(i));
								}
							}
							break;
						}
					} else { // if all synchronized points are unallocated
						Cluster newCluster = new Cluster(count);
						x.setClusterId(count);
						newCluster.addMember(x);
						for (Integer i : syncPoints) {
							this.points.get(i).setClusterId(count);
							newCluster.addMember(this.points.get(i));
						}
						this.clusters.add(newCluster);
						count++;
					}
				}
			}
//			System.out.print("\n");
		}
		this.numOfClusters = count;
		System.out.println("\nFinished. Number of clusters: " + this.numOfClusters); // debug
	}
	
	/**
	 * isSynchronized()
     * @author jxshi21
     */
	private boolean isSynchronized(Point x, Point y) {
		return ( Double.compare(disCalculator.calEuclideanDist(x, y), 0.1) < 0 );
	}
}
