package loguru.ui;
import java.io.IOException;
import java.io.InputStream;
import java.lang.AutoCloseable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import loguru.util.StyleSheets;

public class LoguruMainWindow extends Application{

	private static final String FXML = "/fxml/LoguruMainWindow.fxml";

	private static final Logger logger = Logger.getLogger(LoguruMainWindow.class.getCanonicalName()); 
	
	private static volatile Window mainWindow;
	
	private static final List<AutoCloseable> shutdownListeners = Collections.synchronizedList(new ArrayList<AutoCloseable>());
	
	public static Window getMainWindow(){
		return mainWindow;
	}
	
	public static void main(String[] args) {
	    try{	    	
	    	if(Arrays.asList(args).contains("-debug")){
	    		logger.setLevel(Level.ALL);
	    	}
	    	
	    	launch(args);
	    } catch(Throwable t){
	    	logger.log(Level.SEVERE, "Fatal Exception in Loguru",t);
	    } finally {
	    	notifyShutdownListeners();
	    }
	}
	
	public static void registerShutdownListener(AutoCloseable shutdownListener){
		shutdownListeners.add(shutdownListener);
	}
	
	public static void unregisterShutdownListener(AutoCloseable shutdownListener){
		shutdownListeners.remove(shutdownListener);
	}
	
	private static void notifyShutdownListeners(){
		shutdownListeners.stream().forEach((l)->{
			try{
				l.close();
			} catch(Throwable t){
				logger.log(Level.SEVERE, "Shutdown listener failed.",t);
			}
		});
	}
	
	@Override
    public void start(Stage stage) {
		try{
			mainWindow = stage;
			Parent root = FXMLLoader.load(getClass().getResource(FXML));
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(StyleSheets.BASE_STYLE);
			
			stage.setMinWidth(((BorderPane)root).getMinWidth());
			stage.setMinHeight(((BorderPane)root).getMinHeight());
			
			loadLogo(stage);
			
			stage.setTitle("Loguru");
			stage.setScene(scene);
			stage.show();
		} catch(IOException e){
			logger.log(Level.SEVERE, "Error loading main FXML", e);
		}
    }

	private void loadLogo(Stage stage) {
		
		List<String> logoPaths = Arrays.asList("/icons/loguruLogo.png",
				"/icons/loguruLogo_16.png",
				"/icons/loguruLogo_24.png",
				"/icons/loguruLogo_32.png",
				"/icons/loguruLogo_48.png",
				"/icons/loguruLogo_64.png",
				"/icons/loguruLogo_96.png",
				"/icons/loguruLogo_128.png");
		for(String logoPath : logoPaths){			
			InputStream logoStream = LoguruMainWindow.class.getResourceAsStream(logoPath);
			if(logoStream!=null){				
				Image logo = new Image(logoStream);
				stage.getIcons().add(logo);
				logger.log(Level.FINEST, "Loaded Loguru logo. with path '"+logoPath+"'");
			} else {
				logger.warning("Failed to find Loguru logo with path '"+logoPath+"'");
			}
		}
	}
}
