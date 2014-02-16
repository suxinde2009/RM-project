package com.jan.rm.subactivity;

import com.jan.rm.R;
import com.jan.rm.baseactivity.BaseActivity;
import com.jan.rm.utils.LightUtil;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

public class FlashLightActivity extends BaseActivity{
	
	private ImageView toggle;
	private ImageView sos;
	
	private GestureDetector gestureDetector;
	
	private SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener(){

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(LightUtil.getInstance(FlashLightActivity.this).toggle()){
				toggle.setImageResource(R.drawable.light_on);
			}else{
				toggle.setImageResource(R.drawable.light_off);
				sos.setVisibility(View.GONE);
			}
			
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(!LightUtil.getInstance(FlashLightActivity.this).isSosOn()){
				toggle.setImageResource(R.drawable.light_on);
				sos.setVisibility(View.VISIBLE);
				LightUtil.getInstance(FlashLightActivity.this).sosOn();
			}
			return true;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flashlight);
		
		toggle = (ImageView) findViewById(R.id.toggle);
		sos = (ImageView) findViewById(R.id.sos);
		toggle.setImageResource(LightUtil.getInstance(this).isLightOn() ? R.drawable.light_on : R.drawable.light_off);
		
		gestureDetector = new GestureDetector(this, simpleOnGestureListener);
		
		toggle.setClickable(true);
		toggle.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
			
		});
		
		((Button) findViewById(R.id.back_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				FlashLightActivity.this.finish();
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		LightUtil.getInstance(this).onResume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		LightUtil.getInstance(this).onPause();
	}
}
