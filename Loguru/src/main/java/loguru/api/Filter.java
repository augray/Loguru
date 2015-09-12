package loguru.api;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Filter {
	
	private BooleanProperty isActive = new SimpleBooleanProperty(true);
	private StringProperty filterName = new SimpleStringProperty();
	
	public boolean applyFilter(String lineGroup){
		if(isActive()){
			return passesFilter(lineGroup);
		} else {
			return true;
		}
	}
	
	public boolean isActive(){
		return isActive.get();
	}
	
	public void isActive(boolean isActive){
		this.isActive.set(isActive);
	}
	
	public BooleanProperty isActiveProperty(){
		return isActive;
	}
	
	protected abstract boolean passesFilter(String lineGroup);
	
	
	public String getFilterName(){
		return filterName.get();
	}
	
	public void setFilterName(String name){
		this.filterName.set(name);
	}
	
	public StringProperty filterNameProperty(){
		return this.filterName;
	}
}
