package com.jan.rm.task;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.jan.rm.entity.PlaceSearchResult;
import com.jan.rm.utils.Constants;

public class GooglePlacesSearchTask {
	
	private static final String nearbySearchApi = Constants.GOOGLE_PLACES_API + "/nearbysearch/json";
	private static final String textSearchApi = Constants.GOOGLE_PLACES_API + "/textsearch/json";
	private static final String radarSearchApi = Constants.GOOGLE_PLACES_API + "/radarsearch/json";
	private static final String autoCompleteSearchApi = Constants.GOOGLE_PLACES_API + "/autocomplete/json";
	private static final String geocodeSearchApi = Constants.GOOGLE_GEOCODE_API + "/json";
	
	public static class NearbySearchTask extends HttpGetTask<PlaceSearchResult[]>{

		public NearbySearchTask(LatLng latlng, int radius, String name, String language) {
			super(nearbySearchApi, "location=" + latlng.latitude + "," + latlng.longitude + "&radius=" + radius + "&name=" + name + "&sensor=true&language=" + language + "&key=" + Constants.GOOGLE_API_KEY);
		}
		
		@Override
		protected PlaceSearchResult[] doInBackground(Void... params){
			super.doInBackground(params);
			
			return parseResult(result);
		}
		
	}
	
	public static class TextSearchTask extends HttpGetTask<PlaceSearchResult[]>{
		
		public TextSearchTask(String text, String language) throws UnsupportedEncodingException{
			super(textSearchApi, "query=" + text + "&sensor=true&language=" + language + "&key=" + Constants.GOOGLE_API_KEY);
		}
		
		@Override
		protected PlaceSearchResult[] doInBackground(Void... params){
			super.doInBackground(params);
			
			return parseResult(result);
		}
	}
	
	public static class RadarSearchTask extends HttpGetTask<PlaceSearchResult[]>{
		
		public RadarSearchTask(LatLng latlng, int radius, String name, String language){
			super(radarSearchApi, "location=" + latlng.latitude + "," + latlng.longitude + "&radius=" + radius + "&name=" + name + "&sensor=true&language=" + language + "&key=" + Constants.GOOGLE_API_KEY);
		}
		
		@Override
		protected PlaceSearchResult[] doInBackground(Void... params){
			super.doInBackground(params);
			
			return parseResult(result);
		}
	}
	
	public static class AutoCompleteSearchTask extends HttpGetTask<PlaceSearchResult[]>{
		public AutoCompleteSearchTask(String text, String language){
			super(autoCompleteSearchApi, "input=" + text + "&sensor=true&language=" + language + "&key=" + Constants.GOOGLE_API_KEY);
		}
		
		@Override
		protected PlaceSearchResult[] doInBackground(Void... params){
			super.doInBackground(params);
			
			return null;
		}
	}
	
	public static class GeoCodeAddressSearchTask extends HttpGetTask<PlaceSearchResult[]>{
		
		public GeoCodeAddressSearchTask(String name, String language){
			super(geocodeSearchApi, "address=" + name + "&sensor=true&language=" + language);
		}
		
		@Override
		protected PlaceSearchResult[] doInBackground(Void... params){
			super.doInBackground(params);
			
			return parseGeoAddressResult(result);
		}
	}
	
	public static class GeoCodeLatLngSearchTask extends HttpGetTask<PlaceSearchResult[]>{

		public GeoCodeLatLngSearchTask(LatLng latLng, String language) {
			super(geocodeSearchApi, "latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true&language=" + language);
		}
		
		@Override
		protected PlaceSearchResult[] doInBackground(Void... params){
			super.doInBackground(params);
			
			return parseGeoLatLngResult(result);
		}
	}
	
	private static PlaceSearchResult[] parseResult(String result){
		PlaceSearchResult[] results = null;
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			
			JSONArray jsonArray = jsonObject.optJSONArray("results");
			results = new PlaceSearchResult[jsonArray.length()];
			for(int i = 0; i < results.length; i++){
				results[i] = new PlaceSearchResult();
				try{
					results[i].setName(jsonObject.getJSONArray("address_components").getJSONObject(0).getString("long_name"));
					results[i].setFormattedAddress(jsonObject.getString("formatted_address"));
					results[i].setTypes(jsonObject.getJSONArray("types").join(",").split(","));
					JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
					results[i].setLocation(new LatLng(location.getDouble("lat"), location.getDouble("lng")));
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		
		return results;
	}
	
	private static PlaceSearchResult[] parseGeoAddressResult(String result){
		PlaceSearchResult[] results = null;
		
		try{
			JSONObject jsonObject = new JSONObject(result);
			
			JSONArray jsonArray = jsonObject.optJSONArray("results");
			results = new PlaceSearchResult[jsonArray.length()];
			for(int i = 0; i < results.length; i++){
				results[i] = parseAddressGeo(jsonArray.getJSONObject(i));
				
			}
		}catch(JSONException e){
			e.printStackTrace();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		
		return results;
	}
	
	private static PlaceSearchResult[] parseGeoLatLngResult(String result){
		PlaceSearchResult[] results = null;
		
		try{
			JSONObject jsonObject = new JSONObject(result);
			
			JSONArray jsonArray = jsonObject.optJSONArray("results");
			results = new PlaceSearchResult[jsonArray.length()];
			for(int i = 0; i < results.length; i++){
				results[i] = new PlaceSearchResult();
				JSONObject resultJSONObject = jsonArray.getJSONObject(i);
				if(resultJSONObject.has("formatted_address")){
					results[i].setFormattedAddress(resultJSONObject.getString("formatted_address"));
				}
				
				if(resultJSONObject.has("address_components")){
					JSONArray componentsArray = resultJSONObject.getJSONArray("address_components");
					for(int j = 0; j < componentsArray.length(); j++){
						setResultArea(results[i], componentsArray.getJSONObject(j).getJSONArray("types").getString(0), componentsArray.getJSONObject(j));
					}
				}
				
			}
		}catch(JSONException e){
			e.printStackTrace();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		
		return results;
	}
	
	private static void setResultArea(PlaceSearchResult result, String name, JSONObject value) throws JSONException{
		if(name.equals("sublocality")){
			result.setSubLocality(value.getString("long_name"));
		}else if(name.equals("locality")){
			result.setLocality(value.getString("long_name"));
		}else if(name.equals("administrative_area_level_1")){
			result.setAdminArea(1, value.getString("long_name"));
		}else if(name.equals("administrative_area_level_2")){
			result.setAdminArea(2, value.getString("long_name"));
		}else if(name.equals("administrative_area_level_3")){
			result.setAdminArea(3, value.getString("long_name"));
		}else if(name.equals("country")){
			result.setCountryCode(value.getString("short_name"));
		}
	}
	
	private static PlaceSearchResult parseAddressGeo(JSONObject jsonObject) throws JSONException{
		PlaceSearchResult result = new PlaceSearchResult();

		result.setId(jsonObject.getString("id"));
		result.setName(jsonObject.getString("name"));
		result.setRating((float) jsonObject.optDouble("rating"));
		result.setIcon(jsonObject.getString("icon"));
		result.setTypes(jsonObject.getJSONArray("types").join(",").split(","));
		result.setFormattedAddress(jsonObject.getString("formatted_address"));
		JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
		result.setLocation(new LatLng(location.getDouble("lat"), location.getDouble("lng")));
		
		return result;
	}
}
