package loguru.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class LoguruMainWindowController implements Initializable{

	@FXML TabPane logTabs;
	@FXML Tab newTabTab;
	@FXML TextField findText;
	@FXML Button findPrev;
	@FXML Button findNext;
	
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
		findNext.setOnAction((e)->doFindNext());
		findPrev.setOnAction((e)->doFindPrev());
	}

	private LogTab getSelectedLogTab(){
		return (LogTab)this.logTabs.getSelectionModel().getSelectedItem();
	}
	
	private void doFindPrev() {
		LogTab selectedTab = getSelectedLogTab();
		selectedTab.findPrev(findText.getText());
	}

	private void doFindNext() {
		LogTab selectedTab = getSelectedLogTab();
		selectedTab.findNext(findText.getText());
	}
}
