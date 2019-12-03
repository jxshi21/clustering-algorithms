package com.jxshi.clu.tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import com.jxshi.clu.algorithms.*;
import com.jxshi.clu.utils.*;

/**
 * KMeans Clustering Test
 * @author jxshi21
 * @param None
 * @date 2019/12/03
 */

public class KMeansTest {

	public static void main(String[] args) {
		// read data
		String filePath = "D:\\Users\\Jinxin Shi\\eclipse-workspace\\Clustering\\data\\datasets\\square5.data";
        DataLoader dataLoader = new DataLoader(filePath, ",", 0, 1, 2); // select column{0,1} as features and column{2} as class labels
        int[] class_labels = new int[1000];
        ArrayList<double[]> square5 = dataLoader.parseData(class_labels);
        
        // create KMeans clustering model
        double threshold = 0.001;
        int iterMax = 500;
        KMeans kmeansModel = new KMeans(4, threshold, iterMax, square5);
        kmeansModel.clustering();
        System.out.println("[Finished] Clustering finished.");
        
        // plot results
        drawPlot(kmeansModel);
	}
	
	public static void drawPlot(KMeans kmeansModel) {
		DefaultXYDataset xydataset = new DefaultXYDataset();
		Set<Cluster> clusterSet = kmeansModel.getClusterSet();
		
		// get coordinates and cluster labels
		for (Cluster cluster : clusterSet) {
			List<Point> members = cluster.getMembers();
			double[][] coords = new double[2][members.size()]; // row{0}: coord_x | row{1}: coord_y
			for (int i = 0; i < members.size(); i++) {
				coords[0][i] = members.get(i).getCoords()[0]; // x coordinate
				coords[1][i] = members.get(i).getCoords()[1]; // y coordinate
			}
			xydataset.addSeries(cluster.getId(), coords);
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot("kmeans-clustering", "x", "y", xydataset);
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