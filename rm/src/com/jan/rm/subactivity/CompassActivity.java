package com.jan.rm.subactivity;

import com.google.android.gms.maps.model.LatLng;
import com.jan.rm.R;
import com.jan.rm.baseactivity.BaseActivity;
import com.jan.rm.entity.PlaceSearchResult;
import com.jan.rm.task.GooglePlacesSearchTask;
import com.jan.rm.utils.LocationSeeker;
import com.jan.rm.utils.LocationSeeker.OnLocationGotListener;
import com.jan.rm.widget.BaseCompassView.OnDirectionChangeListener;
import com.jan.rm.widget.CompassView;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CompassActivity extends BaseActivity {
	
	private CompassView compass;
	private LocationSeeker locationSeeker;
	
	private TextView coords;
	private TextView angleText;
	private TextView direction;
	
	private String locality;
	
	private String latitudeString;
	private String longitudeString;
	
	private int[] directionString = new int[]{R.string.north,
			                                  R.string.north_east,
			                                  R.string.east,
			                                  R.string.south_east,
			                                  R.string.south,
			                                  R.string.south_west,
			                                  R.string.west,
			                                  R.string.north_west};
	
	private OnLocationGotListener onLocationGotListener = new OnLocationGotListener(){

		@Override
		public void onLocationGot(final Location location) {
			if(location.getLatitude() > 0){
				latitudeString = CompassActivity.this.getString(R.string.n_latitude);
			}else{
				latitudeString = CompassActivity.this.getString(R.string.s_latitude);
			}
			
			if(location.getLongitude() > 0){
				longitudeString = CompassActivity.this.getString(R.string.e_longitude);
			}else{
				longitudeString = CompassActivity.this.getString(R.string.w_longitude);
			}
			
			coords.setText(latitudeString + LocationSeeker.getFormatedCoords(location.getLatitude()) + " " + longitudeString + LocationSeeker.getFormatedCoords(location.getLongitude()));
			
			new GooglePlacesSearchTask.GeoCodeLatLngSearchTask(new LatLng(location.getLatitude(), location.getLongitude()), CompassActivity.this.getString(R.string.google_place_api_language)){
				@Override
				protected void onPostExecute(PlaceSearchResult[] result){
					locality = result[0].getSubLocality() + "\n" + result[0].getLocality();
				}
				
			}.execute();
		}
		
	};
	
	private OnDirectionChangeListener onDirectionChangeListener = new OnDirectionChangeListener(){

		@Override
		public void onDirectionChanged(float angle) {
			String directionS;
			if(angle < 22.5 || angle > 337.5){
				directionS = CompassActivity.this.getString(R.string.north);
			}else{
				directionS = CompassActivity.this.getString(directionString[(int) ((angle - 22.5) / 45F) + 1]);
			}
			
			angleText.setText((int) angle + "°");
			if(locality == null){
				direction.setText(directionS);
			}else{
				direction.setText(directionS + "\n" + locality);
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);
		
		compass = (CompassView) findViewById(R.id.compass);
		compass.setOnDirectionChangeListener(onDirectionChangeListener);
		
		coords = (TextView) findViewById(R.id.coords);
		angleText = (TextView) findViewById(R.id.angle);
		direction = (TextView) findViewById(R.id.direction);
		
		locationSeeker = LocationSeeker.getInstance(this);
		
		((Button) findViewById(R.id.back_button)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				CompassActivity.this.finish();
			}
		});
		
	}

	@Override
	public void onResume(){
		super.onResume();
		compass.onResume();
		locationSeeker.findMyLocation(5000, 10, onLocationGotListener);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		compass.onPause();
		locationSeeker.stopFindingLocation(onLocationGotListener);
	}
	
}
