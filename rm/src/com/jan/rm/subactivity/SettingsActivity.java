package com.jan.rm.subactivity;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;

import com.jan.rm.R;
import com.jan.rm.adapter.SettingsAdapter;
import com.jan.rm.baseactivity.BaseTitleActivity;

public class SettingsActivity extends BaseTitleActivity {
	
	private SettingsAdapter adapter;
	private ListView list;
	private List<String> items;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setTitleView(TITLE_ONLY_TITLE_TEXT, getString(R.string.activity_settings_title), null);
		
		list = (ListView) findViewById(R.id.list);
		
		items = Arrays.asList(getResources().getStringArray(R.array.settings_menu));
		
		adapter = new SettingsAdapter(this, items);
		list.setAdapter(adapter);
	}
}
