package com.jan.rm.subactivity;

import org.json.JSONArray;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jan.rm.baseactivity.BaseTitleActivity;
import com.jan.rm.dao.RmPreferenceManager;
import com.jan.rm.dao.RmServerApi;
import com.jan.rm.entity.rm.RMUser;
import com.jan.rm.task.RMSqlGetTask;
import com.jan.rm.R;

public class ProfileActivity extends BaseTitleActivity{
	
	private String packageName;
	
	private String[] medalLevelRes = new String[]{":drawable/copper_",
			                                      ":drawable/silver_",
			                                      ":drawable/gold_",
			                                      ":drawable/platinum_"};
	
	private String[] medalMaterialString;
	private String[] medalLevelString;
	
	private int[] levelDepend = new int[]{10, 40, 50, 50, 100, 200,
			                              50, 50, 100, 100, 100, 100,
			                              50, 50, 100, 100, 100, 100,
			                              100, 200, 200, 250, 250, 500,
			                              500, 500, 1000, 2000, 3000, 1};
	
	private ImageView avatar;
	private ImageView medal;
	private TextView medalLevel;
	private TextView account;
	private TextView sendCount;
	private TextView confirmCount;
	private TextView dispelCount;
	
	private RMUser user;
	
	private boolean profileGot;
	private boolean publishCountGot;
	private boolean recordCountGot;
	
	private String storedName;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		setTitleView(BaseTitleActivity.TITLE_ONLY_TITLE_TEXT, getString(R.string.activity_profile_title), null);
		
		medalMaterialString = this.getResources().getStringArray(R.array.medal_material);
		medalLevelString = this.getResources().getStringArray(R.array.medal_level);
		
		avatar = (ImageView) findViewById(R.id.head);
		medal = (ImageView) findViewById(R.id.medal);
		
		medalLevel = (TextView) findViewById(R.id.medal_level_describe);
		account = (TextView) findViewById(R.id.account);
		sendCount = (TextView) findViewById(R.id.sent_count);
		confirmCount = (TextView) findViewById(R.id.confirm_count);
		dispelCount = (TextView) findViewById(R.id.dispel_count);

		packageName = this.getPackageName();
		
		storedName = RmPreferenceManager.getInstance(this).getUserId();
		
		new RMSqlGetTask(RmServerApi.getProfile(storedName)){
			@Override
			protected void onPreExecute(){
				profileGot = false;
				ProfileActivity.this.setProgressing(true, ProfileActivity.this.getString(R.string.activity_profile_refreshing));
			}
			@Override
			protected void onPostExecute(JSONArray result){
				profileGot = true;
				updateAllSet();
				try{
					user = RmServerApi.parseJSONForUser(result.getJSONObject(0));
					
					account.setText(user.getUserId());
					
					/*
					int material = user.getLevel() / 6;
					int level = user.getLevel() % 6 - 1;
					if(level < 0) level = 0;
					
					medalLevel.setText(medalMaterialString[material] + medalLevelString[level]);
					
					int id = ProfileActivity.this.getResources().getIdentifier(packageName + medalLevelRes[material] + level, null, null);
					medal.setImageResource(id);
					 */
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.execute();
		
		new RMSqlGetTask(RmServerApi.getPublishCount(storedName)){
			@Override
			protected void onPreExecute(){
				publishCountGot = false;
				ProfileActivity.this.setProgressing(true, ProfileActivity.this.getString(R.string.activity_profile_refreshing));
			}
			@Override
			protected void onPostExecute(JSONArray result){
				publishCountGot = true;
				updateAllSet();
				try{
					sendCount.setText(result.getJSONObject(0).getString("publishCount") + ProfileActivity.this.getString(R.string.activity_profile_times));
					
					int level = 0;
					int levelIncrease = 0;
					for(int i = 0; i < levelDepend.length; i++){
						levelIncrease += levelDepend[i];
						if(levelIncrease < result.getJSONObject(0).getInt("publishCount")){
							level++;
						}else{
							break;
						}
					}
					
					int material = level / 6;
					
                    medalLevel.setText(medalMaterialString[material] + medalLevelString[level]);
					
					int id = ProfileActivity.this.getResources().getIdentifier(packageName + medalLevelRes[material] + level, null, null);
					medal.setImageResource(id);
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.execute();
		
		new RMSqlGetTask(RmServerApi.getRecordCount(storedName)){
			@Override
			protected void onPreExecute(){
				recordCountGot = false;
				ProfileActivity.this.setProgressing(true, ProfileActivity.this.getString(R.string.activity_profile_refreshing));
			}
			@Override
			protected void onPostExecute(JSONArray result){
				recordCountGot = true;
				updateAllSet();
				try{
					confirmCount.setText(result.getJSONObject(0).getString("confirmCount") + ProfileActivity.this.getString(R.string.activity_profile_times));
					dispelCount.setText(result.getJSONObject(1).getString("confirmCount") + ProfileActivity.this.getString(R.string.activity_profile_times));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	private void updateAllSet(){
		if(profileGot && publishCountGot && recordCountGot){
			setProgressing(false, null);
		}
	}
}
