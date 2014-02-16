package com.jan.rm.sns.entity;

import java.io.Serializable;

public class Status implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9007534810597620126L;

	private String content;
	
	private String[] imagesPath;
	private String videoPath;
	
	private double latitude;
	private double longitude;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String[] getImagesPath() {
		return imagesPath;
	}
	public void setImagesPath(String[] imagesPath) {
		this.imagesPath = imagesPath;
	}
	public String getVideoPath() {
		return videoPath;
	}
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	
}
