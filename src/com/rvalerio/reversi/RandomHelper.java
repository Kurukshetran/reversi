package com.rvalerio.reversi;

import java.util.Random;

public class RandomHelper {

	public static Integer[] createIntArray (int size) {
		Integer []array = new Integer[ size ];
		
		for(int j=0; j<array.length; j++)
			array[j] = j;

		Random rnd = new Random();
		int r;
		
		for(int k=array.length-1; k>0; k--) {
			r = rnd.nextInt(k);

			Integer c = array[k];
			array[k] = array[r];
			array[r] = c;
		}
		
		return array;
	}
	
}
