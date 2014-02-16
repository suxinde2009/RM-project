package com.jan.rm.err;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
public class ExceptionHandler {
	
	private Context context;
	
	private static ExceptionHandler exceptionHandler;

	public ExceptionHandler(Context context){
		this.context = context;
	}
	
	public static ExceptionHandler getInstance(Context context){
		if(exceptionHandler == null){
			exceptionHandler = new ExceptionHandler(context);
		}
		
		return exceptionHandler;
	}
	
	public void print(String exception){
		Log.e(this.getClass().getName(), exception);
	}
	
	public void alert(String title, String exception){
		//new AlertDialog.Builder(context).setTitle(title).setMessage(exception).setPositiveButton("确定", null).create().show();
	}
	
	public void toast(String exception){
		Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
	}
	
	public Context getContext(){
		return context;
	}
}
