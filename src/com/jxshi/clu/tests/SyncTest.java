package com.jxshi.clu.tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import com.jxshi.clu.algorithms.Sync;
import com.jxshi.clu.utils.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * Sync Clustering Test
 * @author jxshi21
 * @param None
 * @date 2019/12/04
 */

public class SyncTest {

	public static void main(String[] args) {
		// read data
		String filePath = "D:\\Users\\Jinxin Shi\\eclipse-workspace\\Clustering\\data\\datasets\\2d-3c-no123.data";
		DataLoader dataLoader = new DataLoader(filePath, ",", 0, 1, 2); // select column{0,1} as features and column{2} as class labels
		int[] class_labels = new int[10000];
		ArrayList<double[]> dataset = dataLoader.parseData(class_labels);
		
		// create Sync clustering model
		double epsilon = 0.6;
		double lambda = 0.99;
//		Sync syncModel = new Sync(lambda, dataset); // automatically choose initial epsilon
		Sync syncModel = new Sync(epsilon, lambda, dataset); // manually set initial epsilon
		syncModel.clustering();
		
		// plot results
		plotResults(syncModel, dataset);
	}
	
	private static void plotResults(Sync syncModel, ArrayList<double[]> dataset) {
		DefaultXYDataset xydataset = new DefaultXYDataset();
		Set<Cluster> clusters = syncModel.getClusterSet();
		List<Point> outliers = syncModel.getOutliers();
		
		// get coordinates and cluster labels
		for (Cluster clu : clusters) {
			int id = clu.getId();
			int size = clu.getMembers().size();
			double[][] coords = new double[2][size]; // row{0}: coord_x | row{1}: coord_y
			for (int i = 0; i < size; i++) {
				coords[0][i] = dataset.get(clu.getMembers().get(i).getId())[0]; // x coordinate
				coords[1][i] = dataset.get(clu.getMembers().get(i).getId())[1]; // y coordinate
			}
			xydataset.addSeries(id, coords);
		}
		// outliers
		double[][] outlierCoords = new double[2][outliers.size()];
		for (int i = 0; i < outliers.size(); i++) {
			outlierCoords[0][i] = dataset.get(outliers.get(i).getId())[0]; // x coordinate
			outlierCoords[1][i] = dataset.get(outliers.get(i).getId())[1]; // y coordinate
		}
		xydataset.addSeries("outlier", outlierCoords);
		// synchronized positions
		double[][] syncCoords = new double[2][dataset.size()];
		List<Point> pointSet = syncModel.getPoints();
		for (int i = 0; i < pointSet.size(); i++) {
			syncCoords[0][i] = pointSet.get(i).getCoords()[0]; // x coordinate
			syncCoords[1][i] = pointSet.get(i).getCoords()[1]; // y coordinate
		}
		xydataset.addSeries("sync pos", syncCoords);
		
		// plot settings
		String title = "sync-clustering ( e = " + syncModel.getEpsilon() + " )";
		JFreeChart chart = ChartFactory.createScatterPlot(title, "x", "y", xydataset);
		ChartFrame frame = new ChartFrame("Clustering", chart, true);
		chart.setBackgroundPaint(Color.WHITE);
		chart.setBorderPaint(Color.GREEN);
		chart.setBorderStroke(new BasicStroke(1.5f));
		XYPlot xyplot = (XYPlot) chart.getPlot();
		
		xyplot.setBackgroundPaint(new Color(255, 253, 246));
		ValueAxis vaaxis = xyplot.getDomainAxis();
		vaaxis.setAxisLineStroke(new BasicStroke(1.5f));
		
		ValueAxis va = xyplot.getDomainAxis(0);
		va.setAxisLineStroke(new BasicStroke(1.5f));		// axis width
		va.setAxisLinePaint(new Color(215, 215, 215));		// axis color
		va.setLabelPaint(new Color(10, 10, 10));			// axis title color
		va.setTickLabelPaint(new Color(102, 102, 102));		// axis ruler color
		xyplot.setOutlineStroke(new BasicStroke(1.5f));		// border width
		ValueAxis axis = xyplot.getRangeAxis();
		axis.setAxisLineStroke(new BasicStroke(1.5f));
		
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
		renderer.setSeriesOutlinePaint(0, Color.WHITE);
		renderer.setUseOutlinePaint(true);
		
		NumberAxis nmaxis = (NumberAxis) xyplot.getDomainAxis();
		nmaxis.setAutoRangeIncludesZero(false);
		nmaxis.setTickMarkInsideLength(2.0F);    
		nmaxis.setTickMarkOutsideLength(0.0F);    
		nmaxis.setAxisLineStroke(new BasicStroke(1.5f)); 

		frame.pack();  
        frame.setVisible(true);
	}

}
