package com.jan.rm.entity;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class PlaceSearchResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8649599145260117302L;

	private String id;
	private LatLng location;
	private String name;
	private float rating;
	private String icon;
	private String[] types;
	private String formatted_address;
	
	private String locality;
	private String subLocality;
	
	private String[] adminArea = new String[3];
	private String countryCode;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String[] getTypes() {
		return types;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public String getFormattedAddress() {
		return formatted_address;
	}
	public void setFormattedAddress(String formatted_address) {
		this.formatted_address = formatted_address;
	}
	
	public String getLocality(){
		return locality;
	}
	
	public void setLocality(String locality){
		this.locality = locality;
	}
	
	public String getSubLocality(){
		return subLocality;
	}
	
	public void setSubLocality(String subLocality){
		this.subLocality = subLocality;
	}
	
	public String[] getAdminArea(){
		return adminArea;
	}
	
	public void setAdminArea(int position, String adminArea){
		this.adminArea[position] = adminArea;
	}
	
	public String getCountryCode(){
		return countryCode;
	}
	
	public void setCountryCode(String countryCode){
		this.countryCode = countryCode;
	}
}
