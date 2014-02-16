package com.jan.rm.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jan.rm.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ExplanationAdapter extends RoundCornerListAdapter<String[]>{
	
	SpannableString[] spannableContents;

	public ExplanationAdapter(Context context, List<String[]> items) {
		super(context, items);
		spannableContents = new SpannableString[items.size()];
		for(int i = 0; i < items.size(); i++){
			spannableContents[i] = getSpannable(items.get(i)[1]);
		}
	}

	@Override
	protected String getTitle(int position) {
		return getItem(position)[0];
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		FrameLayout item = null;
		FrameLayout expandable = null;
		
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView = getInflater().inflate(R.layout.item_expandablelistview_explanation_activity, null, false);
			item = (FrameLayout) convertView.findViewById(R.id.expandable_toggle_button);
			expandable = (FrameLayout) convertView.findViewById(R.id.expandable);
			
			View itemContent = null;
			int expandableContentId = position == getCount() - 1 ? BOTTOM_LAYOUT : MIDDLE_LAYOUT;
			View expandableContent = getInflater().inflate(expandableContentId, null, false);
			
			expandableContent.findViewById(R.id.arrow).setVisibility(View.GONE);
			
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			
			switch(getItemViewType(position)){
			case 0:
				itemContent = getInflater().inflate(HEAD_LAYOUT, null, false);
				break;
			case 1:
				itemContent = getInflater().inflate(MIDDLE_LAYOUT, null, false);
				break;
			case 2:
				itemContent = getInflater().inflate(BOTTOM_LAYOUT, null, false);
				break;
			case 3:
				itemContent = getInflater().inflate(SINGLE_LAYOUT, null, false);
				break;
			}
			
			itemContent.setLayoutParams(params);
			item.addView(itemContent);
			
			expandableContent.setLayoutParams(params);
			expandable.addView(expandableContent);
			
			holder = new ViewHolder();
			
			holder.item = (TextView) item.findViewById(R.id.name);
			holder.expandable = (TextView) expandable.findViewById(R.id.name);
			holder.expandable.setClickable(true);
			holder.expandable.setMovementMethod(LinkMovementMethod.getInstance());
			
			holder.item.setTextColor(context.getResources().getColor(R.color.rm_content_highlight_color));
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.item.setText(getItem(position)[0]);
		holder.expandable.setText(spannableContents[position]);
		
		
		return convertView;
	}

	private class ViewHolder{
		TextView item;
		TextView expandable;
	}
	
	private SpannableString getSpannable(String content){
		SpannableString ss = new SpannableString(content);
		
		String HTTP_PATTERN = "http://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
		Pattern pattern = Pattern.compile(HTTP_PATTERN);
		final Matcher matcher = pattern.matcher(content);
		while(matcher.find()){
			final String group = matcher.group();
			ss.setSpan(new ClickableSpan(){

				@Override
				public void onClick(View widget) {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri url = Uri.parse(group);
					intent.setData(url);
					context.startActivity(intent);
				}
				
			}, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		return ss;
	}
}
