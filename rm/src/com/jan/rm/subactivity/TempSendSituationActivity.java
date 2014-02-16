package com.jan.rm.subactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jan.rm.GoogleMapActivity;
import com.jan.rm.R;
import com.jan.rm.adapter.TreeMenuAdapter;
import com.jan.rm.baseactivity.BaseTitleActivity;
import com.jan.rm.dao.RmDatabase;
import com.jan.rm.dao.RmPreferenceManager;
import com.jan.rm.dao.RmServerApi;
import com.jan.rm.dao.ds.ActionPair;
import com.jan.rm.task.RMSqlPostTask;
import com.jan.rm.widget.RMToast;

public class TempSendSituationActivity extends BaseTitleActivity {
	
	ActionPair situationTree;
	ActionPair currentPair;
	
	List<ActionPair> items;
	
	private ListView list;
	private TreeMenuAdapter adapter;
	
	private double latitude;
	private double longitude;
	
	private Intent resultIntent;
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View target, final int position, long id){
			if(currentPair.getChild(position) != null && currentPair.getChild(position).hasChild()){
				currentPair = currentPair.getChild(position);
				
				setTitleText(currentPair.getTitle());
				
				items.clear();
				items.addAll(currentPair.getChildren());
				
				adapter.notifyDataSetChanged();
			}else{
				if(!isProgressing()){
					new RMSqlPostTask(RmServerApi.sendSituation(latitude, longitude, currentPair.getChild(position).getTitle(), currentPair.getChild(position).getLevel(), RmPreferenceManager.getInstance(TempSendSituationActivity.this).getUserId())){
						@Override
						protected void onPreExecute(){
							TempSendSituationActivity.this.setProgressing(true, TempSendSituationActivity.this.getString(R.string.activity_location_share_sending));
						}
						@Override
						protected void onPostExecute(Integer result){
							TempSendSituationActivity.this.setProgressing(false, null);
							if(result == 1){
								RMToast.showPositive(TempSendSituationActivity.this, TempSendSituationActivity.this.getString(R.string.location_share_send_success));
								RmDatabase.getInstance(TempSendSituationActivity.this).insertRecentSituation(currentPair.getChild(position));
								resultIntent = new Intent(TempSendSituationActivity.this, GoogleMapActivity.class);
								resultIntent.putExtra("content_sent", currentPair.getChild(position).getTitle());
								resultIntent.putExtra("content_level", currentPair.getChild(position).getLevel());
								resultIntent.putExtra("latitude", latitude);
								resultIntent.putExtra("longitude", longitude);
								setResult(RESULT_OK, resultIntent);
								finish();
							}
						}
					}.execute();
				}
			}
		}
	};
	
	private OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v){
			switch(v.getId()){
			case BUTTON_MODE_LEFT_BUTTON:
				if(currentPair.getParent() != null){
	    			currentPair = currentPair.getParent();
	    			
	    			items.clear();
	    			items.addAll(currentPair.getChildren());
	    			
	    			setTitleText(currentPair.getTitle());
	    			
	    			adapter.notifyDataSetChanged();
	    		}else{
	    			finish();
	    		}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_treemenu_node_has_child);
		setTitleView(BaseTitleActivity.TITLE_WITH_NAV_BUTTON, getString(R.string.activity_send_situation_title), onClickListener);
		setButtonMode(BaseTitleActivity.BUTTON_MODE_LEFT_BUTTON);
		
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(onItemClickListener);
		
		latitude = getIntent().getDoubleExtra("latitude", 0D);
		longitude = getIntent().getDoubleExtra("longitude", 0D);
		
		situationTree = new ActionPair(null, 1);
		parseXml();
		
		ActionPair[] recentSituation = RmDatabase.getInstance(this).getRecentSituation();
		if(recentSituation != null){
			situationTree.addChild(new ActionPair(getString(R.string.activity_send_situation_recent_situation_title), 1));
			situationTree.getChild(situationTree.getChildren().size() - 1).setChildren(Arrays.asList(recentSituation));
		}
		
		currentPair = situationTree;
		
		items = new ArrayList<ActionPair>();
		items.addAll(currentPair.getChildren());
		
		adapter = new TreeMenuAdapter(this, items);
		list.setAdapter(adapter);
		
	}
	
    private void parseXml(){
		
		XmlResourceParser xrp = getResources().getXml(R.xml.situation_tree);
		ActionPair tempActionPair = situationTree;
		int depth = 0;
		
		try{
			while(xrp.getEventType() != XmlResourceParser.END_DOCUMENT){
				switch(xrp.getEventType()){
				case XmlResourceParser.START_TAG:
					String tagName = xrp.getName();
					if(tagName.equals("situation")){
						depth = xrp.getDepth();
						tempActionPair.setTitle(xrp.getAttributeValue(null, "name"));
					}
					if(tagName.equals("tree")){
						
						if(xrp.getDepth() > depth){
							depth = xrp.getDepth();
							
							if(tempActionPair.getChildren().size() > 0) tempActionPair = tempActionPair.getChild(tempActionPair.getChildren().size() - 1);
						}
						
						tempActionPair.addChild(new ActionPair(xrp.getAttributeValue(null, "name"), xrp.getAttributeIntValue(null, "level", tempActionPair.getLevel())));
					}
					break;
				case XmlResourceParser.END_TAG:
					if(xrp.getDepth() < depth){
						depth = xrp.getDepth();
						if(tempActionPair.getParent() != null){
							tempActionPair = tempActionPair.getParent();
						}
						
					}
					break;
				}
				
				xrp.next();
			}
		}catch(XmlPullParserException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			xrp.close();
		}
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		if(currentPair.getParent() != null){
    			currentPair = currentPair.getParent();
    			
    			items.clear();
    			items.addAll(currentPair.getChildren());
    			
    			setTitleText(currentPair.getTitle());
    			
    			adapter.notifyDataSetChanged();
    			return true;
    		}
    	}
    	
    	return super.onKeyDown(keyCode, event);
    }
}
