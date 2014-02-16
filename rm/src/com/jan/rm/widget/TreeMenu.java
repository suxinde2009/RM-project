package com.jan.rm.widget;

import java.util.ArrayList;
import java.util.List;

import com.jan.rm.R;
import com.jan.rm.adapter.TreeMenuAdapter;
import com.jan.rm.dao.ds.ActionPair;
import com.jan.rm.logger.RLog;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TreeMenu extends ViewGroup {
	
	private final int MIN_EDGE = 20;
	private final int MIN_VELOCITY;
	
	private ActionPair treeActionPair;
	private ActionPair cursorActionPair;
	
	private int width;
	private int height;
	
	private int x;
	
	private View frontView;
	private View backView;
	private ListView frontListView;
	private ListView backListView;
	private List<ActionPair> frontDatas;
	private List<ActionPair> backDatas;
	private TreeMenuAdapter frontAdapter;
	private TreeMenuAdapter backAdapter;
	
	private float frontViewOffset;
	
	private VelocityTracker velocityTracker = VelocityTracker.obtain();
	
	private boolean isDataChanged;
	
	private int shadowWidth;
	private float fadeDegree;
	private float shadowDegree;
	private Drawable shadowDrawable;
	
	private int minEdge;
	
	private Paint paint;
	
	private boolean hasParent;
	private boolean slidable;
	
	private Handler handler;
	private int destinationX;
	
	private Runnable flingRunnable = new Runnable(){
		@Override
		public void run(){
			if(Math.abs(frontViewOffset - destinationX) > 1){
				frontViewOffset += ((float) destinationX - frontViewOffset) * 0.13F;
				requestLayout();
				invalidate();
				
				if(onSlideListener != null) onSlideListener.onSlide(frontViewOffset / width);
				
				handler.postDelayed(this, 16);
			}else if(frontViewOffset >= width - 1){
				
				if(cursorActionPair.getParent() != null){
					RLog.d("cursorActionPair", "switch");
					cursorActionPair = cursorActionPair.getParent();
					if(cursorActionPair.getParent() == null) hasParent = false;
				}
				
				frontViewOffset = 0;
				destinationX = 0;
				
				backDatas.clear();
				frontDatas.clear();
				
				frontDatas.addAll(cursorActionPair.getChildren());
				RLog.d("frontDatas", frontDatas.size() + "");
				if(cursorActionPair.getParent() != null){
					backDatas.addAll(cursorActionPair.getParent().getChildren());
				}else{
					backDatas.addAll(cursorActionPair.getChildren());
				}
				
				frontAdapter.notifyDataSetChanged();
				backAdapter.notifyDataSetChanged();
				
				requestLayout();
				invalidate();
			}
			
			if(Math.abs(frontViewOffset - destinationX) <= 1){
				slidable = true;
			}
		}
	};
	
	private Runnable enterChildRunnable = new Runnable(){
		@Override
		public void run(){
			if(Math.abs(frontViewOffset - 0) > 1){
				frontViewOffset += ((float) 0 - frontViewOffset) * 0.13F;
				requestLayout();
				invalidate();
				
				if(onSlideListener != null) onSlideListener.onSlide(frontViewOffset / width);
				
				handler.postDelayed(this, 16);
			}else{
				frontViewOffset = 0;
				destinationX = 0;
				slidable = true;
				
				requestLayout();
			}
		}
	};
	
	private OnSlideListener onSlideListener;
	private OnNodeEndListener onNodeEndListener;
	
	public interface OnSlideListener{
		public void onSlide(float offset);
	}
	
	public interface OnNodeEndListener{
		public void onNodeEnd(String title);
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View target, int position, long id){
			if(cursorActionPair.getChild(position).hasChild()){
				hasParent = true;
				backDatas.clear();
				frontDatas.clear();
				
				backDatas.addAll(cursorActionPair.getChildren());
				cursorActionPair = cursorActionPair.getChild(position);
				frontDatas.addAll(cursorActionPair.getChildren());
				
				frontAdapter.notifyDataSetChanged();
				backAdapter.notifyDataSetChanged();
				
				performEnterChild();
			}else{
				if(onNodeEndListener != null) onNodeEndListener.onNodeEnd(cursorActionPair.getChild(position).getTitle());
			}
		}
	};
	
	public TreeMenu(Context context){
		this(context, null);
	}
	
	public TreeMenu(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public TreeMenu(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		setFocusable(true);
		setWillNotDraw(true);
		
		isDataChanged = true;
		frontViewOffset = 0;
		
		fadeDegree = 0.2F;
		shadowDegree = 0.7F;
		shadowWidth = context.getResources().getDimensionPixelOffset(R.dimen.treemenu_shadow_width);
		shadowDrawable = context.getResources().getDrawable(R.drawable.shadow_treemenu);
		paint = new Paint();
		
		minEdge = (int) (context.getResources().getDisplayMetrics().density * MIN_EDGE + 0.5F);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		frontView = inflater.inflate(R.layout.view_treemenu_node_has_child, null, false);
		backView = inflater.inflate(R.layout.view_treemenu_node_has_child, null, false);
		
		frontView.setFocusable(true);
		backView.setFocusable(true);
		
		frontDatas = new ArrayList<ActionPair>();
		backDatas = new ArrayList<ActionPair>(); 
		
		frontAdapter = new TreeMenuAdapter(context, frontDatas);
		backAdapter = new TreeMenuAdapter(context, backDatas);
		
		hasParent = false;
		slidable = true;
		
		this.setBackgroundColor(0xFF000000);
		
		ViewConfiguration config = ViewConfiguration.get(context);
		MIN_VELOCITY = config.getScaledMinimumFlingVelocity();
		
	}
	
	public void setActionPair(ActionPair actionPair){
		this.treeActionPair = actionPair;
		cursorActionPair = actionPair;
		
		isDataChanged = true;
		
		frontDatas.clear();
		backDatas.clear();
		
		frontDatas.addAll(treeActionPair.getChildren());
		backDatas.addAll(treeActionPair.getChildren());
		
		if(frontAdapter != null && backAdapter != null){
			frontAdapter.notifyDataSetChanged();
			backAdapter.notifyDataSetChanged();
		}
		
		requestLayout();
	}
	
	public ActionPair getActionPair(){
		return treeActionPair;
	}
	
	public String getCursorTitle(){
		return cursorActionPair.getTitle();
	}
	
	private ViewGroup.LayoutParams getChildLayoutParams(View child){
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if(params == null){
			params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}
		
		return params;
	}
	
	private void measureChild(View child){

        child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
	}
	
	private void addAndMeasure(View child, int position){
		addViewInLayout(child, position, getChildLayoutParams(child), true);
		measureChild(child);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(isDataChanged){
			removeAllViewsInLayout();
			
			addAndMeasure(backView, 0);
			addAndMeasure(frontView, 1);
			
			frontListView = (ListView) frontView.findViewById(R.id.list);
			backListView = (ListView) backView.findViewById(R.id.list);
			
			frontListView.setOnItemClickListener(onItemClickListener);
			
			if(treeActionPair != null){
				frontDatas.clear();
				backDatas.clear();
				
				frontDatas.addAll(treeActionPair.getChildren());
				backDatas.addAll(treeActionPair.getChildren());
			}
			
			frontListView.setAdapter(frontAdapter);
			backListView.setAdapter(backAdapter);
			
			isDataChanged = false;
		}
		
		if(frontView != null && backView != null){
			//int edgeOffset = (int) ((width - frontViewOffset) / 15);
			//backView.layout(edgeOffset, edgeOffset, width - edgeOffset, height - edgeOffset);
			backView.layout(0, 0, width, height);
			frontView.layout((int) frontViewOffset, 0, (int) (frontViewOffset + width), height);
		}
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event){
		if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN && event.getX() <= minEdge){
			return true;
		}else{
			return super.onInterceptTouchEvent(event);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		velocityTracker.addMovement(event);
		switch(event.getAction() & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			if(event.getX() <= minEdge && slidable) stopFling();
			x = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if(x <= minEdge && hasParent && slidable){
				frontViewOffset = (int) (event.getX() - x);
				requestLayout();
				invalidate();
				
				if(onSlideListener != null) onSlideListener.onSlide(frontViewOffset / width);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if(x <= minEdge && hasParent && slidable){
				velocityTracker.computeCurrentVelocity(1000);
				if(Math.abs(velocityTracker.getXVelocity()) < MIN_VELOCITY){
					if(frontViewOffset > width / 2){
						destinationX = width;
					}else{
						destinationX = 0;
					}
				}else{
					if(velocityTracker.getXVelocity() > 0){
						destinationX = width;
					}else{
						destinationX = 0;
					}
				}
				
				slidable = false;
				performFling();
				
				velocityTracker.clear();
			}
			break;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(hasParent && keyCode == KeyEvent.KEYCODE_BACK){
			if(slidable){
				destinationX = width;
				performFling();
			}
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
		
	}
	
	private void performFling(){
		if(handler == null) handler = new Handler();
		handler.removeCallbacks(enterChildRunnable);
		handler.post(flingRunnable);
	}
	
	private void performEnterChild(){
		if(handler == null) handler = new Handler();
		slidable = false;
		frontViewOffset = width;
		
		requestLayout();
		
		handler.removeCallbacks(flingRunnable);
		handler.post(enterChildRunnable);
	}
	
	private void stopFling(){
		if(handler != null) handler.removeCallbacks(flingRunnable);
	}
	
	public void setOnSlideListener(OnSlideListener onSlideListener){
		this.onSlideListener = onSlideListener;
	}
	
	public OnSlideListener getOnSlideListener(){
		return onSlideListener;
	}
	
	public void setOnNodeEndListener(OnNodeEndListener onNodeEndListener){
		this.onNodeEndListener = onNodeEndListener;
	}
	
	public OnNodeEndListener getOnNodeEndListener(){
		return onNodeEndListener;
	}
	
	private void drawShadow(View content, Canvas canvas, float openPercent) {
		if (shadowDrawable == null || shadowWidth <= 0) return;
		int left = 0;
		int alpha = (int) (shadowDegree * 255 * Math.abs(1-openPercent));
		if(alpha <= 20) alpha = 20;
		left = content.getLeft() - shadowWidth;
		shadowDrawable.setBounds(left, 0, left + shadowWidth, getHeight());
		shadowDrawable.setAlpha(alpha);
		shadowDrawable.draw(canvas);
	}

	private void drawFade(View content, Canvas canvas, float openPercent) {
		final int alpha = (int) (fadeDegree * 255 * Math.abs(1-openPercent));
		paint.setColor(alpha << 24 | 0x000000);
		int left = 0;
		int right = 0;
		left = (int) (content.getLeft() - frontViewOffset);
		right = content.getLeft();
		canvas.drawRect(left, 0, right, getHeight(), paint);
	}
	
	@Override
	public void dispatchDraw(Canvas canvas){
		super.dispatchDraw(canvas);
		float offset = ((float) frontViewOffset) / ((float) width);
		
		drawShadow(frontView, canvas, offset);
		drawFade(frontView, canvas, offset);
	}
}
