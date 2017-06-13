package util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolExecutorRunnable {
	
	private Runnable runnable;	
	private int corePoolSize; 
	private int maxPoolSize;
	private long keepAlive;
	private TimeUnit unit;
	private BlockingQueue<Runnable> workQueue;
	private RejectedExecutionHandler handler;

	private  ExecutorService executor;
	
	public PoolExecutorRunnable(Runnable runnable) {
		 this.runnable = runnable;
		 
		 this.corePoolSize = 5;
         this.maxPoolSize = 10;
         this.keepAlive = 100L;
         this.unit = TimeUnit.MILLISECONDS;
         this.workQueue = new LinkedBlockingQueue<Runnable>(30);
         this.handler = new ThreadPoolExecutor.CallerRunsPolicy();
         
	}
	
	private void init(){ 
         this.executor = new ThreadPoolExecutor(
 				this.corePoolSize, 
 				this.maxPoolSize, 
 				this.keepAlive, 
 				this.unit,
 				this.workQueue, this.handler);
	}
	
	public void start(){
		init();		
		this.executor.submit(this.runnable);
	}
	
	public void stop(){
		System.out.println("thread stop!");
		this.executor.shutdown();
	}
}
