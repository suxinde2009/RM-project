package com.jan.rm.task;

public class HttpPostTask<T> extends NetWorkTask<T>{
	
	String result;
	
	public HttpPostTask(String url, String queryString){
		super();
		this.url = url;
		this.queryString = queryString;
	}
	
	@Override
	protected T doInBackground(Void... params){
		
		try{
			result = httpPost(url, queryString);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

}
