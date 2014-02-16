package com.jan.rm.adapter;

import java.util.List;

import com.jan.rm.R;
import com.jan.rm.dao.ds.ActionPair;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TreeMenuAdapter extends RoundCornerListAdapter<ActionPair> {

	public TreeMenuAdapter(Context context, List<ActionPair> items) {
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
		
		if(getItem(position).getCount() != -1){
			convertView.findViewById(R.id.subtitle).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.subtitle)).setText(String.format(context.getString(R.string.send_situation_count_subtitle), getItem(position).getCount() + ""));
		}else{
			convertView.findViewById(R.id.subtitle).setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
