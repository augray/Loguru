package loguru.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtil {
	
	public static ThreadFactory namingFactory(String poolName){
		return new ThreadNamingFactory(poolName);
	}
	
	public static class ThreadNamingFactory implements ThreadFactory{
		private final AtomicInteger threadId = new AtomicInteger(0);
		private final String poolName;
		
		public ThreadNamingFactory(String poolName){
			this.poolName = poolName;
		}
		
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, poolName+"-"+threadId.getAndIncrement());
			return t;
		}
		
	}
	

	public static interface CancelCallback {
		public void cancelled();
	}
	
	public static interface FinishedCallback<T> {
		public void finished(T result);
	}
	
	public static class CallbackCallable<T> implements Callable<T>{
		private final FinishedCallback<T> callback;
		private final Callable<T> callable;
		public CallbackCallable(Callable<T> callable ,FinishedCallback<T> callback){
			this.callback = callback;
			this.callable = callable;
		}
		
		@Override
		public T call() throws Exception {
			T result = null;
			if(this.callable!=null){
				result = callable.call();
			}
			if(callback!=null){
				callback.finished(result);
			}
			
			return result;
		}
		
	}
	
	public static interface Stopable{
		public void stop();
	}
}
