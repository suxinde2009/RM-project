package com.jan.rm.sns.entity;

import java.io.Serializable;

public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 440041109891863588L;
	
	private String userId;
	private String userName;
	private String avatarImagePath;
	private int statusesCount;
	private String description;
	
	private String gender;
	
	private int followerCount;
	private int idolCount;
	
	private String platform;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAvatarImagePath() {
		return avatarImagePath;
	}
	public void setAvatarImagePath(String avatarImagePath) {
		this.avatarImagePath = avatarImagePath;
	}
	public int getStatusesCount() {
		return statusesCount;
	}
	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getFollowerCount() {
		return followerCount;
	}
	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}
	public int getIdolCount() {
		return idolCount;
	}
	public void setIdolCount(int idolCount) {
		this.idolCount = idolCount;
	}
	
	public String getPlatform(){
		return platform;
	}
	
	public void setPlatform(String platform){
		this.platform = platform;
	}
}
