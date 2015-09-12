package loguru.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import loguru.util.ThreadUtil.Stopable;


public class FilterApplicator implements AutoCloseable, Stopable{
	
	private final Filter filter;
	
	private volatile boolean stopped = false;
	
	/**
	 * The stream to apply the filter to.
	 * 
	 * Guarded by sourceLock
	 */
	private InputStream source;
	
	/**
	 * A scanner to scan the source. 
	 * 
	 * Guarded by sourceLock. 
	 */
	private Scanner scanner = null;
	
	private final Object sourceLock = new Object();
	
	public FilterApplicator(Filter filter){
		this.filter = filter;
	}
	
	public FilterApplicator(Filter filter, InputStream source){
		this(filter);
		this.source = source;
	}
	
	public FilterApplicator(Filter filter, File fileSource) throws FileNotFoundException{
		this(filter);
		this.source = new FileInputStream(fileSource);
	}
	
	public String applyFilter(){
		if(filter==null){
			throw new IllegalStateException("Cannot apply filter when filter is null.");
		}
		synchronized (sourceLock) {			
			if(source==null){
				throw new IllegalStateException("Cannot apply filter to null source.");
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		String lineGroup = null;
		boolean hasMore = true;
		while(hasMore && !stopped){
			lineGroup = getNextLineGroup();
			if(lineGroup!=null){ 
				if(filter.applyFilter(lineGroup)){
					sb.append(lineGroup);
				}
			} else {
				hasMore = false;
			}
		}
		
		return sb.toString();
	}

	private String getNextLineGroup() {
		synchronized (sourceLock) {
			if(scanner==null){
				scanner = new Scanner(this.source);
			}
			String group = null;
			if(scanner.hasNext()){
				//For now, lines aren't grouped. Just return next line.
				group = scanner.nextLine()+"\n";
			} else {				
				scanner.close();
			}
			
			return group;
		}
	}

	public void stop(){
		this.stopped = true;
	}
	
	@Override
	public void close() throws Exception {
		this.stop();
		synchronized (sourceLock) {
			if(source!=null){
				source.close();
			}
			if(scanner!=null){
				scanner.close();
			}
		}
	}
}
