package com.jan.rm.dao;

import org.json.JSONException;
import org.json.JSONObject;

import com.jan.rm.entity.rm.Situation;
import com.jan.rm.entity.rm.RMUser;


public class RmServerApi {
	
	private class UserTable{
		public static final String TABLE_NAME = "tb_user";
		
		public static final String USER_ID = "user_id";
		public static final String PASSWORD = "password";
		public static final String USER_NAME = "user_name";
		public static final String EMAIL = "email";
		public static final String PHONE_NUMBER = "phone_number";
		public static final String GENDER = "sex";
		public static final String BIRTH = "birth";
		public static final String LEVEL = "level";
		public static final String SEARCH_RANGE = "search_meters";
		public static final String UPDATE_TIME = "update_time";
		public static final String CREATE_TIME = "create_time";
		public static final String MODIFY_NAME = "modify_name";
		public static final String TIME_STAMP = "time_stamp";
	}
	
	private class SuggestTable{
		public static final String TABLE_NAME = "tb_suggest";
		public static final String USER_ID = "user_id";
		public static final String SUBMIT_TIME = "submit_time";
		public static final String CONTENT = "content";
		public static final String REMARK = "remark";
		public static final String CREATE_TIME = "create_time";
		public static final String MODIFY_NAME = "modify_name";
		public static final String TIME_STAMP = "time_stamp";
	}
	
	private class SituationTable{
		public static final String TABLE_NAME = "tb_pa_exception";
		
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String lEVEL = "level";
		public static final String CAT_ID = "cat_id";
		public static final String ORDER = "order_";
		public static final String CREATE_TIME = "create_time";
		public static final String MODIFY_NAME = "modify_name";
		public static final String TIME_STAMP = "time_stamp";
	}
	
	private class UserPublishTable{
		public static final String TABLE_NAME = "tb_exception";
		
		public static final String AKEY = "akey";
		public static final String USER_ID = "user_id";
		public static final String SUBMIT_TIME = "submit_time";
		public static final String MINUTES = "minutes";
		public static final String LATITUDE = "lat";
		public static final String LONGITUDE = "lng";
		public static final String EXCEPTION_ID = "exception_id";
		public static final String CONTENT = "exception_name";
		public static final String CONFIRM = "confirm";
		public static final String RELIEVE = "relieve";
		public static final String LEVEL = "level";
		public static final String TIME_STAMP = "time_stamp";
	}
	
	private class PublishRecordTable{
		public static final String TABLE_NAME = "tb_exception_record";
		
		public static final String USER_ID = "user_id";
		public static final String OP_TIME = "op_time";
		public static final String TYPE = "type";
		public static final String EXCEPTION_AKEY = "exception_akey";
	}

	public static String verifyAccount(String userId, String password){
		
		return String.format("SELECT * FROM %1$s WHERE (%2$s='%4$s' OR %3$s='%4$s') AND %5$s='%6$s';", UserTable.TABLE_NAME, UserTable.USER_ID, UserTable.EMAIL, userId, UserTable.PASSWORD, password);
	}
	
	public static String getSituation(double latitude, double longitude, int range){
		
		String result = null;
		
		if(range == -1){
			result = String.format("SELECT * FROM %1$s WHERE %2$s > 0 ORDER BY %2$s ASC;", UserPublishTable.TABLE_NAME, UserPublishTable.AKEY);
		}else{
			float rangeInlatlng = rangeToLatlng(range);
			
			result = String.format("SELECT * FROM %1$s WHERE %2$s > %4$f-%6$f AND %2$s < %4$f+%6$f AND %3$s > %5$f-%6$f AND %3$s < %5$f+%6$f ORDER BY %7$s ASC;", UserPublishTable.TABLE_NAME, UserPublishTable.LATITUDE, UserPublishTable.LONGITUDE, latitude, longitude, rangeInlatlng, UserPublishTable.AKEY);
		}
		
		return result;
	}
	
	public static String sendSituation(double latitude, double longitude, String content, int level, String userId){
		return String.format("INSERT INTO %1$s (%2$s, %3$s, %4$s, %5$s, %6$s, %7$s, %8$s, %9$s, %10$s) values ('%11$s', now(), 30, %12$f, %13$f, '%14$s', 1, %15$s, now())",
				             UserPublishTable.TABLE_NAME, UserPublishTable.USER_ID, UserPublishTable.SUBMIT_TIME, UserPublishTable.MINUTES, UserPublishTable.LATITUDE, UserPublishTable.LONGITUDE,
				             UserPublishTable.CONTENT, UserPublishTable.CONFIRM, UserPublishTable.LEVEL, UserPublishTable.TIME_STAMP, userId, latitude, longitude,
				             content, level);
	}
	
	public static String updateConfirm(long akey){
		return String.format("UPDATE %1$s SET %2$s=%2$s+1 WHERE %3$s=%4$d;", UserPublishTable.TABLE_NAME, UserPublishTable.CONFIRM, UserPublishTable.AKEY, akey);
	}
	
	public static String updateRelieve(long akey){
		return String.format("UPDATE %1$s SET %2$s=%2$s+1 WHERE %3$s=%4$d;", UserPublishTable.TABLE_NAME, UserPublishTable.RELIEVE, UserPublishTable.AKEY, akey);
	}
	
	public static String getProfile(String userId){
		return String.format("SELECT * FROM %1$s WHERE %2$s='%3$s';", UserTable.TABLE_NAME, UserTable.USER_ID, userId);
	}
	
	public static String getPublishCount(String userId){
		return String.format("SELECT COUNT(*) AS publishCount FROM %1$s WHERE %2$s='%3$s';", UserPublishTable.TABLE_NAME, UserPublishTable.USER_ID, userId);
	}
	
	public static String getRecordCount(String userId){
		return String.format("SELECT COUNT(*) AS confirmCount FROM %1$s WHERE %2$s='%3$s' AND %4$s=1 UNION SELECT COUNT(*) AS dispelCount FROM %1$s WHERE %2$s='%3$s' AND %4$s=2;", PublishRecordTable.TABLE_NAME, PublishRecordTable.USER_ID, userId, PublishRecordTable.TYPE);
	}
	
	private static float rangeToLatlng(int range){
		return range / 10F;
	}

	public static RMUser parseJSONForUser(JSONObject jsonObject){
		RMUser user = new RMUser();
		
		try{
			user.setUserId(jsonObject.getString(UserTable.USER_ID));
			user.setUserName(jsonObject.getString(UserTable.USER_NAME));
			user.setBirth(jsonObject.getString(UserTable.BIRTH));
			user.setPassword(jsonObject.getString(UserTable.PASSWORD));
			user.setEmail(jsonObject.optString(UserTable.EMAIL));
			user.setPhoneNumber(jsonObject.optInt(UserTable.PHONE_NUMBER));
			user.setGender(jsonObject.getString(UserTable.GENDER));
			user.setLevel(jsonObject.getInt(UserTable.LEVEL));
			user.setTimeStamp(jsonObject.optString(UserTable.TIME_STAMP));
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return user;
	}
	
	public static Situation parseJSONForSituation(JSONObject jsonObject){
		Situation situation = new Situation();
		
		try{
			situation.setAKey(jsonObject.getInt(UserPublishTable.AKEY));
			situation.setUserId(jsonObject.getString(UserPublishTable.USER_ID));
			situation.setLatitude(jsonObject.getDouble(UserPublishTable.LATITUDE));
			situation.setLongitude(jsonObject.getDouble(UserPublishTable.LONGITUDE));
			situation.setContent(jsonObject.getString(UserPublishTable.CONTENT));
			situation.setConfirmed(jsonObject.getInt(UserPublishTable.CONFIRM));
			situation.setRelieved(jsonObject.optInt(UserPublishTable.RELIEVE));
			situation.setLevel(jsonObject.optInt(UserPublishTable.LEVEL));
			situation.setSubmitTime(jsonObject.getString(UserPublishTable.SUBMIT_TIME));
			situation.setExceptionId(jsonObject.optInt(UserPublishTable.EXCEPTION_ID));
			
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		return situation;
	}
}
