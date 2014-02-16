package com.jan.rm.utils;

import java.util.List;

import com.jan.rm.err.FeatureException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;

public class LightUtil {
	
	private static LightUtil lightUtil;
	
	private Context context;
	
	private Camera camera;
	
	private boolean lightOn;
	private boolean flashLightAvailable;
	
	private int cameraCount;
	private Camera.CameraInfo cameraInfo;
	
	private String flashOnMode;
	
	private List<long[]> sosList;
	private int sosListIterate;
	
	private Handler handler;
	private TimeRunnable timeRunnable;
	private long millisSeconds;
	
	private class TimeRunnable implements Runnable{
		Camera.Parameters params;

		public TimeRunnable(){
			params = camera.getParameters();
		}
		
		@Override
		public void run() {
			long[] i = sosList.get(sosListIterate);
			if(i[0] == 1){
				params.setFlashMode(flashOnMode);
				camera.setParameters(params);
				camera.startPreview();
			}else{
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				camera.setParameters(params);
				camera.stopPreview();
			}
			
			handler.postDelayed(this, 1);
			if(System.currentTimeMillis() - millisSeconds >= i[1]){
				if(sosListIterate >= sosList.size() - 1){
					sosListIterate = 0;
				}else{
					sosListIterate++;
				}
				millisSeconds = System.currentTimeMillis();
			}
		}
		
	};

	public LightUtil(Context context){
		this.context = context;
		
		flashLightAvailable = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		
		cameraCount = Camera.getNumberOfCameras();
		cameraInfo = new Camera.CameraInfo();
		
		lightOn = false;
	}
	
	public static LightUtil getInstance(Context context){
		if(lightUtil == null){
			lightUtil = new LightUtil(context);
		}
		
		return lightUtil;
	}
	
	public boolean toggle(){
		
		lightOn = !lightOn;
		Camera.Parameters params = camera.getParameters();
		if(lightOn){
			params.setFlashMode(flashOnMode);
			camera.setParameters(params);
			camera.startPreview();
		}else{
			params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			
			if(handler != null){
				handler.removeCallbacks(timeRunnable);
				handler = null;
			}
		}
		
		
		
		return lightOn;
	}
	
	public void sosOn(){
		lightOn = true;
		
		if(handler == null){
			sosListIterate = 0;
			sosList = MorseUtil.lettersToMorseLong("SOS", 500L, 150L, 100L, 300L, 1000L);
			handler = new Handler();
			timeRunnable = new TimeRunnable();
			millisSeconds = System.currentTimeMillis();
			handler.post(timeRunnable);
		}
	}
	
	public boolean isLightOn(){
		return lightOn;
	}
	
	public boolean isSosOn(){
		if(handler != null){
			return true;
		}
		
		return false;
	}
	
	public void onResume(){
		if(!flashLightAvailable) new FeatureException(context, FeatureException.FEATURE_FLASH_LIGHT_UNAVAILABLE);
		
		for(int i = 0; i < cameraCount; i++){
			Camera.getCameraInfo(i, cameraInfo);
			if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
				try{
					camera = Camera.open(i);
					if(flashOnMode == null){
						Camera.Parameters params = camera.getParameters();
						if(params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH)){
							flashOnMode = Camera.Parameters.FLASH_MODE_TORCH;
						}else{
							flashOnMode = Camera.Parameters.FLASH_MODE_ON;
						}
					}
				}catch(RuntimeException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public void onPause(){
		camera.release();
		lightOn = false;
		
		if(handler != null){
			handler.removeCallbacks(timeRunnable);
		    handler = null;
		}
	}
}
