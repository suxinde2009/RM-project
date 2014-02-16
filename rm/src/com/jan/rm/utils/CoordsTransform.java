package com.jan.rm.utils;

public class CoordsTransform {
	
	static{
		System.loadLibrary("coords");
	}
	
	public static native double[] getGCJ02fromWGS84(double lat, double lng);
	public static native double[] getBD09fromGCJ02(double lat, double lng);
	public static native double[] getGCJ02fromBD09(double lat, double lng);

}
