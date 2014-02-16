package com.jan.rm.adapter;

import java.util.List;

import com.jan.rm.R;
import com.jan.rm.dao.ds.ActionPair;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class EmergencyCallAdapter extends RoundCornerListAdapter<ActionPair> {

	public EmergencyCallAdapter(Context context, List<ActionPair> items) {
		super(context, items);
	}

	@Override
	protected String getTitle(int position) {
		
		return getItem(position).getTitle();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		convertView = super.getView(position, convertView, parent);
		
		if(getItem(position).hasChild()){
			convertView.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
		}else{
			convertView.findViewById(R.id.arrow).setVisibility(View.GONE);
		}
		
		return convertView;
	}
}
