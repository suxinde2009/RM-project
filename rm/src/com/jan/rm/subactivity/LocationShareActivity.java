package com.jan.rm.subactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.twitter.Twitter;

import com.jan.rm.R;
import com.jan.rm.adapter.LocationShareAdapter;
import com.jan.rm.baseactivity.BaseTitleActivity;
import com.jan.rm.dao.RmDatabase;
import com.jan.rm.entity.LocationShareItem;
import com.jan.rm.logger.RLog;
import com.jan.rm.sns.entity.User;
import com.jan.rm.sns.ServiceProvider;
import com.jan.rm.widget.RMToast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class LocationShareActivity extends BaseTitleActivity{
	
	private int[] iconList = new int[]{R.drawable.logo_facebook,
			                           R.drawable.logo_twitter,
			                           R.drawable.logo_sinaweibo,
			                           R.drawable.logo_tencentweibo};
	
	private String[] nameList;
	
	private String[] sdkName = new String[]{Facebook.NAME,
			                                Twitter.NAME,
			                                SinaWeibo.NAME,
			                                TencentWeibo.NAME};
	
	private List<LocationShareItem> items;
	
	private ListView list;
	private LocationShareAdapter adapter;
	
	private double latitude;
	private double longitude;
	
	private String clickToLogin;
	private String logined;
	private String logout;
	
	private int onGoingTaskCount;
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
			final Platform sns = ShareSDK.getPlatform(LocationShareActivity.this, sdkName[position]);
			sns.setPlatformActionListener(platformActionListener);
			if(!sns.isValid()){
				LocationShareActivity.this.setProgressing(true, LocationShareActivity.this.getString(R.string.activity_login_loging_in));
				sns.authorize();
				onGoingTaskCount++;
			}else{
				
				final EditText inputServer = new EditText(LocationShareActivity.this);
				inputServer.setFocusable(true);
				
				final AlertDialog dialog = new AlertDialog.Builder(LocationShareActivity.this).setTitle(LocationShareActivity.this.getString(R.string.activity_location_share_title))
				                                                   .setView(inputServer)
				                                                   .setPositiveButton(LocationShareActivity.this.getString(R.string.location_share_send_location_directly), new OnClickListener(){
				                                                	   @Override
				                                                	   public void onClick(DialogInterface dialog, int which){
				                                                		   LocationShareActivity.this.setProgressing(true, LocationShareActivity.this.getString(R.string.activity_location_share_sending));
				                                                		   Platform.ShareParams shareParams = null;
				                                           				    switch(position){
				                                           				    case 0:
				                                           				    	shareParams = new Facebook.ShareParams();
				                                           				    	break;
				                                           				    case 1:
				                                           				    	shareParams = new Twitter.ShareParams();
				                                           				    	break;
				                                           				    case 2:
				                                           				    	shareParams = new SinaWeibo.ShareParams();
				                                           				    	((SinaWeibo.ShareParams) shareParams).latitude = (float) latitude;
				                                           				    	((SinaWeibo.ShareParams) shareParams).longitude = (float) longitude;
				                                           				    	break;
				                                           				    case 3:
				                                           				    	shareParams = new TencentWeibo.ShareParams();
				                                           				    	((TencentWeibo.ShareParams) shareParams).latitude = (float) latitude;
				                                           				    	((TencentWeibo.ShareParams) shareParams).longitude = (float) longitude;
				                                           				    	break;
				                                           				    default:
				                                           				    	break;
				                                           				    }
				                                           				
				                                           				    if(shareParams != null){
				                                           				    	shareParams.text = inputServer.getText().length() > 0 ? inputServer.getText().toString() : LocationShareActivity.this.getString(R.string.location_share_i_am_here);
				                                           				    	sns.share(shareParams);
				                                           				    	onGoingTaskCount++;
				                                           				    }
				                                                	   }
				                                                   }).setNegativeButton(LocationShareActivity.this.getString(R.string.cancel), new OnClickListener(){
				                                                	   @Override
				                                                	   public void onClick(DialogInterface dialog, int which){
				                                                		   dialog.cancel();
				                                                	   }
				                                                   }).create();
				
				inputServer.addTextChangedListener(new TextWatcher(){

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if(inputServer.getText().length() > 0){
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(LocationShareActivity.this.getString(R.string.location_share_send_location));
						}else{
							dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(LocationShareActivity.this.getString(R.string.location_share_send_location_directly));
						}
					}

					@Override
					public void afterTextChanged(Editable s) {}
					
				});
				
				dialog.show();
				
			}
		}
		
	};
	
	private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			
			Platform sns = ShareSDK.getPlatform(LocationShareActivity.this, sdkName[position]);
			if(sns.isValid()){
				new AlertDialog.Builder(LocationShareActivity.this).setTitle(logout)
                .setPositiveButton(LocationShareActivity.this.getString(R.string.confirm), new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Platform sns = ShareSDK.getPlatform(LocationShareActivity.this, sdkName[position]);
						sns.removeAccount();
						
						items.get(position).setName(nameList[position]);
						items.get(position).setSubTitle(clickToLogin);
						
						adapter.notifyDataSetChanged();
					}
             	   
                }).setNegativeButton(LocationShareActivity.this.getString(R.string.cancel), new OnClickListener(){
             	  @Override
             	  public void onClick(DialogInterface dialog, int which){
             		  dialog.cancel();
             	  }
                }).create().show();
				
				return true;
			}else{
				return false;
			}
			
			
		}
		
	};
	
	private PlatformActionListener platformActionListener = new PlatformActionListener(){

		@Override
		public void onCancel(Platform platform, int action) {
			RLog.d("count", onGoingTaskCount + "");
			onGoingTaskCount--;
			
			runOnUiThread(new Runnable(){
				@Override
				public void run(){
					if(onGoingTaskCount == 0) LocationShareActivity.this.setProgressing(false, null);
				}
			});
			RLog.d("share" + action, platform.getName());
		}

		@Override
		public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
			RLog.d("count", onGoingTaskCount + "");
			
			switch(action){
			case Platform.ACTION_AUTHORIZING:
				onGoingTaskCount--;
				for(int i = 0; i < items.size(); i++){
					if(platform.getName().equals(sdkName[i])){
						
						final int ii = i;
						
						platform.showUser(null);
						RLog.d("platform", platform.getName());
						
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								items.get(ii).setSubTitle(logined);
								
								adapter.notifyDataSetChanged();
								
							}
						});
						
						break;
					}
				}
				break;
			case Platform.ACTION_SHARE:
				onGoingTaskCount--;
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						RMToast.showPositive(LocationShareActivity.this, LocationShareActivity.this.getString(R.string.location_share_send_success));
					}
				});
				break;
			case Platform.ACTION_USER_INFOR:
				RLog.d("map" + action, res.toString());
				for(int i = 0; i < items.size(); i++){
					if(platform.getName().equals(sdkName[i])){
						
						final int ii = i;
						final User user = ServiceProvider.getServiceProvider(sdkName[i]).parseUserInfo(res);
						
						if(RmDatabase.getInstance(LocationShareActivity.this).isPlatformStored(platform.getName())){
							RmDatabase.getInstance(LocationShareActivity.this).updateUser(user, platform.getName());
						}else{
							RmDatabase.getInstance(LocationShareActivity.this).insertUser(user, platform.getName());
						}
						
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								items.get(ii).setName(user.getUserName());
								items.get(ii).setSubTitle(nameList[ii]);
								
								adapter.notifyDataSetChanged();
							}
						});
					}
				}
				break;
			}
			
			runOnUiThread(new Runnable(){
				@Override
				public void run(){
					if(onGoingTaskCount == 0) LocationShareActivity.this.setProgressing(false, null);
				}
			});
		}

		@Override
		public void onError(Platform platform, int action, final Throwable t) {
			RLog.d("count", onGoingTaskCount + "");
			
			switch(action){
			case Platform.ACTION_AUTHORIZING:
				onGoingTaskCount--;
				break;
			case Platform.ACTION_SHARE:
				onGoingTaskCount--;
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if(t.getMessage().contains("Connection")){
							RMToast.showNegative(LocationShareActivity.this, LocationShareActivity.this.getString(R.string.location_share_send_network_issue));
						}else{
							RMToast.showNegative(LocationShareActivity.this, LocationShareActivity.this.getString(R.string.location_share_send_failure));
						}
					}
				});
				RLog.d("error" + action, t.getMessage());
				break;
			case Platform.ACTION_USER_INFOR:
				RLog.d("error" + action, t.getMessage());
				break;
			}
			
			runOnUiThread(new Runnable(){
				@Override
				public void run(){
					if(onGoingTaskCount == 0) LocationShareActivity.this.setProgressing(false, null);
				}
			});
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);
		setTitleView(BaseTitleActivity.TITLE_ONLY_TITLE_TEXT, getString(R.string.activity_location_share_title), null);
		
		clickToLogin = getString(R.string.location_share_click_to_login);
		logined = getString(R.string.location_share_logined);
		logout = getString(R.string.location_share_log_out);
		
		latitude = this.getIntent().getDoubleExtra("latitude", 0);
		longitude = this.getIntent().getDoubleExtra("longitude", 0);
		
		ShareSDK.initSDK(this);
		
		nameList = this.getResources().getStringArray(R.array.location_share_name_list);
		
		items = new ArrayList<LocationShareItem>();
		
		onGoingTaskCount = 0;
		
		for(int i = 0; i < iconList.length; i++){
			LocationShareItem item = new LocationShareItem();
			item.setIcon(this.getResources().getDrawable(iconList[i]));
			item.setName(nameList[i]);
			Platform sns = ShareSDK.getPlatform(this, sdkName[i]);
			if(sns.isValid()){
			    User user = RmDatabase.getInstance(this).getUser(sns.getName());
				item.setName(user.getUserName());
				item.setSubTitle(nameList[i]);
			}else{
				item.setSubTitle(clickToLogin);
			}
			
			items.add(item);
		}
		
		adapter = new LocationShareAdapter(this, items);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(onItemClickListener);
		list.setOnItemLongClickListener(onItemLongClickListener);
	
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}
}
