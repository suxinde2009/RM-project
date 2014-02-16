package com.jan.rm.sns.api;

import java.util.HashMap;

import com.jan.rm.sns.entity.User;

public interface UserAPI {

	public User parseUserInfo(HashMap<String, Object> userInfo);
}
