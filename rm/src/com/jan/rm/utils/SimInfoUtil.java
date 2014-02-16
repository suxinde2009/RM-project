package com.jan.rm.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SimInfoUtil {
	
	public static String getPhoneNumber(Context context){
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		String result = null;
		
		try{
			result = telephonyManager.getLine1Number();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
