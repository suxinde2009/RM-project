package com.jan.rm.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

public class HttpRequestTask<T> extends NetWorkTask<T> {
	
	String result;
	
	private List<BasicNameValuePair> parameters;
	
	public HttpRequestTask(String url, BasicNameValuePair... params){
		super();
		this.url = url;
		parameters = new ArrayList<BasicNameValuePair>();
		for(BasicNameValuePair param  : params){
			parameters.add(param);
		}
	}

	@Override
	protected T doInBackground(Void... params) {
		try{
			result = httpPost(url, parameters);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}
