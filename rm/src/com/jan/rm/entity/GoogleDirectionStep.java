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

public class GoogleDirectionStep {
	
	private String distanceText;
	private int distanceValue;
	
	private String durationText;
	private int durationValue;
	
	private LatLng startLocation;
	private LatLng endLocation;
	
	private String instruction;
	
	private List<LatLng> path;
	
	private String maneuver;
	private String travelMode;
	
	public String getDistanceText() {
		return distanceText;
	}
	public void setDistanceText(String distanceText) {
		this.distanceText = distanceText;
	}
	public int getDistanceValue() {
		return distanceValue;
	}
	public void setDistanceValue(int distanceValue) {
		this.distanceValue = distanceValue;
	}
	public String getDurationText() {
		return durationText;
	}
	public void setDurationText(String durationText) {
		this.durationText = durationText;
	}
	public int getDurationValue() {
		return durationValue;
	}
	public void setDurationValue(int durationValue) {
		this.durationValue = durationValue;
	}
	public LatLng getStartLocation() {
		return startLocation;
	}
	public void setStartLocation(LatLng startLocation) {
		this.startLocation = startLocation;
	}
	public LatLng getEndLocation() {
		return endLocation;
	}
	public void setEndLocation(LatLng endLocation) {
		this.endLocation = endLocation;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public List<LatLng> getPath() {
		return path;
	}
	public void setPath(List<LatLng> path) {
		this.path = path;
	}
	public String getManeuver() {
		return maneuver;
	}
	public void setManeuver(String maneuver) {
		this.maneuver = maneuver;
	}
	public String getTravelMode() {
		return travelMode;
	}
	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}
	
	
}
