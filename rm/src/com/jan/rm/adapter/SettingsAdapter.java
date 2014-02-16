package com.jan.rm.adapter;

import java.util.List;

import com.jan.rm.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsAdapter extends RoundCornerListAdapter<String>{

	public SettingsAdapter(Context context, List<String> items){
		super(context, items);
	}

	@Override
	protected String getTitle(int position) {
		return getItem(position);
	}
	
	@Override
	public int getViewTypeCount(){
		return 1;
	}
	
	@Override
	public int getItemViewType(int position){
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		if(convertView == null){
			convertView = getInflater().inflate(SINGLE_LAYOUT, null, false);
			
			if(enableSubTitle()) convertView.findViewById(R.id.subtitle).setVisibility(View.VISIBLE);
			if(enableIcon()) convertView.findViewById(R.id.icon).setVisibility(View.VISIBLE);
			
			((ImageView) convertView.findViewById(R.id.arrow)).setImageDrawable(getRightDrawable(position));
		}
		
		((TextView) convertView.findViewById(R.id.name)).setText(getTitle(position));
		
		return convertView;
	}
}
