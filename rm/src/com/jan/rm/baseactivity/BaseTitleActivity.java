package com.jan.rm.baseactivity;

import com.jan.rm.R;
import com.jan.rm.widget.RMProgressBar;
import com.jan.rm.widget.RMTitle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class BaseTitleActivity extends BaseActivity{
	
	protected static final int TITLE_ONLY_TITLE_TEXT = R.layout.titlebar_title_text_only;
	protected static final int TITLE_WITH_NAV_BUTTON = R.layout.titlebar_title_nav_button;
	
	protected static final int BUTTON_MODE_LEFT_BUTTON = R.id.left_button;
	protected static final int BUTTON_MODE_RIGHT_BUTTON = R.id.right_button;
	protected static final int BUTTON_MODE_TWO_BUTTON = (BUTTON_MODE_LEFT_BUTTON << 16) | BUTTON_MODE_RIGHT_BUTTON;
	
	private LinearLayout titleBar;
	private LinearLayout content;
	private RMProgressBar progressBar;

	private RMTitle title;
	private ImageView leftButton;
	private ImageView rightButton;
	
	private int titleMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_title_base);
		
		titleBar = (LinearLayout) findViewById(R.id.title_bar);
		content = (LinearLayout) findViewById(R.id.content);
		progressBar = (RMProgressBar) findViewById(R.id.progressbar);
		
	}
	
	@Override
	public void setContentView(int layoutResID){
		if(content.getChildCount() > 0) content.removeAllViewsInLayout();
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(layoutResID, null, false);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);
		
		content.addView(view);
	}
	
	protected void setTitleView(int layoutResID, String title, OnClickListener onClickListener){
		if(titleBar.getChildCount() > 0) titleBar.removeAllViewsInLayout();
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(layoutResID, null, false);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);
		
		titleBar.addView(view);
		
		this.title = (RMTitle) titleBar.findViewById(R.id.title);
		
		if(layoutResID == TITLE_WITH_NAV_BUTTON){
			leftButton = (ImageView) titleBar.findViewById(R.id.left_button);
			rightButton = (ImageView) titleBar.findViewById(R.id.right_button);
			
			setOnNavButtonClickListener(onClickListener);
		}
		
		if(title != null) setTitleText(title);
		
		titleMode = layoutResID;
	}
	
	//only available for nav button titlebar
	protected void setButtonMode(int mode){
		switch(mode){
		case BUTTON_MODE_LEFT_BUTTON:
			rightButton.setVisibility(View.GONE);
			break;
		case BUTTON_MODE_RIGHT_BUTTON:
			leftButton.setVisibility(View.GONE);
			break;
		}
	}
	
	protected void setOnNavButtonClickListener(OnClickListener onClickListener){
		rightButton.setClickable(true);
		leftButton.setClickable(true);
		
		rightButton.setOnClickListener(onClickListener);
		leftButton.setOnClickListener(onClickListener);
	}
	
	protected void setTitleText(String text){
		if(title.getText() == null){
			title.setText(text);
		}else{
			title.setUpperText(text);
		}
	}
	
	protected boolean isProgressing(){
		return progressBar.getVisibility() == View.VISIBLE;
	}
	
	protected void setProgressing(boolean progressing, String title){
		
		if(progressing){
			progressBar.setVisibility(View.VISIBLE);
			if(title != null){
				setTitleText(title);
			}
		}else{
			progressBar.setVisibility(View.GONE);
			if(title != null){
				this.title.setText(title);
			}
			this.title.backward();
		}
	}

}
