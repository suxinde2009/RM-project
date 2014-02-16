package com.jan.rm.dao;

import android.content.Context;
import android.content.SharedPreferences;

public class RmPreferenceManager {
	
	private static RmPreferenceManager preferenceManager;
	private SharedPreferences rmPreference;
	
	private final String PREFERENCE_NAME = "rm_sharedpreference";
	
	private final String ACCOUNT_USER_ID = "account_user_id";
	private final String ACCOUNT_PASSWORD = "account_password";
	
	private final String SETTINGS_SEARCH_RANGE = "settings_search_range";
	private final String SETTINGS_MILES_COUNT = "settings_miles_count";
	
	
	public RmPreferenceManager(Context context){
		if(rmPreference == null){
			rmPreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		}
	}
	
	public static RmPreferenceManager getInstance(Context context){
		if(preferenceManager == null){
			preferenceManager = new RmPreferenceManager(context);
		}
		
		return preferenceManager;
	}
	
	public void setSearchRange(int range){
		rmPreference.edit().putInt(SETTINGS_SEARCH_RANGE, range).commit();
	}
	
	public int getSearchRange(){
		return rmPreference.getInt(SETTINGS_SEARCH_RANGE, -1);
	}
	
	public void setMilesCount(long miles){
		rmPreference.edit().putLong(SETTINGS_MILES_COUNT, miles).commit();
	}
	
	public long getMilesCount(){
		return rmPreference.getLong(SETTINGS_MILES_COUNT, -1);
	}
	
	public void setUserId(String userId){
		rmPreference.edit().putString(ACCOUNT_USER_ID, userId).commit();
	}
	
	public String getUserId(){
		return rmPreference.getString(ACCOUNT_USER_ID, null);
	}
	
	public void setPassword(String password){
		rmPreference.edit().putString(ACCOUNT_PASSWORD, password).commit();
	}
	
	public String getPassword(){
		return rmPreference.getString(ACCOUNT_PASSWORD, null);
	}
}
