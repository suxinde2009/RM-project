package com.jan.rm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import com.jan.rm.adapter.PlaceSearchResultAdapter;
import com.jan.rm.adapter.RmInfoWindowAdapter;
import com.jan.rm.concurrent.RunningExecutor;
import com.jan.rm.concurrent.ThreadPolicy;
import com.jan.rm.dao.RmPreferenceManager;
import com.jan.rm.dao.RmServerApi;
import com.jan.rm.entity.GoogleDirectionRoute;
import com.jan.rm.entity.GoogleDirectionStep;
import com.jan.rm.entity.PlaceSearchResult;
import com.jan.rm.entity.rm.ClusterMarker;
import com.jan.rm.entity.rm.Situation;
import com.jan.rm.logger.RLog;
import com.jan.rm.subactivity.CompassActivity;
import com.jan.rm.subactivity.EmergencyCallActivity;
import com.jan.rm.subactivity.ExplanationActivity;
import com.jan.rm.subactivity.FlashLightActivity;
import com.jan.rm.subactivity.LocationShareActivity;
import com.jan.rm.subactivity.ProfileActivity;
import com.jan.rm.subactivity.SettingsActivity;
//import com.jan.rm.subactivity.SendSituationActivity;
import com.jan.rm.subactivity.TempSendSituationActivity;
import com.jan.rm.task.GoogleDirectionsTask;
import com.jan.rm.task.GooglePlacesSearchTask;
import com.jan.rm.task.NewAsyncTask;
import com.jan.rm.task.RMSqlGetTask;
import com.jan.rm.task.RMSqlPostTask;
import com.jan.rm.utils.ConnectionUtil;
import com.jan.rm.utils.CoordsTransform;
import com.jan.rm.utils.LocationSeeker;
import com.jan.rm.utils.LocationSeeker.OnLocationGotListener;
import com.jan.rm.widget.BottomUpView;
import com.jan.rm.widget.MiniCompassView;
import com.jan.rm.widget.RMTitle;
import com.jan.rm.widget.RMToast;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class GoogleMapActivity extends FragmentActivity{
	
	private static final int LOCATION_UPDATE_TIME_GAP = 1000;
	private static final int MIN_CLUSTER_COUNT = 10;
	
	private int width;
	private int height;

	private Button rmButton;
	private Button explanationButton;
	private Button menuButton;
	private AutoCompleteTextView searchBar;
	private RelativeLayout mainButtonLayout;
	private ProgressBar searchProgressBar;
	private ImageButton myLocationButton;
	
	private List<PlaceSearchResult> autoCompleteItems;
	private PlaceSearchResultAdapter autoCompleteAdapter;
	
	private DrawerLayout drawerLayout;
	private View rightDrawer;
	private MiniCompassView miniCompass;
	
	private LinearLayout mapLayout;
	
	private GoogleMap map;
	private LocationSeeker locationSeeker;
	private String currentCountry;
	private RmInfoWindowAdapter infoWindowAdapter;
	
	private BottomUpView bottomUpView;
	
	private LatLng currentLatLng;
	private LatLng notCorrectedLatLng;
	private Marker currentMarker;
	private Marker parkingMarker;
	private Marker searchResultMarker;
	private Marker clickedMarker;
	private Circle accuracyCircleOverlays;
	private Polyline routeInnerLine;
	private Polyline routeOuterLine;
	
	private long currentMillisSeconds = -1;
	
	private ClusterManager<ClusterMarker> clusterManager;
	private ClusterMarker[] clusterMarkers;
	private Queue<ClusterMarker> addClusterMarkers;
	private Queue<ClusterMarker> removeClusterMarkers;
	
	private Button emergencyCallButton;
	private Button lightButton;
	private Button compassButton;
	private Button parkingButton;
	private Button locationButton;
	
	private Button profileButton;
	private Button settingsButton;
	private Button feedbackButton;
	
	private List<Situation> situations;
	private LatLng[] latLngs;
	
	private final int VOICE_RECOGNITION_REQUEST_CODE = 1100;
	private final int SEND_SITUATION_REQUEST_CODE = 1111;
	
	private int scrollDistance;
	
	private RunningExecutor searchSingleTaskExecutor;
	private RunningExecutor situationUpdateSingleTaskExecutor;
	
	private int onGoingTaskCount;

	private class ClusterMarkerRenderer extends DefaultClusterRenderer<ClusterMarker>{
		private IconGenerator iconGenerator;

		public ClusterMarkerRenderer() {
			super(getApplicationContext(), map, clusterManager);
			
			iconGenerator = new IconGenerator(getApplicationContext());
		}
		
		@Override
		protected void onBeforeClusterItemRendered(ClusterMarker marker, MarkerOptions markerOptions){
			markerOptions.icon(BitmapDescriptorFactory.fromResource(marker.getResourceId()))
			             .anchor(0.5F, 1F)
			             .title(marker.getSituation().getContent())
			             .snippet(marker.getSituation().getConfirmed() + GoogleMapActivity.this.getString(R.string.activity_main_marker_confirmed));
		}
		
		@Override
		protected void onBeforeClusterRendered(Cluster<ClusterMarker> cluster, MarkerOptions markerOptions){
			int iconStyle;
			int size = cluster.getSize();
			if(size < 50){
				iconStyle = IconGenerator.STYLE_BLUE;
			}else if(size < 100){
				iconStyle = IconGenerator.STYLE_ORANGE;
			}else{
				iconStyle = IconGenerator.STYLE_RED;
			}
			
			iconGenerator.setStyle(iconStyle);
			Bitmap icon = iconGenerator.makeIcon(size + GoogleMapActivity.this.getString(R.string.cluster_title_count));
			
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).anchor(0.5F,  1F);
		}
		
		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster){
			return cluster.getSize() > MIN_CLUSTER_COUNT;
		}
		
	}
	
	private TextWatcher onTextChangeListener = new TextWatcher(){

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(searchBar.getText().length() > 0){
				try {
					new GooglePlacesSearchTask.GeoCodeAddressSearchTask(URLEncoder.encode(searchBar.getText().toString(), "UTF-8"), GoogleMapActivity.this.getString(R.string.google_place_api_language)){
						
						@Override
						protected void onPreExecute(){
							onGoingTaskCount++;
							searchProgressBar.setVisibility(View.VISIBLE);
						}
						
						@Override
						protected void onPostExecute(PlaceSearchResult[] results){
							onGoingTaskCount--;
							if(onGoingTaskCount == 0){
								searchProgressBar.setVisibility(View.GONE);
								if(results != null && results.length > 0){
									autoCompleteItems.clear();
									autoCompleteItems.addAll(Arrays.asList(results));
									autoCompleteAdapter.notifyDataSetChanged();
								}
							}
						}
						
					}.executeOnRunningExecutor(searchSingleTaskExecutor);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			if(searchBar.getText().length() == 0 && searchResultMarker != null){
				searchResultMarker.remove();
				searchResultMarker = null;
			}
		}
		
	};
	
	private OnItemClickListener onAutoCompleteItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View target, int position, long id){
			InputMethodManager imm = (InputMethodManager) GoogleMapActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm.isActive()){
				imm.hideSoftInputFromWindow(searchBar.getApplicationWindowToken(), 0);
			}
			
			searchBar.setText(autoCompleteAdapter.getItem(position).getName());
			LatLng searchResultLatLng = autoCompleteAdapter.getItem(position).getLocation();
			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(searchResultLatLng, 17F));
			
			if(searchResultLatLng != null){
				if(searchResultMarker != null){
					searchResultMarker.setPosition(searchResultLatLng);
					searchResultMarker.setTitle(autoCompleteAdapter.getItem(position).getName());
					searchResultMarker.setSnippet(autoCompleteAdapter.getItem(position).getFormattedAddress());
					searchResultMarker.hideInfoWindow();
				}else{
					searchResultMarker = map.addMarker(new MarkerOptions().position(searchResultLatLng).anchor(0.5F, 1F)
							                                              .title(autoCompleteAdapter.getItem(position).getName())
							                                              .snippet(autoCompleteAdapter.getItem(position).getFormattedAddress())
							                                              .icon(BitmapDescriptorFactory.defaultMarker()));
				}
				searchResultMarker.showInfoWindow();
				
			}
		}
	};
	
	private OnLocationGotListener onLocationGotListener = new OnLocationGotListener(){

		@Override
		public void onLocationGot(final Location location) {
			
			notCorrectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			
			if(currentCountry == null){
				currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				new GooglePlacesSearchTask.GeoCodeLatLngSearchTask(notCorrectedLatLng, GoogleMapActivity.this.getString(R.string.google_place_api_language)){
					@Override
					protected PlaceSearchResult[] doInBackground(Void... params){
						PlaceSearchResult[] result = super.doInBackground(params);
						
						if(result != null && result[0].getCountryCode() != null && result[0].getCountryCode().equals("CN")){
							currentCountry = result[0].getCountryCode();
							double[] correctResult = CoordsTransform.getGCJ02fromWGS84(location.getLatitude(), location.getLongitude());
							currentLatLng = new LatLng(correctResult[0], correctResult[1]);
						}
						
						return result;
					}
					
				}.execute();
			}else{
				new NewAsyncTask<Void, Void, double[]>(){

					@Override 
					protected double[] doInBackground(Void... params) {
						//encrypt for china mainland
						if(currentCountry.equals("CN")){
							return CoordsTransform.getGCJ02fromWGS84(location.getLatitude(), location.getLongitude());
						}else{
							return new double[]{location.getLatitude(), location.getLongitude()};
						}
						
					}
					
					@Override
					protected void onPostExecute(double[] result){
						
						executeLocationGotResult(result, location.getAccuracy());
					}
					
				}.execute();
				
				new RMSqlGetTask(RmServerApi.getSituation(location.getLatitude(), location.getLongitude(), 10)){
					@Override
					protected JSONArray doInBackground(Void... params){
						JSONArray result = super.doInBackground(params);
						if(result != null){
							int length = result.length();
							latLngs = new LatLng[length];
							
							if(situations != null){
								situations.clear();
							}else{
								situations = new ArrayList<Situation>();
							}
							try{
								ClusterMarker[] newClusterMarkers = new ClusterMarker[length];
								for(int i = 0; i < length; i++){
									Situation situation = RmServerApi.parseJSONForSituation(result.getJSONObject(i));
									situations.add(situation);
									
									double[] correctedC = new double[]{situation.getLatitude(), situation.getLongitude()};
									if(currentCountry!= null && currentCountry.equals("CN")){
										correctedC = CoordsTransform.getGCJ02fromWGS84(situation.getLatitude(), situation.getLongitude());
									}
									
									latLngs[i] = new LatLng(correctedC[0], correctedC[1]);
									
									int level = (int) Math.floor(situations.get(i).getConfirmed() / 50) + 1;
									if(level > 3) level = 3;
									
									int intrinsicLevel = situations.get(i).getLevel();
									
									level = Math.max(level, intrinsicLevel);
									
									int resourceId = GoogleMapActivity.this.getResources().getIdentifier(GoogleMapActivity.this.getPackageName() + ":drawable/pin_lv0" + level, null, null);
									
									newClusterMarkers[i] = new ClusterMarker(resourceId, latLngs[i], situation);
								}
								
								if(clusterMarkers!= null && clusterMarkers.length > 0){
									
									//same effect as below, but with much less calculations, these codes didn't work, working on it now...
									/*
									int j = 0;
									int i = 0;
									while(i < clusterMarkers.length && j < newClusterMarkers.length){
										long oldKey = clusterMarkers[i].getSituation().getAKey();
										long newKey = newClusterMarkers[i].getSituation().getAKey();
										
	                                    if(i == clusterMarkers.length - 1){
	                                    	addClusterMarkers.add(newClusterMarkers[j]);
											j++; 
	                                    	continue;
										}
	                                    
	                                    if(j == newClusterMarkers.length - 1){
	                                    	removeClusterMarkers.add(clusterMarkers[i]);
	                                    	i++;
	                                    	continue;
	                                    }
										
										if(oldKey == newKey){
											if(j < newClusterMarkers.length) j++;
											if(i < clusterMarkers.length) i++;
										}else if(oldKey > newKey){
											addClusterMarkers.add(newClusterMarkers[j]);
											if(j < newClusterMarkers.length) j++;
										}else{
											removeClusterMarkers.add(clusterMarkers[i]);
											i++;
										}
									}
									 */
									
									
									//calculate differences between oldClusterMarkers and newClusterMarkers and put it into addClusterMarkers and removeClusterMarkers
									//ClusterManager add or remove items base on addClusterMarkers and removeClusterMarkers, prevent a recluter being call after clear and add for each and every item;
									outloop1 : for(int k = 0; k < clusterMarkers.length; k++){
										for(int l = 0; l < newClusterMarkers.length; l++){
											if(clusterMarkers[k].getSituation().getAKey() == newClusterMarkers[l].getSituation().getAKey()){
												continue outloop1;
											}
										}
										removeClusterMarkers.add(clusterMarkers[k]);
									}
									
									outloop2 : for(int k = 0; k < newClusterMarkers.length; k++){
										for(int l = 0; l < clusterMarkers.length; l++){
											if(newClusterMarkers[k].getSituation().getAKey() == clusterMarkers[l].getSituation().getAKey()){
												continue outloop2;
											}
										}
										addClusterMarkers.add(newClusterMarkers[k]);
									}
									
								}else{
									addClusterMarkers = new LinkedList<ClusterMarker>();
									removeClusterMarkers = new LinkedList<ClusterMarker>();
									
									for(int i = 0; i < newClusterMarkers.length; i++){
										addClusterMarkers.add(newClusterMarkers[i]);
									}
								}
								
								clusterMarkers = newClusterMarkers;
								
							}catch(JSONException e){
								e.printStackTrace();
							}
						}
						
						return result;
					}
					@Override
					protected void onPostExecute(JSONArray result){
						if(result != null && map != null){
							if(removeClusterMarkers != null && removeClusterMarkers.size() > 0){
								RLog.d("removeClusterMarkers", removeClusterMarkers.size() + "");
								while(!removeClusterMarkers.isEmpty()){
									clusterManager.removeItem(removeClusterMarkers.poll());
								}
							}
							if(addClusterMarkers != null && addClusterMarkers.size() > 0){
								RLog.d("addClusterMarkers", addClusterMarkers.size() + "");
								while(!addClusterMarkers.isEmpty()){
									clusterManager.addItem(addClusterMarkers.poll());
								}
							}
						}
					}
				}.executeOnRunningExecutor(situationUpdateSingleTaskExecutor);
			}
			
		}
		
	};
	
	private void executeLocationGotResult(double[] result, float gpsAccuracy){
		RLog.d(result[0] + "", result[1] + "");
		if(map != null){
			LatLng disLatLng = new LatLng(result[0], result[1]);
			if(currentLatLng != null && currentMillisSeconds != -1){
				double gap = System.currentTimeMillis() - currentMillisSeconds;
				searchBar.setText(SphericalUtil.computeDistanceBetween(currentLatLng, disLatLng) * (1000D / gap) * 3.6D + "km/h");
			}
			currentMillisSeconds = System.currentTimeMillis();
			
			currentLatLng = disLatLng;
			
			
			if(currentMarker == null){
				currentMarker = map.addMarker(new MarkerOptions().position(currentLatLng).anchor(0.5F, 0.5F).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot)));
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17F));
				
			}else{
				currentMarker.setPosition(currentLatLng);
				
			}
			
			if(accuracyCircleOverlays == null){
				accuracyCircleOverlays = map.addCircle(new CircleOptions().center(currentLatLng)
						                                                  .radius(gpsAccuracy)
						                                                  .strokeWidth(1)
						                                                  .strokeColor(0x440099CC)
						                                                  .fillColor(0x110099CC));
			}else{
				accuracyCircleOverlays.setCenter(currentLatLng);
				accuracyCircleOverlays.setRadius(gpsAccuracy);
			}
			
		}
	}
	
	//handle all clickEvent in this context
	private OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.emergency_call_button:
				startActivity(new Intent(GoogleMapActivity.this, EmergencyCallActivity.class));
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
				startActivity(new Intent(GoogleMapActivity.this, FlashLightActivity.class));
				break;
			case R.id.compass_button:
				startActivity(new Intent(GoogleMapActivity.this, CompassActivity.class));
		        break;
			case R.id.parking_location_button:
				
				if(parkingMarker != null){
					parkingMarker.remove();
					parkingMarker = null;
				}else{
					if(currentLatLng != null && map != null){
						parkingMarker = map.addMarker(new MarkerOptions().position(currentLatLng)
								                                         .anchor(0.5F, 1F)
								                                         .title(GoogleMapActivity.this.getString(R.string.marker_title_park_here))
								                                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_park)));
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17F));
					}
				}
				
				drawerLayout.closeDrawers();
				
				break;
			case R.id.send_location_button:
				Intent intent = new Intent(GoogleMapActivity.this, LocationShareActivity.class);
				if(currentLatLng != null){
					intent.putExtra("latitude", currentLatLng.latitude);
					intent.putExtra("longitude", currentLatLng.longitude);
				}
				
				startActivity(intent);
				break;
				
			case R.id.menu_toggle:
				drawerLayout.openDrawer(rightDrawer);
				break;
			case R.id.main_button:
				Intent mainButtonIntent = new Intent(GoogleMapActivity.this, TempSendSituationActivity.class);
				if(currentLatLng != null){
					mainButtonIntent.putExtra("latitude", notCorrectedLatLng.latitude);
					mainButtonIntent.putExtra("longitude", notCorrectedLatLng.longitude);
				}
				
				startActivityForResult(mainButtonIntent, SEND_SITUATION_REQUEST_CODE);
				break;
			case R.id.explanation_button:
				startActivity(new Intent(GoogleMapActivity.this, ExplanationActivity.class));
				break;
			case R.id.profile_button:
				startActivity(new Intent(GoogleMapActivity.this, ProfileActivity.class));
				break;
			case R.id.settings_button:
				startActivity(new Intent(GoogleMapActivity.this, SettingsActivity.class));
				break;
			case R.id.search_bar:
				break;
			case R.id.my_location_button:
				if(map != null && currentLatLng != null){
					map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
				}
				break;
			}
		}
	};
	
	private OnLongClickListener onLongClickListener = new OnLongClickListener(){

		@Override
		public boolean onLongClick(View v) {
			switch(v.getId()){
			case R.id.main_button:
				try{
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition");
					
					startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
				}catch(ActivityNotFoundException e){
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=pname:com.google.android.voicesearch"));
					startActivity(intent);
				}
			}
			return true;
		}
		
	};
	
	private OnMapClickListener onMapClickListener = new OnMapClickListener(){
		
		@Override
		public void onMapClick(final LatLng latLng){
			bottomUpView.performHide();
		}
	};
	
	private OnMapLongClickListener onMapLongClickListener = new OnMapLongClickListener(){

		@Override
		public void onMapLongClick(final LatLng latLng) {
			new GooglePlacesSearchTask.GeoCodeLatLngSearchTask(latLng, GoogleMapActivity.this.getString(R.string.google_place_api_language)){
				@Override
				protected void onPreExecute(){
					if(clickedMarker == null){
						clickedMarker = map.addMarker(new MarkerOptions().anchor(0.5F, 1F)
								                                         .title("...")
								                                         .snippet(GoogleMapActivity.this.getString(R.string.marker_clicked_sub_title))
								                                         .position(latLng)
								                                         .icon(BitmapDescriptorFactory.defaultMarker()));
					}else{
						clickedMarker.setTitle("...");
						clickedMarker.setSnippet(GoogleMapActivity.this.getString(R.string.marker_clicked_sub_title));
						clickedMarker.setPosition(latLng);
					}
					
					clickedMarker.hideInfoWindow();
					clickedMarker.showInfoWindow();
				}
				
				@Override
				protected void onPostExecute(PlaceSearchResult[] result){
					String street = null;
					String locality = null;
					if(result == null || result.length == 0){
						
						locality = GoogleMapActivity.this.getString(R.string.marker_clicked_no_result);
						street = "";
					}else{
						locality = result[0].getLocality();
						street = result[0].getFormattedAddress() + "\n";
					}
					
					if(clickedMarker == null){
						clickedMarker = map.addMarker(new MarkerOptions().anchor(0.5F, 1F)
								                                         .position(latLng)
								                                         .title(locality)
								                                         .snippet(street + GoogleMapActivity.this.getString(R.string.marker_clicked_sub_title))
								                                         .icon(BitmapDescriptorFactory.defaultMarker()));
					}else{
						clickedMarker.setTitle(locality);
						clickedMarker.setSnippet(street + GoogleMapActivity.this.getString(R.string.marker_clicked_sub_title));
						clickedMarker.setPosition(latLng);
					}
					
					clickedMarker.hideInfoWindow();
					clickedMarker.showInfoWindow();
					
				}
				
			}.executeOnRunningExecutor(searchSingleTaskExecutor);
			
			new GoogleDirectionsTask.GetRouteTask(currentLatLng, latLng, GoogleDirectionsTask.DIRECTION_MODE_WALKING, GoogleMapActivity.this.getString(R.string.google_place_api_language)){
				@Override
				protected void onPreExecute(){
					if(routeInnerLine != null && routeOuterLine != null){
						routeInnerLine.remove();
						routeOuterLine.remove();
						
						routeInnerLine = null;
						routeOuterLine = null;
					}
				}
				@Override
				protected void onPostExecute(GoogleDirectionRoute[] routes){
					if(routes != null && routes.length > 0){
						RLog.d("routes", routes.length + "");
						if(map != null){
							if(routeInnerLine == null && routeOuterLine == null){
								PolylineOptions innerPolylineOptions = new PolylineOptions();
								PolylineOptions outerPolylineOptions = new PolylineOptions();
								for(GoogleDirectionStep step : routes[0].getSteps()){
									for(LatLng latLng : step.getPath()){
										innerPolylineOptions.add(latLng);
										outerPolylineOptions.add(latLng);
									}
								}
								
								innerPolylineOptions.color(0xFF00CCFF).width(8).zIndex(21);
								outerPolylineOptions.color(0xFF0099CC).width(12).zIndex(20);
								
								routeOuterLine = map.addPolyline(outerPolylineOptions);
								routeInnerLine = map.addPolyline(innerPolylineOptions);
							}else{
								
							}
						}
					}
				}
			}.execute();
		}
		
	};
	
	private OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener(){

		@Override
		public void onInfoWindowClick(Marker marker) {
			if(clickedMarker != null && marker.equals(clickedMarker)){
				clickedMarker.remove();
				clickedMarker = null;
			}
		}
		
	};
	
	private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener(){

		@Override
		public boolean onMarkerClick(Marker marker) {
			if(clickedMarker != null && marker.equals(clickedMarker)){
				clickedMarker.hideInfoWindow();
				clickedMarker.remove();
				clickedMarker = null;
			}else if(!marker.equals(currentMarker) && !marker.equals(parkingMarker)){
				
				if(marker.isInfoWindowShown()){
					marker.hideInfoWindow();
				}else{
					marker.showInfoWindow();
				}
			}
			return clusterManager.onMarkerClick(marker);
		}
		
	};
	
	private OnClusterItemClickListener<ClusterMarker> onClusterItemClickListener = new OnClusterItemClickListener<ClusterMarker>(){

		@Override
		public boolean onClusterItemClick(final ClusterMarker item) {
			if(map != null){
				map.animateCamera(CameraUpdateFactory.newLatLng(item.getPosition()));
				if(bottomUpView.getContent() != null){
					bottomUpView.performShow();
					((RMTitle) bottomUpView.findViewById(R.id.title)).setUpperText(item.getSituation().getContent());
					((RMTitle) bottomUpView.findViewById(R.id.subtitle)).setUpperText(item.getSituation().getConfirmed() + GoogleMapActivity.this.getString(R.string.activity_main_marker_confirmed));
					
					bottomUpView.findViewById(R.id.confirm_button).setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v){
							new RMSqlPostTask(RmServerApi.updateConfirm(item.getSituation().getAKey())){
								@Override
								protected void onPostExecute(Integer result){
									if(result == RMSqlPostTask.RESULT_OK){
										RMToast.showPositive(GoogleMapActivity.this, "success");
										bottomUpView.performHide();
									}else{
										RMToast.showNegative(GoogleMapActivity.this, "failure");
									}
								}
							}.execute();
						}
					});
					
					bottomUpView.findViewById(R.id.dispel_button).setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v){
							new RMSqlPostTask(RmServerApi.updateConfirm(item.getSituation().getAKey())){
								@Override
								protected void onPostExecute(Integer result){
									if(result == RMSqlPostTask.RESULT_OK){
										RMToast.showPositive(GoogleMapActivity.this, "success");
										bottomUpView.performHide();
									}else{
										RMToast.showNegative(GoogleMapActivity.this, "failure");
									}
								}
							}.execute();
						}
					});
				}
			}
			
			return true;
		}
		
	};
	
	private OnClusterClickListener<ClusterMarker> onClusterClickListener = new OnClusterClickListener<ClusterMarker>(){

		@Override
		public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
			if(map != null){
				map.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
			}
			return true;
		}
		
	};
	
	//handle all KeyEvent in this context
	private OnKeyListener onKeyListener = new OnKeyListener(){

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			switch(v.getId()){
			case R.id.search_bar:
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					InputMethodManager imm = (InputMethodManager) GoogleMapActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					if(imm.isActive()){
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
						
						try {
							new GooglePlacesSearchTask.GeoCodeAddressSearchTask(URLEncoder.encode(searchBar.getText().toString(), "UTF-8"), GoogleMapActivity.this.getString(R.string.google_place_api_language)){
								
								@Override
								protected void onPreExecute(){
									onGoingTaskCount++;
									searchProgressBar.setVisibility(View.VISIBLE);
									autoCompleteItems.clear();
									autoCompleteAdapter.notifyDataSetChanged();
								}
								
								@Override
								protected void onPostExecute(PlaceSearchResult[] results){
									onGoingTaskCount--;
									if(onGoingTaskCount == 0){
										searchProgressBar.setVisibility(View.GONE);
										if(results != null && results.length > 0){
											autoCompleteItems.clear();
											autoCompleteItems.addAll(Arrays.asList(results));
											autoCompleteAdapter.notifyDataSetChanged();
										}else{
											
										}
									}
								}
								
							}.executeOnRunningExecutor(searchSingleTaskExecutor);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			}
			
			return false;
		}
		
	};
	
	private OnCameraChangeListener onCameraChangeListener = new OnCameraChangeListener(){

		@Override
		public void onCameraChange(CameraPosition position) {
			clusterManager.onCameraChange(position);
			
			if(map != null && currentLatLng != null){
				Point currentPosition = map.getProjection().toScreenLocation(currentLatLng);
				if(currentPosition.x <= 0 || currentPosition.x >= width || currentPosition.y <= 0 || currentPosition.y >= height){
					myLocationButton.setVisibility(View.VISIBLE);
				}else{
					myLocationButton.setVisibility(View.GONE);
				}
			}
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
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		width = dm.widthPixels;
		height = dm.heightPixels;
		
		mapLayout = (LinearLayout) findViewById(R.id.map_layout);
		View mapInclude = getLayoutInflater().inflate(R.layout.include_google_map, null, false);
		mapInclude.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mapLayout.addView(mapInclude);
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		drawerLayout.setDrawerListener(drawerListener);
		
		rightDrawer = findViewById(R.id.right_drawer);
		
		locationSeeker = LocationSeeker.getInstance(this);
		
		miniCompass = (MiniCompassView) findViewById(R.id.mini_compass);
		
		//drawerLayout buttons
		emergencyCallButton = (Button) findViewById(R.id.emergency_call_button);
		lightButton = (Button) findViewById(R.id.light_button);
		compassButton = (Button) findViewById(R.id.compass_button);
		parkingButton = (Button) findViewById(R.id.parking_location_button);
		locationButton = (Button) findViewById(R.id.send_location_button);
		
		profileButton = (Button) findViewById(R.id.profile_button);
		settingsButton = (Button) findViewById(R.id.settings_button);
		feedbackButton = (Button) findViewById(R.id.feedback_button);
		
		//main function buttons
		rmButton = (Button) findViewById(R.id.main_button);
		explanationButton = (Button) findViewById(R.id.explanation_button);
		menuButton = (Button) findViewById(R.id.menu_toggle);
		myLocationButton = (ImageButton) findViewById(R.id.my_location_button);
		
		scrollDistance = -1;
		
		searchBar = (AutoCompleteTextView) findViewById(R.id.search_bar);
		searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
		mainButtonLayout = (RelativeLayout) findViewById(R.id.main_button_layout);
		
		bottomUpView = (BottomUpView) findViewById(R.id.bottom_up_view);
		bottomUpView.inflateChild(R.layout.bottom_up_view_situation_detail);
		
		emergencyCallButton.setOnClickListener(onClickListener);
		lightButton.setOnClickListener(onClickListener);
		compassButton.setOnClickListener(onClickListener);
		parkingButton.setOnClickListener(onClickListener);
		locationButton.setOnClickListener(onClickListener);
		
		profileButton.setOnClickListener(onClickListener);
		settingsButton.setOnClickListener(onClickListener);
		feedbackButton.setOnClickListener(onClickListener);
		
		rmButton.setOnClickListener(onClickListener);
		rmButton.setOnLongClickListener(onLongClickListener);
		explanationButton.setOnClickListener(onClickListener);
		menuButton.setOnClickListener(onClickListener);
		myLocationButton.setOnClickListener(onClickListener);
		
		searchBar.setOnKeyListener(onKeyListener);
		searchBar.addTextChangedListener(onTextChangeListener);
		searchBar.setClickable(true);
		searchBar.setOnClickListener(onClickListener);
		
		autoCompleteItems = new ArrayList<PlaceSearchResult>();
		autoCompleteAdapter = new PlaceSearchResultAdapter(this, autoCompleteItems);
		
		searchBar.setOnItemClickListener(onAutoCompleteItemClickListener);
		searchBar.setAdapter(autoCompleteAdapter);
		
		situations = new ArrayList<Situation>();
		
		try{
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			
			//disable all google map UI
			map.getUiSettings().setZoomControlsEnabled(false);
			map.getUiSettings().setRotateGesturesEnabled(false);
			map.getUiSettings().setCompassEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			//map.getUiSettings().setTiltGesturesEnabled(false);
			map.setBuildingsEnabled(false);
			
			map.setOnMapLongClickListener(onMapLongClickListener);
			map.setOnInfoWindowClickListener(onInfoWindowClickListener);
			map.setOnMarkerClickListener(onMarkerClickListener);
			map.setOnMapClickListener(onMapClickListener);
			
			//enable traffic
			map.setTrafficEnabled(true);
			
			clusterManager = new ClusterManager<ClusterMarker>(this, map);
			clusterManager.setOnClusterItemClickListener(onClusterItemClickListener);
			clusterManager.setOnClusterClickListener(onClusterClickListener);
			clusterManager.setRenderer(new ClusterMarkerRenderer());
			
			map.setOnCameraChangeListener(onCameraChangeListener);
			
			infoWindowAdapter = new RmInfoWindowAdapter(this);
			map.setInfoWindowAdapter(infoWindowAdapter);
			
		}catch(NullPointerException e){
			searchBar.setVisibility(View.GONE);
			miniCompass.setVisibility(View.GONE);
			e.printStackTrace();
		}
		
		onGoingTaskCount = 0;
		
		ConnectionUtil.checkConnectionAndReport(this);
	}
	
	//start all system services in onResume and stop all system services in onPause to prevent unnecessary resources waste
	@Override
	public void onResume(){
		super.onResume();
		miniCompass.onResume();
		
		locationSeeker.findMyLocation(LOCATION_UPDATE_TIME_GAP, 10F, onLocationGotListener);
		
		searchSingleTaskExecutor = new RunningExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), new ThreadPolicy.DumpOldestPolicy());
		situationUpdateSingleTaskExecutor = new RunningExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), new ThreadPolicy.DumpOldestPolicy());
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		miniCompass.onPause();
		locationSeeker.stopFindingLocation(onLocationGotListener);
		
		searchSingleTaskExecutor.shutdownNow();
		situationUpdateSingleTaskExecutor.shutdownNow();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			switch(requestCode){
			case VOICE_RECOGNITION_REQUEST_CODE:
				
				ArrayList<String> matcher = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if(matcher.size() > 0){
					String[] temp = new String[matcher.size()];
					matcher.toArray(temp);
					
					final String[] items = temp;
					
					new AlertDialog.Builder(GoogleMapActivity.this).setItems(items, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int item){
							searchBar.setText(items[item]);
						}
					}).create().show();
				}else{
					new AlertDialog.Builder(GoogleMapActivity.this).setTitle("no result").setPositiveButton(GoogleMapActivity.this.getString(R.string.confirm), null).create().show();
				}
				break;
			case SEND_SITUATION_REQUEST_CODE:
				Situation resultSituation = new Situation();
				resultSituation.setContent(data.getStringExtra("content_sent"));
				resultSituation.setLatitude(data.getDoubleExtra("latitude", 0D));
				resultSituation.setLongitude(data.getDoubleExtra("longitude", 0D));
				resultSituation.setUserId(RmPreferenceManager.getInstance(this).getUserId());
				resultSituation.setConfirmed(1);
				
				situations.add(resultSituation);
				
				/*
				int resourceId = this.getResources().getIdentifier(this.getPackageName() + ":drawable/pin_lv0" + data.getIntExtra("content_level", 1), null, null);
				
				if(currentCountry != null && currentCountry == "CN"){
					double[] correctedLocation = CoordsTransform.getGCJ02fromWGS84(resultSituation.getLatitude(), resultSituation.getLongitude());
					
					clusterManager.addItem(new ClusterMarker(resourceId, new LatLng(correctedLocation[0], correctedLocation[1]), resultSituation));
				}else{
					clusterManager.addItem(new ClusterMarker(resourceId, new LatLng(data.getDoubleExtra("latitude", 0D), data.getDoubleExtra("longitude", 0D)), resultSituation));
				}
				 */
				
				
				break;
			}
		}
	}

}
