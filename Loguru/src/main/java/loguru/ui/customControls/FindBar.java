package loguru.ui.customControls;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class FindBar extends BorderPane implements Initializable {

	private static final Logger logger = Logger.getLogger(FindBar.class.getCanonicalName());
	
	@FXML TextField findText;
	@FXML Button findPrev;
	@FXML Button findNext;
	
	public static interface FindTextHandler{
		public void find(String text, boolean forward);
	}
	
	private List<FindTextHandler> findNextHandlers = Collections.synchronizedList(new ArrayList<>(1));
	private List<FindTextHandler> findPrevHandlers = Collections.synchronizedList(new ArrayList<>(1));;
	
	private static final String FXML = "/fxml/customControls/findBar.fxml";
	
	public FindBar() {
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
		findPrev.setOnAction((e)->notifyFindPrevHandlers());
		findNext.setOnAction((e)->notifyFindNextHandlers());
	}
	
	public void addOnFindHandler(FindTextHandler onFind){
		addOnFindNextHandler(onFind);
		addOnFindPrevHandler(onFind);
	}
	
	public void removeOnFindHandler(FindTextHandler onFind){
		removeOnFindNextHandler(onFind);
		removeOnFindPrevHandler(onFind);
	}
	
	public void addOnFindNextHandler(FindTextHandler onFindNext){
		this.findNextHandlers.add(onFindNext);
	}
	
	public void removeOnFindNextHandler(FindTextHandler onFindNext){
		this.findNextHandlers.remove(onFindNext);
	}
	
	public void addOnFindPrevHandler(FindTextHandler onFindPrev){
		this.findPrevHandlers.add(onFindPrev);
	}
	
	public void removeOnFindPrevHandler(FindTextHandler onFindPrev){
		this.findPrevHandlers.remove(onFindPrev);
	}
	
	private void notifyFindNextHandlers(){
		notifyHandlers(findNextHandlers, true /*isForward*/);
	}
	
	private void notifyFindPrevHandlers(){
		notifyHandlers(findPrevHandlers, false /*isBackward*/);
	}
	
	private void notifyHandlers(List<FindTextHandler> handlerList, boolean isForward){
		if(!Platform.isFxApplicationThread()){
			Platform.runLater(()->notifyHandlers(handlerList,isForward));
		} else {
			String textToFind = findText.getText();
			handlerList.stream().forEach((finder)->{
				try{					
					finder.find(textToFind,isForward);
				} catch(Exception e){
					logger.log(Level.WARNING, "Find text handler failed to find text.",e);
				}
			});
		}
	}
}
