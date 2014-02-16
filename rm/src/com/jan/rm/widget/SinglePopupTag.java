package com.jan.rm.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.jan.rm.R;

/**
 * SinglePopupTag for splash screen
 * 
 * @author 7heaven
 *
 */
public class SinglePopupTag extends View{
	
	private int width = -1;
	private int height = -1;
	
	private int drawableWidth;
	private int drawableHeight;
	
	private int centerX;
	private int centerY;
	
	private Drawable srcDrawable;
	
	private int mode;
	
	private final int MODE_LEFT_BEND = 0;
	private final int MODE_RIGHT_BEND = 1;
	
	private float a;
	private float scale;
	private final float intrinsicScale = 0.57F;
	
	private final int alpha = 0x66;
	
	private Handler handler;
	
	private Runnable initRunnable = new Runnable(){
		
		@Override
		public void run(){
			if(getVisibility()== View.INVISIBLE) setVisibility(View.VISIBLE); 
			a += (intrinsicScale - scale) * 0.1F;
			scale += a;
			a *= 0.81F;
			
			invalidate();
			
			if(a != 0){
				handler.postDelayed(this, 20);
			}else{
				handler.removeCallbacks(this);
			}
		}
	};
	
	public SinglePopupTag(Context context){
		this(context, null);
	}
	
	public SinglePopupTag(Context context, int width, int height){
		this(context, null);
		this.width = width;
		this.height = height;
	}
	
	public SinglePopupTag(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public SinglePopupTag(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SinglePopupTag);
		srcDrawable = ta.getDrawable(R.styleable.SinglePopupTag_drawable);
		mode = ta.getInt(R.styleable.SinglePopupTag_mode, ((int) Math.round(Math.random() * 10)) % 2 == 0 ? MODE_LEFT_BEND : MODE_RIGHT_BEND);
		
		ta.recycle();
		
		if(srcDrawable == null){
			int n = (int) Math.round(Math.random() * 4 + 0.5F);
			srcDrawable = context.getResources().getDrawable(context.getResources().getIdentifier(context.getPackageName() + ":drawable/icon_0" + n + "_24", null, null));
		}
		
		srcDrawable.setAlpha(alpha);
		
		scale = 0.1F;
		
		setVisibility(View.INVISIBLE);
	}
	
	public void init(){
		scale = 0.1F;
		setVisibility(View.INVISIBLE);
	}
	
	public void performInit(int delay){
		if(handler == null) handler = new Handler();
		handler.postDelayed(initRunnable, delay);
	}
	
	public void removeInit(){
		handler.removeCallbacks(initRunnable);
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		
		int drawableIntrinsicWidth = srcDrawable.getIntrinsicWidth();
		int drawableIntrinsicHeight = srcDrawable.getIntrinsicHeight();
		
		if(width / height > drawableIntrinsicWidth / drawableIntrinsicHeight){
			drawableWidth = width;
			drawableHeight = (int) (((float) width) / ((float) drawableIntrinsicWidth) * ((float) drawableIntrinsicHeight));
		}else{
			drawableHeight = height;
			drawableWidth = (int) (((float) height) / ((float) drawableIntrinsicHeight) * ((float) drawableIntrinsicWidth));
		}
		
		centerX = width / 2;
		centerY = height / 2;
		
		srcDrawable.setBounds(centerX - drawableWidth / 2, centerY - drawableHeight / 2, centerX + drawableWidth / 2, centerY + drawableHeight / 2);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.save();
		canvas.scale(scale, scale, centerX, centerY);
		canvas.rotate(mode == MODE_LEFT_BEND ? 45 : -45, centerX, centerY);
		srcDrawable.draw(canvas);
		canvas.restore();
	}
}
