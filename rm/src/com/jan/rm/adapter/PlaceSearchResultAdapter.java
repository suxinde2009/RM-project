package com.jan.rm.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jan.rm.R;
import com.jan.rm.entity.PlaceSearchResult;

public class PlaceSearchResultAdapter extends RoundCornerListAdapter<PlaceSearchResult> implements Filterable {

	public PlaceSearchResultAdapter(Context context, List<PlaceSearchResult> items) {
		super(context, items);
	}

	@Override
	protected String getTitle(int position) {
		return getItem(position).getName();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		convertView = super.getView(position, convertView, parent);
		
		TextView subTitle = (TextView) convertView.findViewById(R.id.subtitle);
		subTitle.setText(getItem(position).getFormattedAddress());
		
		return convertView;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter(){

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				List<PlaceSearchResult> filterResult = autoComplete(constraint);
				filterResults.values = filterResult;
				filterResults.count = filterResult.size();
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if(results != null && results.count > 0){
					notifyDataSetChanged();
				}else{
					notifyDataSetInvalidated();
				}
			}
			
		};
		
		return filter;
	}
	
	private List<PlaceSearchResult> autoComplete(CharSequence constraint){
		List<PlaceSearchResult> result = new ArrayList<PlaceSearchResult>();
		result.addAll(items);
		for(int i = 0; i < items.size(); i++){
			if(!result.get(i).getName().contains(constraint)){
				result.remove(i);
			}
		}
		
		return result;
	}

	@Override
	protected boolean enableSubTitle() {
		return true;
	}
	
}
