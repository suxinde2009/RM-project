package com.jan.rm.task;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import com.jan.rm.utils.Constants;

public class RMSqlGetTask extends HttpRequestTask<JSONArray> {

	public RMSqlGetTask(String queryString) {
		super(Constants.MYSQL_DATABASE_ADDR_READ, new BasicNameValuePair("query_string", queryString));
	}
	
	@Override
	protected JSONArray doInBackground(Void... params){
		super.doInBackground(params);
		
		JSONArray jsonArray = null;
		
		try{
			jsonArray = new JSONArray(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return jsonArray;
	}
}

