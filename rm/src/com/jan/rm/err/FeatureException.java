package com.jan.rm.err;

import android.content.Context;

import com.jan.rm.R;

public class FeatureException {
	
	private String[] featureExceptionArray;

	public static final int FEATURE_FLASH_LIGHT_UNAVAILABLE = 1;
	public static final int FEATURE_COMPASS_UNAVAILABLE = 2;
	public static final int FEATURE_TELEPHONY_UNAVAILABLE = 3;

	public FeatureException(Context context, int featureType){
		featureExceptionArray = ExceptionHandler.getInstance(context).getContext().getResources().getStringArray(R.array.feature_exception_msg);
		ExceptionHandler.getInstance(context).alert("", featureExceptionArray[featureType]);
	}
	
}
