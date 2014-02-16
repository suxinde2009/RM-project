package com.jan.rm;

import com.jan.rm.baseactivity.BaseActivity;
import com.jan.rm.dao.RmPreferenceManager;
import com.jan.rm.err.ExceptionHandler;
import com.jan.rm.subactivity.LoginActivity;
import com.jan.rm.widget.SinglePopupTag;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class SplashActivity extends BaseActivity {
	
	private RelativeLayout backgroundLayout;
	
	private Handler handler;
	
	private AlertDialog dialog;
	
	private Runnable enterRunnable = new Runnable(){
		@Override
		public void run(){
			
			Intent intent;
			
			if(RmPreferenceManager.getInstance(SplashActivity.this).getUserId() != null &&
			   RmPreferenceManager.getInstance(SplashActivity.this).getPassword() != null){
				intent = new Intent(SplashActivity.this, GoogleMapActivity.class);
			}else{
				intent = new Intent(SplashActivity.this, LoginActivity.class);
			}
			
			
			startActivity(intent);
			SplashActivity.this.finish();
			
			/*
			dialog = new AlertDialog.Builder(SplashActivity.this).setPositiveButton("谷歌", new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(SplashActivity.this, GoogleMapActivity.class));
				}
			}).setNegativeButton("百度", new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(SplashActivity.this, BaiduMapActivity.class));
				}
				
			}).create();
			
			dialog.setOnDismissListener(new OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface dialog) {
					SplashActivity.this.finish();
				}
				
			});
			
			dialog.show();
			 */
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		backgroundLayout = (RelativeLayout) findViewById(R.id.background_layout);
		
		ExceptionHandler.getInstance(this);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();

		int count = backgroundLayout.getChildCount();
		View child;
		for(int i = 0; i < count; i++){
			child = backgroundLayout.getChildAt(i);
			if(child instanceof SinglePopupTag){
				((SinglePopupTag) child).init();
				((SinglePopupTag) child).performInit((int) (Math.random() * 10) * 200); 
			}
		}
		
		handler = new Handler();
		handler.postDelayed(enterRunnable, 3000);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		handler.removeCallbacks(enterRunnable);
		
		int count = backgroundLayout.getChildCount();
		View child;
		for(int i = 0; i < count; i++){
			child = backgroundLayout.getChildAt(i);
			if(child instanceof SinglePopupTag){
				((SinglePopupTag) child).removeInit();
			}
		}
	}

	@Override
	protected boolean disableConnectionCheck(){
		return true;
	}
}
