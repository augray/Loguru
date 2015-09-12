package loguru.services;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import loguru.ui.LoguruMainWindow;
import loguru.util.ThreadUtil;
import loguru.util.ThreadUtil.CallbackCallable;
import loguru.util.ThreadUtil.FinishedCallback;


/**
 * The work pool is a thread pool designed
 * to allow for the queueing of and execution
 * of long-running tasks that cannot or should
 * not be performed on the UI thread. this 
 * includes, for example, tasks for filtering.
 * 
 */
public class WorkPool implements AutoCloseable{

	private static final WorkPool instance = new WorkPool();
	
	/**
	 * The pool which performs the work.
	 */
	private final ExecutorService pool = Executors.newFixedThreadPool(10, ThreadUtil.namingFactory("workPool"));
	
	public static WorkPool getInstance(){
		return instance;
	}
	
	public WorkPool(){
		LoguruMainWindow.registerShutdownListener(this);
	}
	
	public <T> Future<T> submit(Callable<T> job){
		return this.submit(job, (t)->{/*do nothing on completion*/});
	}
	
	public <T> Future<T> submit(Callable<T> job, FinishedCallback<T> finishedCallback){
		return pool.submit(new CallbackCallable<T>(job,finishedCallback));
	}
	
	public Future<Void> submit(Runnable job){
		return submit(()->{
			job.run();
			return null;
		});
	}
	
	public Future<Void> submit(Runnable job, FinishedCallback<Void> finishedCallback){
		return submit(()->{
			job.run();
			return null;
		},
		finishedCallback);
	}
	
	@Override
	public void close() throws Exception {
		pool.shutdownNow();
	}

}
