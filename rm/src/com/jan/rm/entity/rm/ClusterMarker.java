package com.jan.rm.entity.rm;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem{
	
	private int drawableRes;
	private final LatLng correctPosition;
	private Situation situation;

	public ClusterMarker(int drawableRes, LatLng correctPosition, Situation situation){
		this.drawableRes = drawableRes;
		this.correctPosition = correctPosition;
		this.situation = situation;
	}
	
	public Situation getSituation(){
		return situation;
	}
	
	public int getResourceId(){
		return drawableRes;
	}
	
	@Override
	public LatLng getPosition(){
		return correctPosition;
	}
}
