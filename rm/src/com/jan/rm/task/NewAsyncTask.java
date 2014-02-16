package com.jan.rm.task;

import com.jan.rm.concurrent.RunningExecutor;

import android.os.AsyncTask;

public abstract class NewAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	public void executeOnRunningExecutor(RunningExecutor exec, Params... params){
		exec.setTask(this);
		
		super.executeOnExecutor(exec, params);
	}
	
}
