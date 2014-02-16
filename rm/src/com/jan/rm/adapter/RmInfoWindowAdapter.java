package com.jan.rm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import com.jan.rm.R;

public class RmInfoWindowAdapter implements InfoWindowAdapter{
	
	private Context context;
	
	public RmInfoWindowAdapter(Context context){
		this.context = context;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		if(marker.getTitle() != null || marker.getSnippet() != null){
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.content_info_window, null, false);
			((TextView) view.findViewById(R.id.title)).setText(marker.getTitle());
			((TextView) view.findViewById(R.id.subtitle)).setText(marker.getSnippet());
			
			return view;
		}else{
			return null;
		}
	}

}
