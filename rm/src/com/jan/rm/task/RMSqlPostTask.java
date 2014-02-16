package com.jan.rm.task;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import com.jan.rm.utils.Constants;

public class RMSqlPostTask extends HttpRequestTask<Integer> {
	
	public static final int RESULT_OK = 1;
	public static final int RESULT_FAIL = -1;

	public RMSqlPostTask(String queryString) {
		super(Constants.MYSQL_DATABASE_ADDR_EXECUTE, new BasicNameValuePair("query_string", queryString));
	}
	
	@Override
	protected Integer doInBackground(Void... params){
		super.doInBackground(params);
		
		try{
			JSONArray jsonArray = new JSONArray(result);
			
			return jsonArray.getJSONObject(0).optInt("rows");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return -1;
	}

}
