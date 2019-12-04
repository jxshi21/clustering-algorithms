package com.jxshi.clu.utils;

import java.util.List;
import java.util.ArrayList;

/**
 * Cluster Class
 * @author jxshi21
 * @param None
 * @date 2019/12/02
 */

public class Cluster {
	// for all clustering models
	private int id;						// cluster id
	private List<Point> members = null;	// cluster members
	// for KMeans
	private Point center; 				// cluster center
	
	/**
	 * Constructors
     * @author jxshi21
     */
	public Cluster(int id) {
		this.id = id;
		this.members = new ArrayList<Point>();
	}
	
	public Cluster(int id, List<Point> members) {
		this.id = id;
		this.members = members;
	}
	
	public Cluster(int id, Point center) {
		this.id = id;
		this.center = center;
		this.members = new ArrayList<Point>();
		
	}
	
	public Cluster(int id, List<Point> members, Point center) {
		this.id = id;
		this.members = members;
		this.center = center;
	}
	
	/**
	 * Get() & Set()
     * @author jxshi21
     */
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }
	
	public List<Point> getMembers() { return this.members; }
	public void setMembers(List<Point> members) { this.members = members; }
	
	public Point getCenter() { return this.center; }
	public void setCenter(Point center) { this.center = center; }
	
	/**
	 * addMember()
     * @author jxshi21
     */
	public void addMember(Point point) {
		if (!this.members.contains(point)) {
			this.members.add(point);
		} else {
//			System.out.println("[Warning] Add point {" + point.getId() + "} to cluster {" +this.getId()+ "} failed. Point already exists!");
		}
	}
	
	/**
	 * override
	 * @author jxshi21
	 */
	@Override
	public String toString() {
		String clusterMsg = "cluster{" + this.id + "} have members:\n";
		for (Point point : this.members) {
			clusterMsg += "\t" + point.toString();
		}
		return clusterMsg + "\n";
	}
}
