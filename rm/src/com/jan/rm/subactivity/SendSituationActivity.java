package com.jan.rm.subactivity;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.jan.rm.R;
import com.jan.rm.baseactivity.BaseTitleActivity;
import com.jan.rm.dao.ds.ActionPair;
import com.jan.rm.logger.RLog;
import com.jan.rm.widget.TreeMenu;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.KeyEvent;

public class SendSituationActivity extends BaseTitleActivity{
	
	private ActionPair situationTree;
	private TreeMenu treeMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_situation);
		setTitleView(BaseTitleActivity.TITLE_ONLY_TITLE_TEXT, getString(R.string.activity_send_situation_title), null);
		
		treeMenu = (TreeMenu) findViewById(R.id.tree_menu);
		
		situationTree = new ActionPair(null, 1);
		
		parseXml();
		treeMenu.setActionPair(situationTree);
	}
	
	private void parseXml(){
		
		XmlResourceParser xrp = getResources().getXml(R.xml.situation_tree);
		ActionPair tempActionPair = situationTree;
		int depth = 0;
		
		try{
			while(xrp.getEventType() != XmlResourceParser.END_DOCUMENT){
				RLog.d(depth + "", xrp.getDepth() + "");
				switch(xrp.getEventType()){
				case XmlResourceParser.START_TAG:
					String tagName = xrp.getName();
					if(tagName.equals("situation")){
						depth = xrp.getDepth();
						tempActionPair.setTitle(xrp.getAttributeValue(null, "name"));
					}
					if(tagName.equals("tree")){
						RLog.d("tree", "get");
						
						if(xrp.getDepth() > depth){
							depth = xrp.getDepth();
							
							if(tempActionPair.getChildren().size() > 0) tempActionPair = tempActionPair.getChild(tempActionPair.getChildren().size() - 1);
						}
						
						tempActionPair.addChild(new ActionPair(xrp.getAttributeValue(null, "name"), xrp.getAttributeIntValue(null, "level", 1)));
					}
					break;
				case XmlResourceParser.END_TAG:
					if(xrp.getDepth() < depth){
						depth = xrp.getDepth();
						if(depth > 0) tempActionPair = tempActionPair.getParent();
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
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(treeMenu.onKeyDown(keyCode, event)){
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

}
