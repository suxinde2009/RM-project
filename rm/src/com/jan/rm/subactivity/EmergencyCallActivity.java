package com.jan.rm.subactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jan.rm.R;
import com.jan.rm.adapter.EmergencyCallAdapter;
import com.jan.rm.baseactivity.BaseTitleActivity;
import com.jan.rm.dao.ds.ActionPair;
import com.jan.rm.dao.ds.TelActionPair;
import com.jan.rm.err.FeatureException;

public class EmergencyCallActivity extends BaseTitleActivity {
	
	private ListView listView;
	
	private List<ActionPair> items;
	private EmergencyCallAdapter adapter;
	
	private ActionPair telTree;
	private ActionPair currentPair;
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
			if(currentPair.getChild(position) != null && currentPair.getChild(position).hasChild()){
				currentPair = currentPair.getChild(position);
				
				setTitleText(currentPair.getTitle());
				
				items.clear();
				for(ActionPair pair : currentPair.getChildren()){
					items.add(pair);
				}
				
				adapter.notifyDataSetChanged();
			}else{
				if(!isProgressing()){
					Intent intent = new Intent();
					intent.setAction("android.intent.action.CALL");
					intent.setData(Uri.parse("tel:" + ((TelActionPair) items.get(position)).getNumber()));
					startActivity(intent);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency_call);
		setTitleView(BaseTitleActivity.TITLE_ONLY_TITLE_TEXT, getString(R.string.activity_emergency_call_title), null);
		
		listView = (ListView) findViewById(R.id.list);
		
		telTree = new TelActionPair("", "");
		parseXml();
		currentPair = telTree;
		
		items = new ArrayList<ActionPair>();
		for(ActionPair pair : currentPair.getChildren()){
			items.add(pair);
		}
		
		adapter = new EmergencyCallAdapter(this, items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(onItemClickListener);
	}
	
    private void parseXml(){
		
		XmlResourceParser xrp = getResources().getXml(R.xml.emergency_call_tree);
		ActionPair tempActionPair = telTree;
		int depth = 0;
		
		try{
			while(xrp.getEventType() != XmlResourceParser.END_DOCUMENT){
				switch(xrp.getEventType()){
				case XmlResourceParser.START_TAG:
					String tagName = xrp.getName();
					if(tagName.equals("emergency")){
						depth = xrp.getDepth();
						tempActionPair.setTitle(xrp.getAttributeValue(null, "name"));
					}
					if(tagName.equals("tree")){
						
						if(xrp.getDepth() > depth){
							depth = xrp.getDepth();
							
							if(tempActionPair.getChildren().size() > 0) tempActionPair = tempActionPair.getChild(tempActionPair.getChildren().size() - 1);
						}
						
						tempActionPair.addChild(new TelActionPair(xrp.getAttributeValue(null, "name"), xrp.getAttributeValue(null, "value")));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			xrp.close();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
			new FeatureException(this, FeatureException.FEATURE_TELEPHONY_UNAVAILABLE);
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		if(currentPair.getParent() != null){
    			currentPair = currentPair.getParent();
    			
    			items.clear();
    			for(ActionPair pair : currentPair.getChildren()){
    				items.add(pair);
    			}
    			
    			setTitleText(currentPair.getTitle());
    			
    			adapter.notifyDataSetChanged();
    			return true;
    		}
    	}
    	
    	return super.onKeyDown(keyCode, event);
    }
}
