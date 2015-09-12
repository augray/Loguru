package loguru.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import loguru.api.Filter;
import loguru.api.FilterApplicator;
import loguru.api.PassAllFilter;
import loguru.services.WorkPool;
import loguru.util.LoguruAlert;
import loguru.util.ThreadUtil.Stopable;

public class LogTab extends Tab implements Initializable{

	private static final String FXML = "/fxml/logTab.fxml";
	private static final Logger logger = Logger.getLogger(LogTab.class.getCanonicalName());
	
	@FXML private Button browseButton;
	@FXML private TextField selectedFileText;
	@FXML private TextArea logContents;
	
	/**
	 * The log file for this tab.
	 * Guarded by its intrinsic lock.
	 */
	ObjectProperty<File> selectedLog = new SimpleObjectProperty<>(); 
	
	public LogTab() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		browseButton.setOnAction((e)->selectLogFile());
		selectedLog.addListener((e)->logChanged());
	}

	private void logChanged() {
		synchronized (selectedLog) {
			File logFile = selectedLog.get();
			if(!logFile.isFile()){
				throw new IllegalStateException("Cannot use non-file '"+logFile+"'.");
			}
			this.setText(logFile.getName());
			this.selectedFileText.setText(logFile.getAbsolutePath());
			
			applyFilter();
		}
	}

	private void applyFilter() {
		synchronized (selectedLog) {
			WorkPool.getInstance().submit(new FilterJob(selectedLog.get(), new PassAllFilter(), new FilterJobResultReceiver(){

				@Override
				public void receiveResult(final String filteredLog,
						final File filteredFile, final Filter filterApplied, final Throwable problem) {
					//receive the result on the UI Thread
					Platform.runLater(()->{						
						synchronized (selectedLog) {
							//TODO: also synchronize on selected filter and  check selected filter.
							if(selectedLog.get()==filteredFile){
								if(filteredLog!=null){
									//WorkPool.getInstance().submit(()->streamText(logContents,filteredLog));
									logContents.setText(filteredLog);
								}
							} else {								
								String message = "Error applying filter '"+filterApplied.getFilterName()+"' to log "+filteredFile.getAbsolutePath()+"contents.";
								LoguruAlert.error(message);
								logger.log(Level.SEVERE, message,problem);
							}
						}
						
					});
				}
			}));
		}
	}
	
//	private static void streamText(final TextArea area,String text){
//		Platform.runLater(()->area.setText(""));
//		
//		int nLinesAtATime = 100;
//		final Scanner textScanner = new Scanner(text);
//		while(textScanner.hasNext()){
//			StringBuilder sb = new StringBuilder();
//			int nLines = 0;
//			while(textScanner.hasNext() && nLines<nLinesAtATime){
//				sb.append(textScanner.nextLine()+"\n");
//				nLines++;
//			}
//			String nextText = sb.toString();
//			Platform.runLater(()->{
//				area.appendText(nextText);
//			});
//			
//			try{				
//				Thread.sleep(5);
//			} catch(InterruptedException ignored){
//				
//			}
//		}
//	}
	
	public void findNext(String toFind){
		find(toFind,true);
	}
	
	public void findPrev(String toFind){
		find(toFind, false);
	}
	
	private void find(String toFind, boolean forward){
		Platform.runLater(()->{
			String text = logContents.getText();
			
			if(text==null || text.equals("")){
				return;
			}
			
			int wordStartIndex = -1;
			
			int currentIndex = (forward ? logContents.selectionProperty().get().getEnd() : logContents.selectionProperty().get().getStart()-1);
			if(forward){			
				wordStartIndex = text.indexOf(toFind, currentIndex);
			} else {
				wordStartIndex = text.lastIndexOf(toFind, currentIndex);
			}
			
			//loop search if necessary by starting at 
			//the extremes of the front or back
			if(wordStartIndex<0){
				if(forward){			
					wordStartIndex = text.indexOf(toFind);
				} else {
					wordStartIndex = text.lastIndexOf(toFind);
				}
			}
			
			if(wordStartIndex>=0){
				logContents.selectRange(wordStartIndex,wordStartIndex+toFind.length());
			}
		});
	}

	private void selectLogFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Log File To Open");
		File selectedFile = fileChooser.showOpenDialog(LoguruMainWindow.getMainWindow());
		if(selectedFile!=null){			
			selectLogFile(selectedFile);
		}
	}
	
	private void selectLogFile(File file){
		synchronized (file) {			
			selectedLog.set(file);
		}
	}

	private static interface FilterJobResultReceiver{
		public void receiveResult(String filteredLog, File filteredFile, Filter filterApplied, Throwable problem);
	}
	
	private static class FilterJob implements Callable<String>, Stopable{
		
		private final File fileToFilter;
		private final Filter filterToApply;
		private final FilterJobResultReceiver receiver;
		
		/**
		 * Applies the filter to the file.
		 * 
		 * Guarded by applicatorLock.
		 */
		private FilterApplicator applicator = null;
		
		private Object applicatorLock = new Object();
		
		public FilterJob(File fileToFilter, Filter filterToApply, FilterJobResultReceiver receiver){
			if(fileToFilter==null){
				throw new IllegalArgumentException("fileToFilter cannot be null");
			}
			if(filterToApply==null){
				throw new IllegalArgumentException("filterToApply cannot be null");
			}
			this.fileToFilter = fileToFilter;
			this.filterToApply = filterToApply;
			this.receiver = receiver;
		}
		
		@Override
		public String call() throws Exception {
			try{
				String filteredLog;
				synchronized (applicatorLock) {					
					applicator = new FilterApplicator(filterToApply, fileToFilter);
					filteredLog = applicator.applyFilter();
				}
				
				if(receiver!=null){
					receiver.receiveResult(filteredLog, fileToFilter, filterToApply,null);
				}
				return filteredLog;
			} catch(Throwable t){
				if(receiver!=null){
					receiver.receiveResult(null, fileToFilter, filterToApply, t);
				} else {
					String message = "Error applying filter '"+filterToApply.getFilterName()+"' to log "+fileToFilter.getAbsolutePath()+"contents.";
					logger.log(Level.SEVERE, message,t);
				}
			}
			
			return null;
		}

		@Override
		public void stop() {
			synchronized (applicatorLock) {
				applicator.stop();
			}
		}
		
	}
}
