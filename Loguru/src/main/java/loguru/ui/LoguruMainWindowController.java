package loguru.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import loguru.ui.customControls.FindBar;
import loguru.ui.LogTab;

public class LoguruMainWindowController implements Initializable{

	@FXML TabPane logTabs;
	@FXML Tab newTabTab;
	@FXML FindBar findBar;
	
	private void handleNewTab(){
		if(newTabTab.isSelected()){
			List<Tab> tabs = logTabs.getTabs();
			int newTabIndex = tabs.size()-1;
			if(newTabIndex < 0){
				newTabIndex = 0;
			}
			LogTab newTab = new LogTab();
			tabs.add(newTabIndex,newTab);
			logTabs.getSelectionModel().select(newTab);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		newTabTab.setOnSelectionChanged((e)->handleNewTab());
		
		findBar.addOnFindHandler((text,forward)->{
			LogTab selectedTab = getSelectedLogTab();
			if(selectedTab!=null){
				if(forward){
					selectedTab.findNext(text);
				} else {
					selectedTab.findPrev(text);
				}
			}
		});
	}

	private LogTab getSelectedLogTab(){
		return (LogTab)this.logTabs.getSelectionModel().getSelectedItem();
	}

}
