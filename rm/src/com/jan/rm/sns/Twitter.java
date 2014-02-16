package com.jan.rm.sns;

import java.util.HashMap;

import com.jan.rm.sns.entity.Gender;
import com.jan.rm.sns.entity.Status;
import com.jan.rm.sns.entity.User;

public class Twitter extends SNS{

	@Override
	public String updateStatus(Status status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User parseUserInfo(HashMap<String, Object> userInfo) {
		User user = new User();
		
		user.setUserId(((Integer) userInfo.get("id")) + "");
		user.setUserName((String) userInfo.get("name"));
		user.setAvatarImagePath((String) userInfo.get("profile_image_url"));
		user.setStatusesCount((Integer) userInfo.get("statuses_count"));
		user.setDescription((String) userInfo.get("description"));
		user.setGender(Gender.NOT_SPECIFIC);
		user.setFollowerCount((Integer) userInfo.get("followers_count"));
		user.setIdolCount((Integer) userInfo.get("friends_count"));
		
		return user;
	}
}
