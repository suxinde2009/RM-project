package com.jan.rm;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import com.jan.rm.adapter.PlaceSearchResultAdapter;
import com.jan.rm.baseactivity.BaseActivity;
import com.jan.rm.entity.PlaceSearchResult;
import com.jan.rm.logger.RLog;
import com.jan.rm.subactivity.CompassActivity;
import com.jan.rm.subactivity.EmergencyCallActivity;
import com.jan.rm.subactivity.FlashLightActivity;
import com.jan.rm.subactivity.LocationShareActivity;
import com.jan.rm.subactivity.SendSituationActivity;
import com.jan.rm.utils.Constants;
import com.jan.rm.utils.CoordsTransform;
import com.jan.rm.utils.LocationSeeker;
import com.jan.rm.utils.LocationSeeker.OnLocationGotListener;
import com.jan.rm.widget.MiniCompassView;
import com.jan.rm.R;

public class BaiduMapActivity extends BaseActivity{
	
	private LinearLayout mapLayout;
	
	private Button rmButton;
	private Button explanationButton;
	private Button settingsButton;
	private AutoCompleteTextView searchBar;
	private RelativeLayout mainButtonLayout;
	private ProgressBar searchProgressBar;
	
	private List<PlaceSearchResult> autoCompleteItems;
	private PlaceSearchResultAdapter autoCompleteAdapter;
	
	private DrawerLayout drawerLayout;
	private View rightDrawer;
	private MiniCompassView miniCompass;

	private BMapManager bMapManager;
	private MapView map;
	private LocationSeeker locationSeeker;
	
	private MyLocationOverlay myLocationOverlay;
	
	private Button emergencyCallButton;
	private Button lightButton;
	private Button compassButton;
	private Button parkingButton;
	private Button locationButton;
	
	private int scrollDistance;
	
	private OnLocationGotListener onLocationGotListener = new OnLocationGotListener(){

		@Override
		public void onLocationGot(final Location location) {
			new AsyncTask<Void, Void, double[]>(){

				@Override
				protected double[] doInBackground(Void... params) {
					//encrypt twice, WGS-84-->GCJ-02-->BD-09, for fucking encrypted baidu map
					double[] gcj02 = CoordsTransform.getGCJ02fromWGS84(location.getLatitude(), location.getLongitude());
					
					return CoordsTransform.getBD09fromGCJ02(gcj02[0], gcj02[1]);
				}
				
				@Override
				protected void onPostExecute(double[] result){
					
					RLog.d(result[0] + ":", result[1] + ":");
					if(map != null){
						LocationData locationData = new LocationData();
						locationData.latitude = result[0];
						locationData.longitude = result[1];
						locationData.direction = location.getBearing();
						
						if(myLocationOverlay == null){
							myLocationOverlay = new MyLocationOverlay(map);
							map.getOverlays().add(myLocationOverlay);
							myLocationOverlay.setData(locationData);
							
							map.refresh();
							map.getController().animateTo(new GeoPoint((int) (locationData.latitude * 1e6), (int) (locationData.longitude * 1e6)));
						}else{
							myLocationOverlay.setData(locationData);
						}
					}
				}
				
			}.execute();
		}
		
	};
	
	//handle all clickEvent in this context
	private OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.emergency_call_button:
				startActivity(new Intent(BaiduMapActivity.this, EmergencyCallActivity.class));
				break;
			case R.id.light_button:
				/*
				if(LightUtil.getInstance(MainActivity.this).toggle()){
					Drawable leftDrawable = MainActivity.this.getResources().getDrawable(R.drawable.ic_light_on);
					leftDrawable.setBounds(lightButton.getCompoundDrawables()[0].getBounds());
					lightButton.setCompoundDrawables(leftDrawable, null, null, null);
				}else{
					Drawable leftDrawable = MainActivity.this.getResources().getDrawable(R.drawable.ic_light_off);
					leftDrawable.setBounds(lightButton.getCompoundDrawables()[0].getBounds());
					lightButton.setCompoundDrawables(leftDrawable, null, null, null);
				}
				 */
				startActivity(new Intent(BaiduMapActivity.this, FlashLightActivity.class));
				break;
			case R.id.compass_button:
				startActivity(new Intent(BaiduMapActivity.this, CompassActivity.class));
		        break;
			case R.id.parking_location_button:
				
				//TODO add parking marker on the map
				
				drawerLayout.closeDrawers();
				
				break;
			case R.id.send_location_button:
				startActivity(new Intent(BaiduMapActivity.this, LocationShareActivity.class));
				break;
				
			case R.id.menu_toggle:
				drawerLayout.openDrawer(rightDrawer);
				break;
			case R.id.main_button:
				startActivity(new Intent(BaiduMapActivity.this, SendSituationActivity.class));
				break;
			case R.id.explanation_button:
				break;
			case R.id.search_bar:
				
				break;
			}
		}
	};
	
	//handle all KeyEvent in this context
	private OnKeyListener onKeyListener = new OnKeyListener(){

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			switch(v.getId()){
			case R.id.search_bar:
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					InputMethodManager imm = (InputMethodManager) BaiduMapActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					if(imm.isActive()){
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
						/*
						new GooglePlacesSearchTask().new TextSearchTask(searchBar.getText().toString(), BaiduMapActivity.this.getString(R.string.google_place_api_language)){
							
							@Override
							protected void onPreExecute(){
								searchProgressBar.setVisibility(View.VISIBLE);
								autoCompleteItems.clear();
								autoCompleteAdapter.notifyDataSetChanged();
							}
							
							@Override
							protected void onPostExecute(PlaceSearchResult[] results){
								searchProgressBar.setVisibility(View.GONE);
								if(results != null && results.length > 0){
									autoCompleteItems.addAll(Arrays.asList(results));
									autoCompleteAdapter.notifyDataSetChanged();
								}else{
									
								}
							}
							
						}.executeOnExecutor(singleTaskExecutor);
						 */
					}
				}
				break;
			}
			
			return false;
		}
		
	};
	
	//map offset of drawerlayout slide to mainButtonLayout
	private SimpleDrawerListener drawerListener = new SimpleDrawerListener(){
		@Override
		public void onDrawerClosed(View drawerView) {
			//disable slide-out gesture in drawerlayout to make sure this gesture will not confuse with google map scroll gesture
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			
			
		}

		@Override
		public void onDrawerOpened(View drawerView) {
			//with drawerlayout opened, enable slide gesture to provide a better experience.
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			
		}
		
		@Override
		public void onDrawerSlide(View drawerView, float slideOffset){
			if(scrollDistance <= 0) scrollDistance = rmButton.getHeight();
			mainButtonLayout.scrollTo(0, (int) - (scrollDistance * slideOffset));
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		bMapManager = new BMapManager(getApplication());
		bMapManager.init(Constants.BAIDU_MAP_API_KEY, null);
		
		setContentView(R.layout.activity_main);
		
		locationSeeker = LocationSeeker.getInstance(this);
		
		mapLayout = (LinearLayout) findViewById(R.id.map_layout);
		View mapInclude = getLayoutInflater().inflate(R.layout.include_baidu_map, null, false);
		mapInclude.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mapLayout.addView(mapInclude);
		
		map = (MapView) findViewById(R.id.map);
		map.setBuiltInZoomControls(false);
		map.getController().setRotationGesturesEnabled(false);
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		drawerLayout.setDrawerListener(drawerListener);
		
		rightDrawer = findViewById(R.id.right_drawer);
		
		miniCompass = (MiniCompassView) findViewById(R.id.mini_compass);
		
		mainButtonLayout = (RelativeLayout) findViewById(R.id.main_button_layout);
		
		rmButton = (Button) findViewById(R.id.main_button);
		explanationButton = (Button) findViewById(R.id.explanation_button);
		settingsButton = (Button) findViewById(R.id.menu_toggle);
		
		emergencyCallButton = (Button) findViewById(R.id.emergency_call_button);
		lightButton = (Button) findViewById(R.id.light_button);
		compassButton = (Button) findViewById(R.id.compass_button);
		parkingButton = (Button) findViewById(R.id.parking_location_button);
		locationButton = (Button) findViewById(R.id.send_location_button);
		
		rmButton.setOnClickListener(onClickListener);
		explanationButton.setOnClickListener(onClickListener);
		settingsButton.setOnClickListener(onClickListener);
		
		emergencyCallButton.setOnClickListener(onClickListener);
		lightButton.setOnClickListener(onClickListener);
		compassButton.setOnClickListener(onClickListener);
		parkingButton.setOnClickListener(onClickListener);
		locationButton.setOnClickListener(onClickListener);
		
		searchBar = (AutoCompleteTextView) findViewById(R.id.search_bar);
		searchBar.setOnKeyListener(onKeyListener);
		
	}
	
	@Override
	protected void onResume(){
		map.onResume();
		if(bMapManager != null){
			bMapManager.stop();
		}
		super.onResume();
		
		miniCompass.onResume();
		
		locationSeeker.findMyLocation(10, 50, onLocationGotListener);
	}
	
	@Override
	protected void onPause(){
		map.onPause();
		if(bMapManager != null){
			bMapManager.start();
		}
		super.onPause();
		
		miniCompass.onPause();
		
		locationSeeker.stopFindingLocation(onLocationGotListener);
	}
	
	@Override
	protected void onDestroy(){
		map.destroy();
		if(bMapManager != null){
			bMapManager.destroy();
			bMapManager = null;
		}
		super.onDestroy();
	}
}
