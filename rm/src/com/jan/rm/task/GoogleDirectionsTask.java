package com.jan.rm.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.jan.rm.entity.GoogleDirectionRoute;
import com.jan.rm.entity.GoogleDirectionStep;
import com.jan.rm.utils.Constants;

/*
 *   _    ________    __    __    ________    ________    __    __    ________    ___   __ _
 *  /\\--/\______ \--/\ \--/\ \--/\  _____\--/\  ____ \--/\ \--/\ \--/\  _____\--/\  \-/\ \\\
 *  \ \\ \/_____/\ \ \ \ \_\_\ \ \ \ \____/_ \ \ \__/\ \ \ \ \_\ \ \ \ \ \____/_ \ \   \_\ \\\
 *   \ \\       \ \ \ \ \  ____ \ \ \  _____\ \ \  ____ \ \_ \ \_\ \  \ \  _____\ \ \  __   \\\
 *    \ \\       \ \ \ \ \ \__/\ \ \ \ \____/_ \ \ \__/\ \  \_ \ \ \   \ \ \____/_ \ \ \ \_  \\\
 *     \ \\       \ \_\ \ \_\ \ \_\ \ \_______\ \ \_\ \ \_\   \_ \_\    \ \_______\ \ \_\_ \__\\\
 *      \ \\       \/_/  \/_/  \/_/  \/_______/  \/_/  \/_/     \/_/     \/_______/  \/_/ \/__/ \\
 *       \ \\----------------------------------------------------------------------------------- \\
 *        \//                                                                                   \//
 *
 * 
 *
 */

public class GoogleDirectionsTask {
	
	private static final String getRouteApi = Constants.GOOGLE_DIRECTION_API + "/json";

	public static final String DIRECTION_MODE_DRIVING = "driving";
	public static final String DIRECTION_MODE_WALKING = "walking";
	public static final String DIRECTION_MODE_BICYCLING = "bicycling";
	public static final String DIRECTION_MODE_TRANSIT = "transit";
	
	public static class GetRouteTask extends HttpGetTask<GoogleDirectionRoute[]>{

		public GetRouteTask(LatLng from, LatLng to, String mode, String language) {
			super(getRouteApi, "origin=" + from.latitude + "," + from.longitude + "&destination=" + to.latitude + "," + to.longitude + "&sensor=true&units=metric&mode=" + mode + "&language=" + language);
		}
		
		@Override
		protected GoogleDirectionRoute[] doInBackground(Void... params){
			super.doInBackground(params);
			
			GoogleDirectionRoute[] routes = null;
			
			try{
				JSONObject resultJSON = new JSONObject(result);
				JSONArray routesJSON = resultJSON.getJSONArray("routes");
				routes = new GoogleDirectionRoute[routesJSON.length()];
				int length = routesJSON.length();
				for(int i = 0; i < length; i++){
					routes[i] = new GoogleDirectionRoute();
					routes[i].setRoute(parsePolyline(routesJSON.getJSONObject(i).getJSONObject("overview_polyline").getString("points")));
					
					JSONObject legsJSON = routesJSON.getJSONObject(i).getJSONArray("legs").getJSONObject(0);
					routes[i].setTotalDistanceText(legsJSON.getJSONObject("distance").getString("text"));
					routes[i].setTotalDistanceValue(legsJSON.getJSONObject("distance").getInt("value"));
					routes[i].setTotalDurationText(legsJSON.getJSONObject("duration").getString("text"));
					routes[i].setTotalDurationValue(legsJSON.getJSONObject("duration").getInt("value"));
					
					routes[i].setStartAddress(legsJSON.getString("start_address"));
					routes[i].setStartLatLng(new LatLng(legsJSON.getJSONObject("start_location").getDouble("lat"),
							                            legsJSON.getJSONObject("start_location").getDouble("lng")));
					
					routes[i].setEndAddress(legsJSON.getString("end_address"));
					routes[i].setEndLatLng(new LatLng(legsJSON.getJSONObject("end_location").getDouble("lat"),
							                          legsJSON.getJSONObject("end_location").getDouble("lng")));
					
					JSONArray stepJSONArray = legsJSON.getJSONArray("steps");
					int stepLength = stepJSONArray.length();
					List<GoogleDirectionStep> steps = new ArrayList<GoogleDirectionStep>();
					for(int j = 0; j < stepLength; j++){
						JSONObject stepJSON = stepJSONArray.getJSONObject(j);
						GoogleDirectionStep step = new GoogleDirectionStep();
						step.setDistanceText(stepJSON.getJSONObject("distance").getString("text"));
						step.setDistanceValue(stepJSON.getJSONObject("distance").getInt("value"));
						step.setDurationText(stepJSON.getJSONObject("duration").getString("text"));
						step.setDurationValue(stepJSON.getJSONObject("duration").getInt("value"));
						
						step.setInstruction(stepJSON.getString("html_instructions"));
						step.setPath(parsePolyline(stepJSON.getJSONObject("polyline").getString("points")));
						
						step.setStartLocation(new LatLng(stepJSON.getJSONObject("start_location").getDouble("lat"),
								                         stepJSON.getJSONObject("start_location").getDouble("lng")));
						step.setEndLocation(new LatLng(stepJSON.getJSONObject("end_location").getDouble("lat"),
								                       stepJSON.getJSONObject("end_location").getDouble("lng")));
						step.setTravelMode(stepJSON.getString("travel_mode"));
						
						steps.add(step);
					}
					routes[i].setSteps(steps);
					
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			
			return routes;
		}
		
	}
	
	private static List<LatLng> parsePolyline(String code){
		List<LatLng> results = new ArrayList<LatLng>();
		
		int len = code.length();
		int index = 0;
		
		int lat = 0;
		int lng = 0;
		
		while(index < len){
			int b;
			int shift = 0;
			int result = 0;
			do{
				b = code.charAt(index++) - 63;
				result |= (b & 0x1F) << shift;
				shift += 5;
			}while(b >= 0x20);
			int dLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dLat;
			
			shift = 0;
			result = 0;
			do{
				b = code.charAt(index++) - 63;
				result |= (b & 0x1F) << shift;
				shift += 5;
			}while(b >= 0x20);
			int dLng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dLng;
			
			double latitude = lat / 1E5;
			double longitude = lng / 1E5;
			
			results.add(new LatLng(latitude, longitude));
		}
		
		return results;
	}
}
