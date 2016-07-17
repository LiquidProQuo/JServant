package javaservant.view;

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
	private static final String JAVASCRIPT_BRIDGE_NAME = "javaServant";
	private static final String WEB_PATH = "web/views/dialog.html";

	Bridge bridge;

	public void init(JavaServant js, Stage primaryStage) throws MalformedURLException, URISyntaxException {
		primaryStage.setTitle(JavaServant.FRAME_TITLE);

		WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();

		ClassLoader classLoader = getClass().getClassLoader();
		webEngine.load(classLoader.getResource(WEB_PATH).toString());
		//webEngine.load("https://google.com");

		JSObject window  = (JSObject) webEngine.executeScript("window");
		bridge = new Bridge(js, webEngine);
		window.setMember(JAVASCRIPT_BRIDGE_NAME, bridge);

		primaryStage.setScene(new Scene(browser, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT));

		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
