package com.jan.rm.widget;

import com.jan.rm.err.FeatureException;
import com.jan.rm.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateInterpolator;

public abstract class BaseCompassView extends SurfaceView implements SurfaceHolder.Callback{
	
	private Context context;
	
	private final boolean useNewTheory = false;
	
	private final float MAX_ROTATE_DEGREE = 1.0F;
	private float mDirection;
	private Handler mHandler = new Handler();
	private AccelerateInterpolator mInterpolator;
	boolean mStopDrawing;
	private float mTargetDirection;
	
	private Sensor orientationSensor;
	private Sensor accelerometerSensor;
	private Sensor magneticSensor;
	
	private SensorManager sensorManager;

	protected float angle;
	
	private float[] mGravity;
	private float[] mGeomagnetic;

	protected int width, height;
	protected Point centerPoint;
	
	private int gravity;
	
	protected SurfaceHolder surfaceHolder;
	private boolean runThread;
	
	private boolean isOrientationSensorAvailable;
	
	private OnDirectionChangeListener onDirectionChangeListener;
	
	private Thread thread = new Thread(){
		@Override
		public void run(){
			while(runThread){
				if(!mStopDrawing){
					Canvas canvas = surfaceHolder.lockCanvas();
					canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					onThreadDraw(canvas);
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	};
	
	private SensorEventListener sensorEventListener = new SensorEventListener(){

		@Override
		public void onSensorChanged(SensorEvent event) {
	        
	        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
	        	mGravity = event.values;
	        }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
	        	mGeomagnetic = event.values;
	        }
	        
	        if(mGravity != null && mGeomagnetic != null){
	        	float R[] = new float[9];
	        	float I[] = new float[9];
	        	
	        	boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
	        	if(success){
	        		float orientation[] = new float[3];
	        		SensorManager.getOrientation(R, orientation);
	        		mTargetDirection = -orientation[0] * 360 / (6.28318F);
	        	}
	        }
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
	};
	
	private SensorEventListener deprecatedSensorEventListener = new SensorEventListener(){
		@Override
		public void onSensorChanged(SensorEvent event){
			float direction = event.values[0] * -1.0F;
			mTargetDirection = normalizeDegree(direction);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};

	private Runnable rotateRunnable = new Runnable() {
        @Override
        public void run() {
        	if (mDirection != mTargetDirection && !mStopDrawing) {

                // calculate the short routine
                float to = mTargetDirection;
                if (to - mDirection > 180) {
                    to -= 360;
                } else if (to - mDirection < -180) {
                    to += 360;
                }

                // limit the max speed to MAX_ROTATE_DEGREE
                float distance = to - mDirection;
                if (Math.abs(distance) > MAX_ROTATE_DEGREE) {
                    distance = distance > 0 ? MAX_ROTATE_DEGREE : (-1.0f * MAX_ROTATE_DEGREE);
                }

                // need to slow down if the distance is short
                mDirection = normalizeDegree(mDirection + ((to - mDirection) * mInterpolator.getInterpolation(Math.abs(distance) > MAX_ROTATE_DEGREE ? 0.4f : 0.3f)));
                setAngle(mDirection);
            }

            mHandler.postDelayed(this, 20);
        }
    };
    
    public interface OnDirectionChangeListener{
    	public void onDirectionChanged(float angle);
    }
	
	public BaseCompassView(Context context) {
		this(context, null);
	}

	public BaseCompassView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressWarnings("deprecation")
	public BaseCompassView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		angle = 0.0F;
		
		centerPoint = new Point();

		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
		if(useNewTheory){
			accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}else{
			orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseCompassView);
		
		gravity = ta.getInt(R.styleable.BaseCompassView_android_gravity, Gravity.CENTER);
		
		ta.recycle();
		
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		
		setZOrderOnTop(true);
		surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
		
		runThread = true;
		
		mInterpolator = new AccelerateInterpolator();
		
		isOrientationSensorAvailable = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);

		this.context = context;
	}
	
	public void onResume(){
		if(!isOrientationSensorAvailable) new FeatureException(context, FeatureException.FEATURE_COMPASS_UNAVAILABLE);
		if(accelerometerSensor != null && magneticSensor != null){
			sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(sensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		if(orientationSensor != null) sensorManager.registerListener(deprecatedSensorEventListener, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
		mStopDrawing = false;
		mHandler.post(rotateRunnable);
	}
	
	public void onPause(){
		mStopDrawing = true;
		if(accelerometerSensor != null || magneticSensor != null) sensorManager.unregisterListener(sensorEventListener);
		if(orientationSensor != null) sensorManager.unregisterListener(deprecatedSensorEventListener);
		mHandler.removeCallbacks(rotateRunnable);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		
		int min = width > height ? height : width;
		
		switch(gravity){
		case Gravity.CENTER:
			centerPoint.x = width / 2;
			centerPoint.y = height / 2;
			break;
		case Gravity.TOP:
			centerPoint.x = width / 2;
			centerPoint.y = min / 2;
			break;
		case Gravity.BOTTOM:
			centerPoint.x = width / 2;
			centerPoint.y = height - min / 2;
			break;
		case Gravity.LEFT:
			centerPoint.x = min / 2;
			centerPoint.y = height / 2;
			break;
		case Gravity.RIGHT:
			centerPoint.x = width - min / 2;
			centerPoint.y = height / 2;
		}

		
	}

	public void setAngle(float angle) {
		this.angle = angle;
		invalidate();
		if(onDirectionChangeListener != null) onDirectionChangeListener.onDirectionChanged(360 - angle);
	}
	
	public float getAngle(){
		return angle;
	}
	
	public void setOnDirectionChangeListener(OnDirectionChangeListener onDirectionChangeListener){
		this.onDirectionChangeListener = onDirectionChangeListener;
	}
	
	public OnDirectionChangeListener getOnDirectionChangeListener(){
		return onDirectionChangeListener;
	}
	
	private float normalizeDegree(float degree) {
	    return (degree + 720) % 360;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try{
			thread.start();
		}catch(IllegalThreadStateException e){
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		runThread = false;
	}
	
	
	protected abstract void onThreadDraw(Canvas canvas);

}