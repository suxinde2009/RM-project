package com.jan.rm.sns;

import java.util.HashMap;

import com.jan.rm.sns.entity.Gender;
import com.jan.rm.sns.entity.Status;
import com.jan.rm.sns.entity.User;

public class Facebook extends SNS {
	
	private static final String FACEBOOK_GRAPH_API = "https://graph.facebook.com";

	@Override
	public String updateStatus(Status status) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public User parseUserInfo(HashMap<String, Object> userInfo) {
		User user = new User();
		
		user.setUserId((String) userInfo.get("id"));
		user.setUserName((String) userInfo.get("name"));
		user.setAvatarImagePath(parseImagePath((HashMap<String, Object>) userInfo.get("picture")));
		user.setDescription((String) userInfo.get("bio"));
		user.setGender(parseGender((String) userInfo.get("gender")));
		
		return user;
	}

	@SuppressWarnings("unchecked")
	private String parseImagePath(HashMap<String, Object> picture){
		return (String) ((HashMap<String, Object>) picture.get("data")).get("url");
	}
	
	private String parseGender(String gender){
		if(!gender.equals("male") && !gender.equals("female")) return Gender.NOT_SPECIFIC;
		
		return gender;
	}

	private String checkIns(Status status){
		//TODO facebook check-in mode (post location)
		return null;
	}
}
