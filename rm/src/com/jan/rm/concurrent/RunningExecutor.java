package com.jan.rm.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;

public class RunningExecutor extends ThreadPoolExecutor {
	
	public static final int TASK_FIRST = 0;
	
	private List<Runnable> runningTasks = Collections.synchronizedList(new ArrayList<Runnable>());
	private AsyncTask<?, ?, ?> oldestTask;
	
	public RunningExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r){
		super.beforeExecute(t, r);
		runningTasks.add(r);
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t){
		super.afterExecute(r, t);
		runningTasks.remove(r);
	}
	
	public Runnable getRunningTask(int position){
		return runningTasks.get(position);
	}
	
	public void setTask(AsyncTask<?, ?, ?> task){
		oldestTask = task;
	}
	
	public AsyncTask<?, ?, ?> getTask(){
		return oldestTask;
	}
}
