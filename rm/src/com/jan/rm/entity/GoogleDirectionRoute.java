package com.jan.rm.entity;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/*
 *   _    ________    __    __    ________    ________    __    __    ________    ___   __ _
 *  /\\--/\______ \--/\ \--/\ \--/\  _____\--/\  ____ \--/\ \--/\ \--/\  _____\--/\  \-/\ \\\
 *  \ \\ \/_____/\ \ \ \ \_\_\ \ \ \ \____/_ \ \ \__/\ \ \ \ \_\ \ \ \ \ \____/_ \ \   \_\ \\\
 *   \ \\       \ \ \ \ \  ____ \ \ \  _____\ \ \  ____ \ \_ \ \_\ \  \ \  _____\ \ \  __   \\\
 *    \ \\       \ \ \ \ \ \__/\ \ \ \ \____/_ \ \ \__/\ \  \_ \ \ \   \ \ \____/_ \ \ \ \_  \\\
 *     \ \\       \ \_\ \ \_\ \ \_\ \ \_______\ \ \_\ \ \_\   \_ \_\    \ \_______\ \ \_\_ \__\\\
 *      \ \\       \/_/  \/_/  \/_/  \/_______/  \/_/  \/_/     \/_/     \/_______/  \/_/ \/__/ \\
 *       \ \\----------------------------------------------------------------------------------- \\
 *        \//                                                                                   \//
 *
 * 
 *
 */

public class GoogleDirectionRoute {

	private String totalDistanceText;
	private int totalDistanceValue;
	private String totalDurationText;
	private int totalDurationValue;
	
	private String startAddress;
	private LatLng startLatLng;
	
	private String endAddress;
	private LatLng endLatLng;
	
	List<LatLng> route;
	List<GoogleDirectionStep> steps;
	
	public List<LatLng> getRoute() {
		return route;
	}
	public void setRoute(List<LatLng> route) {
		this.route = route;
	}
	public List<GoogleDirectionStep> getSteps() {
		return steps;
	}
	public void setSteps(List<GoogleDirectionStep> steps) {
		this.steps = steps;
	}
	public String getTotalDistanceText() {
		return totalDistanceText;
	}
	public void setTotalDistanceText(String totalDistanceText) {
		this.totalDistanceText = totalDistanceText;
	}
	public int getTotalDistanceValue() {
		return totalDistanceValue;
	}
	public void setTotalDistanceValue(int totalDistanceValue) {
		this.totalDistanceValue = totalDistanceValue;
	}
	public String getTotalDurationText() {
		return totalDurationText;
	}
	public void setTotalDurationText(String totalDurationText) {
		this.totalDurationText = totalDurationText;
	}
	public int getTotalDurationValue() {
		return totalDurationValue;
	}
	public void setTotalDurationValue(int totalDurationValue) {
		this.totalDurationValue = totalDurationValue;
	}
	public String getStartAddress() {
		return startAddress;
	}
	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}
	public LatLng getStartLatLng() {
		return startLatLng;
	}
	public void setStartLatLng(LatLng startLatLng) {
		this.startLatLng = startLatLng;
	}
	public String getEndAddress() {
		return endAddress;
	}
	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}
	public LatLng getEndLatLng() {
		return endLatLng;
	}
	public void setEndLatLng(LatLng endLatLng) {
		this.endLatLng = endLatLng;
	}
	
	
	
}
