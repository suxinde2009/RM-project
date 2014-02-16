package com.jan.rm.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jan.rm.R;

public abstract class RoundCornerListAdapter<T> extends BaseAdapter{

	protected Context context;
	List<T> items;
	
	private LayoutInflater inflater;
	
	protected Drawable rightDrawable;
	
	protected final int HEAD_LAYOUT = R.layout.item_head_listview_treemenu;
	protected final int MIDDLE_LAYOUT = R.layout.item_listview_treemenu;
	protected final int BOTTOM_LAYOUT = R.layout.item_bottom_listview_treemenu;
	protected final int SINGLE_LAYOUT = R.layout.item_single_listview_treemenu;
	
	public RoundCornerListAdapter(Context context, List<T> items){
		this.context = context;
		this.items = items;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public T getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position){
		if(getCount() == 1){
			return 3;
		}else{
			if(position == 0){
				return 0;
			}else if(position > 0 && position < getCount() - 1){
				return 1;
			}else{
				return 2;
			}
		}
	}
	
	@Override
	public int getViewTypeCount(){
		return 4;
	}
	
	protected abstract String getTitle(int position);
	
	protected boolean enableSubTitle(){
		return false;
	}
	protected boolean enableIcon(){
		return false;
	}
	
	protected Drawable getRightDrawable(int position){
		if(rightDrawable == null){
			rightDrawable = context.getResources().getDrawable(R.drawable.list_arrow);
		}
		
		return rightDrawable;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			switch(getItemViewType(position)){
			case 0:
				convertView = getInflater().inflate(HEAD_LAYOUT, null, false);
				break;
			case 1:
				convertView = getInflater().inflate(MIDDLE_LAYOUT, null, false);
				break;
			case 2:
				convertView = getInflater().inflate(BOTTOM_LAYOUT, null, false);
				break;
			case 3:
				convertView = getInflater().inflate(SINGLE_LAYOUT, null, false);
			}
			
			if(enableSubTitle()) convertView.findViewById(R.id.subtitle).setVisibility(View.VISIBLE);
			if(enableIcon()) convertView.findViewById(R.id.icon).setVisibility(View.VISIBLE);
			
			((ImageView) convertView.findViewById(R.id.arrow)).setImageDrawable(getRightDrawable(position));
		}
		
		TextView tv = (TextView) convertView.findViewById(R.id.name);
		tv.setText(getTitle(position));
		
		return convertView;
	}
	
	protected LayoutInflater getInflater(){
		if(inflater == null) inflater = LayoutInflater.from(context);
		return inflater;
	}
};