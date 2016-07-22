package javaservant.view;

import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javaservant.bridge.Bridge;
import javaservant.core.JavaServant;
import netscape.javascript.JSObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by Josh on 7/17/2016.
 */
public class JServantWebFrame {
	private static final int DEFAULT_WINDOW_HEIGHT = 385;
	private static final int DEFAULT_WINDOW_WIDTH = 425;
	public static final int HEIGHT_INCREMENT_AMOUNT = 46;
	private static final String JAVASCRIPT_BRIDGE_NAME = "javaServant";
	private static final String WEB_PATH = "web/main.html";

	Bridge bridge;
	WebEngine webEngine;
	Stage primaryStage;

	public void init(JavaServant js, Stage primaryStage) throws MalformedURLException, URISyntaxException {
		this.primaryStage = primaryStage;
		primaryStage.setTitle(JavaServant.FRAME_TITLE);

		WebView browser = new WebView();
		webEngine = browser.getEngine();

		ClassLoader classLoader = getClass().getClassLoader();
		webEngine.load(classLoader.getResource(WEB_PATH).toString());

		bridge = new Bridge(js, this);
		webEngine.setOnAlert(e -> System.out.println("alert: " + e.getData()));
		webEngine.getLoadWorker().stateProperty().addListener((ov, t, t1) -> {
			System.out.println("Java Init...");
			if (t1 == Worker.State.SUCCEEDED) {
				JSObject window  = (JSObject) webEngine.executeScript("window");
				window.setMember(JAVASCRIPT_BRIDGE_NAME, bridge);

				//populate clientSide script list
				window.setMember("scriptListArray", js.getDefaultScriptListing());
				webEngine.executeScript("populateScriptList();");
			}
		});

		primaryStage.setScene(new Scene(browser, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT));
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	public void updateRunState(String runState) {
		webEngine.executeScript("updateScriptStatus('" + runState + "');");
	}

	public void resize(Integer width, Integer height) {
		if (width != null) {
			primaryStage.setWidth(width);
		}
		if (height != null) {
			primaryStage.setHeight(height);
		}
	}

	public double getHeight() {
		return primaryStage.getHeight();
	}

	public double getWidth() {
		return primaryStage.getWidth();
	}

}
