package com.jan.rm.task;

public class HttpGetTask<T> extends NetWorkTask<T> {
	
	String result;
	
	public HttpGetTask(String url, String queryString){
		super();
		
		this.url = url;
		this.queryString = queryString;
	}

	@Override
	protected T doInBackground(Void... params) {
		
		try{
			result = httpGet(url, queryString);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

}
