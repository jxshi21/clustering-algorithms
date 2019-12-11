package com.jxshi.clu.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jxshi.clu.utils.Cluster;
import com.jxshi.clu.utils.DistCalculator;
import com.jxshi.clu.utils.Point;

/**
 * DBSCAN Clustering Model (basic version)
 * @author jxshi21
 * @param None
 * @date 2019/12/10
 */

public class DBSCAN {
	// for all clustering models
	private List<double[]> data = null;		// initial data set
	private int dataSize = 0;				// size of data set
	private int dim = 0;					// dimension of data set
	private List<Point> points = null;		// initial point set
	private Set<Cluster> clusters = null;	// clustering results
	private List<Point> outliers = null;	// outliers
	public static DistCalculator disCalculator = new DistCalculator();
	// for DBSCAN only
	private double eps;
	private int minPts;
	private int numOfClusters;				// number of clusters
	
	/**
	 * Constructors
     * @author jxshi21
     */
	public DBSCAN(List<double[]> data, double eps, int minPts) {
		// field initialization
		this.data = data;
		this.dataSize = data.size();
		this.dim = data.get(0).length;
		this.eps = eps;
		this.minPts = minPts;
		// field validity check
		checkParams();
		// create initial point set, cluster set and outlier set
		this.points = new ArrayList<Point>();
		for (int i = 0; i < this.dataSize; i++) {
			this.points.add(new Point(i, data.get(i)));
		}
		this.clusters = new HashSet<Cluster>();
		this.outliers = new ArrayList<Point>();
	}
	
	/**
	 * Get() & Set()
     * @author jxshi21
     */
	public int getNumOfClusters() { return this.numOfClusters; }
	public Set<Cluster> getClusterSet() { return this.clusters; }
	public List<Point> getOutliers() { return this.outliers; }
	public double getEps() { return this.eps; }
	public int getMinPts() { return this.minPts; }
	
	/**
	 * checkParams()
     * @author jxshi21
     */
	private void checkParams() {
		if (Double.compare(this.eps, 0) <= 0) {
			throw new IllegalArgumentException("[ERROR] DBSCAN.eps must be a double > 0 !");
		}
		if (this.minPts <= 0) {
			throw new IllegalArgumentException("[ERROR] DBSCAN.minPts must be an integer > 0!");
		}
		if (this.data == null) throw new IllegalArgumentException("[ERROR] An original data set is required!");
		if (this.dataSize == 0) throw new IllegalArgumentException("[ERROR] Data set can't be empty!");
		if (this.dim < 1) throw new IllegalArgumentException("[ERROR] DBSCAN.dim must be an integer > 0!");
	}
	
	/**
	 * clustering()
     * @author jxshi21
     */
	public void clustering() {
		System.out.println("\n[Process Info] Start clustering......"); // debug
		int count = 0; // count number of clusters
		for (Point p : this.points) {
			if (p.getClusterId() == -1) {
				if (ExpandCluster(p, count)) {
					count++;
				} // end if
			} // end if
		} // end for 
		
		// add ourliers
		for (Point p : this.points) {
			if (p.getOutlierFlag()) this.outliers.add(p);
		}
		this.numOfClusters = count;
		System.out.println("\nNumber of clusters: " + this.numOfClusters); // debug
		System.out.println("\n[Process Info] Clustering finished."); // debug
	} // end clustering()
	
	/**
	 * ExpandCluster()
     * @author jxshi21
     */
	private boolean ExpandCluster(Point p, int clId) {
		List<Integer> seeds = regionQuery(p); // [!!!] seeds not include point p
		if (seeds.size() < this.minPts) { // no core point
			p.setClusterId(-2); // -2 means outlier
			p.setOutlierFlag(true); // mark p as an outlier
//			outliers.add(p); // outlier flag may change later
			return false;
		} else { // p is a core point, all points in seeds are density-reachable from p
			// create new cluster
			Cluster newCluster = new Cluster(clId);
			this.clusters.add(newCluster);
			// set cluster id, add p and seeds to new cluster
			p.setClusterId(clId);
			p.setOutlierFlag(false);
			newCluster.addMember(p);
			for (int idx : seeds) {
				Point currentP = this.points.get(idx);
				currentP.setClusterId(clId);
				currentP.setOutlierFlag(false);
				newCluster.addMember(currentP);
			}
			while (seeds.size() != 0) {
				Point currentP = this.points.get(seeds.get(0));
				List<Integer> result = regionQuery(currentP);
				if (result.size() >= this.minPts) {
					for (Integer idx : result) {
						Point resultP = this.points.get(idx);
						if (resultP.getClusterId() == -1 || resultP.getClusterId() == -2) {
							if (resultP.getClusterId() == -1) {
								seeds.add(resultP.getId());
							} // end if // UNCLASSIFIED
							resultP.setClusterId(clId);
							resultP.setOutlierFlag(false);
							newCluster.addMember(resultP);
						} // end if // UNCLASSIFIED or NOISE
					} // end for
				} // end if // result.size >= MinPts
				seeds.remove(0);
			} // end while // seeds <> Empty
			return true;
		} // end if
	} // end ExpandCluster()
	
	/**
	 * regionQuery()
     * @author jxshi21
     */
	private List<Integer> regionQuery(Point p){
		List<Integer> epsRegion = new ArrayList<Integer>();
		for (Point y : this.points) {
			if ( p.getId() != y.getId() ) {
				double dist = disCalculator.calEuclideanDist(p, y);
				if ( Double.compare(dist, this.eps) <= 0) {
					epsRegion.add(y.getId());
				}
			}	
		}
		return epsRegion;
	}
	
}