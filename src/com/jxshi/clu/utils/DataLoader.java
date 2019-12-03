package com.jxshi.clu.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Data Loader
 * @author jxshi21
 * @param None
 * @date 2019/12/03
 */

public class DataLoader {
	
	private String filePath;		// file path
	private String separator;		// data separator
	private int attrIdxL;			// feature selection start index
	private int attrIdxR;			// feature selection end index
	private int labelIdx;			// class label index
	
	public DataLoader(String filePath, String separator, int attrIdxL, int attrIdxR, int labelIdx) {
		this.filePath = filePath;
		this.separator = separator;
		this.attrIdxL = attrIdxL;
		this.attrIdxR = attrIdxR;
		this.labelIdx = labelIdx;
	}
	
	public ArrayList<double[]> parseData(int[] classLabels) {
		
		BufferedReader br = null;
		
		ArrayList<double[]> dataSet = new ArrayList<double[]>();
		
		try {
			// read file (with Chinese support)
			FileInputStream fis = new FileInputStream(this.filePath);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			br = new BufferedReader(isr);
			
			// read file line by line
			int dim = this.attrIdxR - this.attrIdxL + 1; // dimension = AttrIdxR - AttrIdxL + 1
			int count = 0;		// count number of rows
			String curLine; 	// current line
			while ((curLine=br.readLine())!=null) {
				curLine = curLine.strip();
				if (curLine!="") { // skip blank lines
					String[] cells = curLine.split(this.separator);
					double[] curData = new double[dim];
					for (int i = this.attrIdxL; i < dim; i++) {
						curData[i] = Double.parseDouble(cells[i]);
					}				
					dataSet.add(curData);
					classLabels[count] = Integer.parseInt(cells[this.labelIdx]);
//					System.out.println("finished reading row{" + count + "}: " + curLine); // debug
					count++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return dataSet;
	}

}
