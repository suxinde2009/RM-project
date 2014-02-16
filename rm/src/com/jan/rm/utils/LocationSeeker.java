package com.jan.rm.utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.jan.rm.R;
import com.jan.rm.logger.RLog;
import com.jan.rm.widget.RMToast;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

public class LocationSeeker {
	
	private Context context;
	
	private static LocationSeeker locationSeeker;
	
	private LocationRequest locationRequest;
	private LocationClient googleLocationClient;
	
	private ArrayList<OnLocationGotListener> onLocationGotListeners;
	
	private long minTime;
	private float smallestDisplacementMeters;
	
	public interface OnLocationGotListener{
		public void onLocationGot(Location location);
	}
	
	private ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks(){

		@Override
		public void onConnected(Bundle dataBundle) {
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setInterval(minTime);
			locationRequest.setFastestInterval(1000);
			//locationRequest.setSmallestDisplacement(smallestDisplacementMeters);
			
			Location currentLocation = googleLocationClient.getLastLocation();
			
			if(currentLocation != null){
				locationGot(currentLocation);
			}
			
			googleLocationClient.requestLocationUpdates(locationRequest, locationListener);
		}

		@Override
		public void onDisconnected() {
			RMToast.showNegative(context, context.getString(R.string.location_navigation_disconnected));
		}
		
	};
	
	private OnConnectionFailedListener onConnectionFailedListener = new OnConnectionFailedListener(){

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			RMToast.showNegative(context, context.getString(R.string.location_navigation_disconnected));
		}
		
	};
	
	private LocationListener locationListener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location) {
			RLog.d(location.getLatitude() + "", location.getLongitude() + "");
			locationGot(location);
		}

	};
	
	public LocationSeeker(Context context){
		
		this.context = context;
		
		locationRequest = LocationRequest.create();
		googleLocationClient = new com.google.android.gms.location.LocationClient(context, connectionCallbacks, onConnectionFailedListener);
		
		onLocationGotListeners = new ArrayList<OnLocationGotListener>();
	}
	
	public static LocationSeeker getInstance(Context context){
		if(locationSeeker == null){
			locationSeeker = new LocationSeeker(context);
		}
		
		return locationSeeker;
	}
	
	public void findMyLocation(long minTime, float smallestDisplacementMeters, OnLocationGotListener onLocationGotListener){
		
		if(onLocationGotListener != null) registerOnLocationGotListener(onLocationGotListener);
		
		this.minTime = minTime;
		this.smallestDisplacementMeters = smallestDisplacementMeters;
		
		if(!googleLocationClient.isConnected() || !googleLocationClient.isConnecting()) googleLocationClient.connect();
	}
	
	public void stopFindingLocation(OnLocationGotListener onLocationGotListener){
		
		if(onLocationGotListener != null) unregisterOnLocationGotListener(onLocationGotListener);
		
		if(googleLocationClient.isConnected()) googleLocationClient.removeLocationUpdates(locationListener);
		googleLocationClient.disconnect();
	}
	
	public void registerOnLocationGotListener(OnLocationGotListener onLocationGotListener){
		onLocationGotListeners.add(onLocationGotListener);
	}
	
	public void unregisterOnLocationGotListener(OnLocationGotListener onLocationGotListener){
		onLocationGotListeners.remove(onLocationGotListeners.indexOf(onLocationGotListener));
	}
	
	private void locationGot(Location location){
		if(onLocationGotListeners != null && onLocationGotListeners.size() > 0){
			for(OnLocationGotListener locationListener : onLocationGotListeners){
				locationListener.onLocationGot(location);
			}
		}
	}
	
	public static String getFormatedCoords(double coords){
		int degree = (int) Math.floor(Math.abs(coords));
		double temp = getdPoint(Math.abs(coords)) * 60;
		int min = (int) Math.floor(temp);
		double sec = getdPoint(temp) * 60;
		sec = Math.round(sec * 100) / 100D;
		
		return degree + "°" + min + "′" + sec + "″";
	}
	
	private static double getdPoint(double num){
		double d = num;
		int fInt = (int) d;
		BigDecimal b1 = new BigDecimal(Double.toString(d));
		BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
		double dPoint = b1.subtract(b2).floatValue();
		
		return dPoint;
	}
	
}
