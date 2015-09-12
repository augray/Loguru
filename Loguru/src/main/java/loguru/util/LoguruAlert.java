package loguru.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class LoguruAlert extends Alert{
	
	
	public LoguruAlert(AlertType arg0) {
		super(arg0);
	}

	public static void error(String errorText){
		Platform.runLater(()->{			
			LoguruAlert alert = new LoguruAlert(AlertType.ERROR);
			alert.setTitle("Loguru error");
			alert.setContentText(errorText);
			alert.show();
		});
	}
}
