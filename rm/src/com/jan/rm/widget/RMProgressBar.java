package com.jan.rm.widget;

import com.jan.rm.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class RMProgressBar extends View {
	
	private int viewWidth;
	private int viewHeight;
	
	private Paint paint;
	private Shader moveShader;
	private RectF rect;
	private Matrix matrix;
	
	private boolean growDirection;
	private float growOffset;
	
	private int progressBarHeight;
	
	private int color;

	public RMProgressBar(Context context){
		this(context, null);
	}
	
	public RMProgressBar(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public RMProgressBar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rect = new RectF();
		matrix = new Matrix();
		
		growDirection = false;
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RMProgressBar);
		
		color = ta.getColor(R.styleable.RMProgressBar_android_color, 0x0);
		
		ta.recycle();
		
        progressBarHeight = context.getResources().getDimensionPixelOffset(R.dimen.rmprogressbar_height);
		
		moveShader = new LinearGradient(0, viewHeight - progressBarHeight - 1, progressBarHeight * 2, viewHeight + progressBarHeight, new int[]{0x90FFFFFF, 0x90FFFFFF, 0x00000000,0x00000000}, new float[]{0F, 0.5F, 0.5F, 1F}, Shader.TileMode.REPEAT);
		
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		if(heightMeasureSpec < progressBarHeight){
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(progressBarHeight, MeasureSpec.EXACTLY);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			super.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		}else{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		
		rect.left = 0;
		rect.top = 0;
		rect.right = viewWidth;
		rect.bottom = viewHeight;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		paint.setColor(color);
		
		canvas.drawRect(rect, paint);
		growOffset = rect.top;
		if(growDirection){
			growOffset -= 2F;
		}else{
			growOffset += 2F;
		}
		if(growOffset <= rect.top){
			growDirection = false;
		}else if(growOffset >= rect.bottom){
			growDirection = true;
		}
		matrix.postTranslate(0, -growOffset);
		moveShader.setLocalMatrix(matrix);
		paint.setShader(moveShader);
		
		canvas.drawRect(rect, paint);
		
		paint.setShader(null);
		
		invalidate();
	}
	
}

