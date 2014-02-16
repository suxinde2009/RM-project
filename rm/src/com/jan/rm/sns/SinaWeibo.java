package com.jan.rm.sns;

import java.util.HashMap;

import com.jan.rm.sns.entity.Gender;
import com.jan.rm.sns.entity.Status;
import com.jan.rm.sns.entity.User;

public class SinaWeibo extends SNS {

	@Override
	public String updateStatus(Status status) {
		return null;
	}

	@Override
	public User parseUserInfo(HashMap<String, Object> userInfo) {
		User user = new User();
		
		user.setUserId(((Integer) userInfo.get("id")) + "");
		user.setUserName((String) userInfo.get("screen_name"));
		user.setAvatarImagePath((String) userInfo.get("avatar_large"));
		user.setStatusesCount((Integer) userInfo.get("statuses_count"));
		user.setGender(parseGender((String) userInfo.get("gender")));
		user.setIdolCount((Integer) userInfo.get("friends_count"));
		user.setFollowerCount((Integer) userInfo.get("followers_count"));
		user.setDescription((String) userInfo.get("description"));
		
		return user;
	}
	
	private String parseGender(String gender){
		if(gender.equals("m")){
			return Gender.MALE;
		}else if(gender.equals("f")){
			return Gender.FEMALE;
		}else{
			return Gender.NOT_SPECIFIC;
		}
	}

}
