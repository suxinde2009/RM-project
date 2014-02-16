package com.jan.rm.subactivity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.jan.rm.R;
import com.jan.rm.adapter.ExplanationAdapter;
import com.jan.rm.baseactivity.BaseTitleActivity;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

public class ExplanationActivity extends BaseTitleActivity{
	
	private ActionSlideExpandableListView list;
	private ExplanationAdapter adapter;
	
	private String[] questionsString;
	private String[] answersString;
	
	private List<String[]> items;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explanation);
		setTitleView(TITLE_ONLY_TITLE_TEXT, getString(R.string.activity_explanation_title), null);
		
		list = (ActionSlideExpandableListView) findViewById(R.id.list);
		
		questionsString = getResources().getStringArray(R.array.intruduction_title_list);
		answersString = getResources().getStringArray(R.array.intruduction_content_list);
		
		items = new ArrayList<String[]>();
		for(int i = 0; i < questionsString.length; i++){
			items.add(new String[]{questionsString[i], answersString[i]});
		}
		
		adapter = new ExplanationAdapter(this, items);
		list.setAdapter(adapter);
	}

}
