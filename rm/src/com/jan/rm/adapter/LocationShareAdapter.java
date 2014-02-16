package com.jan.rm.adapter;

import java.util.List;

import com.jan.rm.R;
import com.jan.rm.entity.LocationShareItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationShareAdapter extends RoundCornerListAdapter<LocationShareItem>{

	public LocationShareAdapter(Context context, List<LocationShareItem> items) {
		super(context, items);
	}

	@Override
	protected String getTitle(int position) {
		return getItem(position).getName();
	}

	@Override
	protected boolean enableIcon(){
		return true;
	}
	
	@Override
	protected boolean enableSubTitle(){
		return true;
	}
	
	@Override
	protected Drawable getRightDrawable(int position){
		return null;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		convertView = super.getView(position, convertView, parent);
		
		ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
		iv.setImageDrawable(getItem(position).getIcon());
		
		TextView subTitle = (TextView) convertView.findViewById(R.id.subtitle);
		subTitle.setText(getItem(position).getSubTitle());
		
		return convertView;
	}
}
