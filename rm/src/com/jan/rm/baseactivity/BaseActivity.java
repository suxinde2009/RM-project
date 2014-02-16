package com.jan.rm.baseactivity;

import com.jan.rm.utils.ConnectionUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		if(!disableConnectionCheck()){
			ConnectionUtil.checkConnectionAndReport(this);
		}
	}
	
	protected boolean disableConnectionCheck(){
		return false;
	}
}
