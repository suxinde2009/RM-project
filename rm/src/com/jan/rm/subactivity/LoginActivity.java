package com.jan.rm.subactivity;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.jan.rm.baseactivity.BaseTitleActivity;
import com.jan.rm.dao.RmPreferenceManager;
import com.jan.rm.dao.RmServerApi;
import com.jan.rm.entity.rm.RMUser;
import com.jan.rm.logger.RLog;
import com.jan.rm.task.RMSqlGetTask;
import com.jan.rm.widget.RMToast;

import com.jan.rm.GoogleMapActivity;
import com.jan.rm.R;

public class LoginActivity extends BaseTitleActivity {
	
	private EditText account;
	private EditText password;
	
	private Button loginButton;
	
	private OnClickListener onClickListener = new OnClickListener(){
		@Override
		public void onClick(View v){
			switch(v.getId()){
			case R.id.login_button:
				verify();
				break;
			}
		}
	};
	
	private OnEditorActionListener onEditorActionListener = new OnEditorActionListener(){

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			switch(actionId){
			case EditorInfo.IME_ACTION_DONE:
				verify();
				break;
			case EditorInfo.IME_ACTION_NEXT:
				password.requestFocus();
				break;
			}
			return true;
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitleView(BaseTitleActivity.TITLE_ONLY_TITLE_TEXT, getString(R.string.activity_login_title), null);
		
		account = (EditText) findViewById(R.id.account);
		password = (EditText) findViewById(R.id.password);
		
		loginButton = (Button) findViewById(R.id.login_button);
		
		account.setOnEditorActionListener(onEditorActionListener);
		password.setOnEditorActionListener(onEditorActionListener);
		
		loginButton.setOnClickListener(onClickListener);
	}
	
	private void verify(){
		new RMSqlGetTask(RmServerApi.verifyAccount(account.getText().toString(), password.getText().toString())){
			@Override
			protected void onPreExecute(){
				LoginActivity.this.setProgressing(true, LoginActivity.this.getString(R.string.activity_login_loging_in));
			}
			@Override
			protected void onPostExecute(JSONArray result){
				LoginActivity.this.setProgressing(false, null);
				RMUser user = null;
				try {
					user = RmServerApi.parseJSONForUser(result.getJSONObject(0));
					if((account.getText().toString().equals(user.getUserId()) || account.getText().toString().equals(user.getEmail())) && password.getText().toString().equals(user.getPassword())){
						RmPreferenceManager.getInstance(LoginActivity.this.getApplicationContext()).setPassword(user.getPassword());
						RmPreferenceManager.getInstance(LoginActivity.this.getApplicationContext()).setUserId(user.getUserId());
						
						startActivity(new Intent(LoginActivity.this, GoogleMapActivity.class));
						LoginActivity.this.finish();
					}else{
						RMToast.showNegative(LoginActivity.this, LoginActivity.this.getString(R.string.login_failure));
					}
					
					RLog.d(user.getUserId(), account.getText().toString());
					RLog.d(user.getPassword(), password.getText().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}
		}.execute();
	}
}
