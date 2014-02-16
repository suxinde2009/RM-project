package com.jan.rm.entity.rm;

import java.io.Serializable;



public class Situation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5075095739249554017L;

	private long aKey;
	private String userId;
	private String content;
	private String submitTime;
	private long updateTime;
	
	private double latitude;
	private double longitude;
	
	private int confirmed;
	private int relieved;
	private int level;
	
	private int exceptionId;
	
	public void setAKey(long aKey){
		this.aKey = aKey;
	}
	
	public long getAKey(){
		return aKey;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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

	public int getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}

	public int getRelieved() {
		return relieved;
	}

	public void setRelieved(int relieved) {
		this.relieved = relieved;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}

	public int getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(int exceptionId) {
		this.exceptionId = exceptionId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
