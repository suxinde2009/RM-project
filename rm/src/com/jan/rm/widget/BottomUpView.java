package com.jan.rm.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/*
 *   _    ________    __    __    ________    ________    __    __    ________    ___   __ _
 *  /\\--/\______ \--/\ \--/\ \--/\  _____\--/\  ____ \--/\ \--/\ \--/\  _____\--/\  \-/\ \\\
 *  \ \\ \/_____/\ \ \ \ \_\_\ \ \ \ \____/_ \ \ \__/\ \ \ \ \_\ \ \ \ \ \____/_ \ \   \_\ \\\
 *   \ \\       \ \ \ \ \  ____ \ \ \  _____\ \ \  ____ \ \_ \ \_\ \  \ \  _____\ \ \  __   \\\
 *    \ \\       \ \ \ \ \ \__/\ \ \ \ \____/_ \ \ \__/\ \  \_ \ \ \   \ \ \____/_ \ \ \ \_  \\\
 *     \ \\       \ \_\ \ \_\ \ \_\ \ \_______\ \ \_\ \ \_\   \_ \_\    \ \_______\ \ \_\_ \__\\\
 *      \ \\       \/_/  \/_/  \/_/  \/_______/  \/_/  \/_/     \/_/     \/_______/  \/_/ \/__/ \\
 *       \ \\----------------------------------------------------------------------------------- \\
 *        \//                                                                                   \//
 *
 * 
 *
 */

public class BottomUpView extends ViewGroup{
	
	private Context context;
	
	private int widthMeasureSpec;
	private int childHeight;
	private int height;
	
	private View child;
	
	private boolean isDataChanged;
	private int resourceId;
	
	private float offset;
	private int dest;
	
	private Handler handler;
	private Runnable performRunnable  = new Runnable(){
		@Override
		public void run(){
			offset += (dest - offset) * 0.4F;
			
			requestLayout();
			
			if(Math.abs(offset - dest) >= 1) handler.postDelayed(this, 20);
		}
	};

	public BottomUpView(Context context){
		this(context, null);
	}
	
	public BottomUpView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public BottomUpView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		this.context = context;
		offset = 0;
		
		isDataChanged = false;
	}
	
	public void inflateChild(int resourceId){
		isDataChanged = true;
		this.resourceId = resourceId;
		
		requestLayout();
	}
	
	private void measureChild(View view){
		LayoutParams params = view.getLayoutParams();
		int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), params.width);
		
		int childHeightSpec;
		if(params.height > 0){
			childHeightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
		}else{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		
		view.measure(childWidthSpec, childHeightSpec);
	}
	
	private LayoutParams getChildLayoutParams(View child){
		LayoutParams params = child.getLayoutParams();
		if(params == null){
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		
		return params;
	}
	
	private void layoutChild(){
		if(child != null){
			int left = getPaddingLeft();
			int right = getMeasuredWidth() - getPaddingRight();
			int top = (int) offset;
			int bottom = childHeight + top;
			
			child.layout(left, top, right, bottom);
		}
	}
	
	public void performHide(){
		dest = height;
		if(handler == null) handler = new Handler();
		handler.post(performRunnable);
	}
	
	public void performShow(){
		dest = height - childHeight;
		if(handler == null) handler = new Handler();
		handler.post(performRunnable);
	}
	
	public View getContent(){
		return child;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		this.widthMeasureSpec = widthMeasureSpec;
		height = MeasureSpec.getSize(heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if(isDataChanged){
			this.removeAllViewsInLayout();
			
			LayoutInflater inflater = LayoutInflater.from(context);
			child = inflater.inflate(resourceId, null, false);
			
			this.addViewInLayout(child, 0, getChildLayoutParams(child));
			measureChild(child);
			
			if(child.getBackground() == null){
				child.setBackgroundColor(0xEEFFFFFF);
			}
			
			childHeight = child.getMeasuredHeight();
			offset = height;
			
			isDataChanged = false;
		}
		
		if(this.getChildCount() > 0) layoutChild();
	}
}
