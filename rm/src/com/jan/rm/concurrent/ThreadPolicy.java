package com.jan.rm.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import android.os.AsyncTask;

public class ThreadPolicy {
	
	public static class DumpOldestPolicy implements RejectedExecutionHandler{

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			if(!executor.isShutdown()){
				
				if(executor instanceof RunningExecutor){
					AsyncTask<?, ?, ?> task = ((RunningExecutor) executor).getTask();
					if(task != null){
						task.cancel(true);
					}
				}
				
				
			}
		}
		
	}
}
