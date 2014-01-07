package com.lkh.cflow.ha;

public class Elector {
	public static int elect(int number) {
		double d = java.lang.Math.random();
		int selected = (int) java.lang.Math.floor(d * number);
		return selected;
	}
}
