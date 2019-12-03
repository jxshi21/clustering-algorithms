package com.jxshi.clu.tests;

import java.util.Random;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		for (int i = 0; i < 10; i++) {
			double d = 10.0 + (20.0 - 10.0) * rand.nextDouble();
			System.out.println(d);
		}
	}

}
