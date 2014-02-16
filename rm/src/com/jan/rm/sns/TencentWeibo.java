package com.jan.rm.sns;

import java.util.HashMap;

import com.jan.rm.sns.entity.Gender;
import com.jan.rm.sns.entity.Status;
import com.jan.rm.sns.entity.User;

public class TencentWeibo extends SNS{

	@Override
	public String updateStatus(Status status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User parseUserInfo(HashMap<String, Object> userInfo) {
		User user = new User();
		
		user.setUserId((String) userInfo.get("openid"));
		user.setUserName((String) userInfo.get("nick"));
		user.setAvatarImagePath((String) userInfo.get("head") + "/100");
		user.setStatusesCount((Integer) userInfo.get("tweetnum"));
		user.setDescription((String) userInfo.get("introduction"));
		user.setGender(parseGender((Integer) userInfo.get("sex")));
		user.setFollowerCount((Integer) userInfo.get("fansnum"));
		user.setIdolCount((Integer) userInfo.get("idolnum"));
		
		return user;
	}

	private String parseGender(int gender){
		String result = null;
		
		switch(gender){
		case 0:
			result = Gender.NOT_SPECIFIC;
			break;
		case 1:
			result = Gender.MALE;
			break;
		case 2:
			result = Gender.FEMALE;
			break;
		}
		
		return result;
	}
}
