package com.jan.rm.widget;

import com.jan.rm.R;
import com.jan.rm.err.ExceptionHandler;
import com.jan.rm.err.FeatureException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class MiniCompassView extends View {
	
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
	
	private Drawable compassDrawable;
	
	private boolean isOrientationSensorAvailable;
	
	private OnDirectionChangeListener onDirectionChangeListener;
	
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

	public MiniCompassView(Context context){
		this(context, null);
	}
	
	public MiniCompassView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public MiniCompassView(Context context, AttributeSet attrs, int defStyle){
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
		
		mInterpolator = new AccelerateInterpolator();
		
		isOrientationSensorAvailable = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
		
		compassDrawable = context.getResources().getDrawable(R.drawable.ic_mini_compass);
		
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
		
		if(width == 0){
			if(MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY){
				width = height;
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
				setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			}
		}
		
		if(height == 0){
			if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY){
				height = width;
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
				setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			}
		}
		
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
		
		min = min / 2;

		compassDrawable.setBounds(centerPoint.x - min, centerPoint.y - min, centerPoint.x + min, centerPoint.y + min);
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
	public void onDraw(Canvas canvas){
		canvas.save();
		canvas.rotate(angle, centerPoint.x, centerPoint.y);
		compassDrawable.draw(canvas);
		canvas.restore();
	}
}
