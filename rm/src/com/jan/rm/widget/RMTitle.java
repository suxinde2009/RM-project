package com.jan.rm.widget;

import com.jan.rm.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;

public class RMTitle extends View{
	
	private String text;
	private String upperText;
	
	private TextPaint textPaint;
	
	private int width;
	private int height;
	
	private int centerX;
	private int centerY;
	
	private StaticLayout layout;
	private StaticLayout upperLayout;
	
	private int offset;
	private int initOffset;
	private int layoutHeight;
	private int upperLayoutHeight;
	
	private boolean isInAction;
	private boolean isActionUp;
	
	private final float offsetPlus = 0.2F;

	public RMTitle(Context context){
		this(context, null);
	}
	
	public RMTitle(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public RMTitle(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RMTitle);
		
		textPaint.setColor(ta.getColor(R.styleable.RMTitle_android_textColor, context.getResources().getColor(R.color.rm_title_text_color)));
		textPaint.setTextSize(ta.getDimension(R.styleable.RMTitle_android_textSize, context.getResources().getDimension(R.dimen.rmtitle_text_size)));
		text = ta.getString(R.styleable.RMTitle_android_text);
		
		ta.recycle();
		
		width = -1;
		height = -1;
		
		offset = 0;
		
		isInAction = false;
		isActionUp = false;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		
		if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED){
			
			FontMetrics fm = new FontMetrics();
			textPaint.getFontMetrics(fm);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (fm.bottom - fm.top), MeasureSpec.EXACTLY);
			this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
			
			height = (int) (fm.bottom - fm.top);
		}
		
		
		
		centerX = width / 2;
		centerY = height / 2;

        doMeasureWork();
	}
	
	public void setText(String text){
		this.text = text;
		layout = null;
		doMeasureWork();
	}
	
	public String getText(){
		return text;
	}
	
	public void setUpperText(String text){
		if(upperText != null && !upperText.equals(text) && offset > initOffset + initOffset){
			this.text = upperText;
			
		    layout = null;
		}
			
		upperText = text;
		
		upperLayout = null;
		
		doMeasureWork();
		
		isInAction = true;
		isActionUp = true;
		invalidate();
	}
	
	public String getUpperText(){
		return upperText;
	}
	
	public void backward(){
		if(upperText != null && text != null){
			isInAction = true;
			isActionUp = false;
			invalidate();
		}
	}
	
	private void doMeasureWork(){
		if(width != -1 && height != -1){
			if(layout == null && text != null){
				layout = new StaticLayout(text, 0, text.length(), textPaint, width, Alignment.ALIGN_CENTER, 1.0F, 0.0F, true, TruncateAt.END, width);
				layoutHeight = layout.getHeight();
				
				initOffset = centerY - layoutHeight / 2;
				offset = initOffset;
			}
			
			if(upperLayout == null && upperText != null){
				upperLayout = new StaticLayout(upperText, 0, upperText.length(), textPaint, width, Alignment.ALIGN_CENTER, 1.0F, 0.0F, true, TruncateAt.END, width);
				upperLayoutHeight = upperLayout.getHeight();
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas){
		
		canvas.save();
		canvas.translate(0, offset);
		if(layout != null) layout.draw(canvas);
		canvas.translate(0, -upperLayoutHeight - initOffset);
		if(upperLayout != null) upperLayout.draw(canvas);
		canvas.restore();
		
		if(isInAction){
			if(isActionUp){
				if(offset < initOffset + initOffset + upperLayoutHeight){
					offset -= (offset - (initOffset + initOffset + upperLayoutHeight)) * offsetPlus;
				}else{
					offset = initOffset + initOffset + upperLayoutHeight;
					isInAction = false;
				}
			}else{
				if(offset > initOffset){
					offset -= (offset - initOffset) * offsetPlus;
				}else{
					offset = initOffset;
					isInAction = false;
				}
			}
			
			invalidate();
		}
	}
}
