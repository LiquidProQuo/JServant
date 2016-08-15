package javaservant.view;

import com.google.gson.JsonObject;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by Josh on 7/17/2016.
 */
public class JServantWebFrame {
	private static final int DEFAULT_WINDOW_HEIGHT = 385;
	private static final int DEFAULT_WINDOW_WIDTH = 425;
	private static final int WINDOW_HEIGHT_BUFFER = 38;
	private static final String JAVASCRIPT_BRIDGE_NAME = "javaServant";
	private static final String WEB_PATH = "web/main.html";

	private Bridge bridge;
	private WebEngine webEngine;
	private Stage primaryStage;

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

	private void resize(Integer width, Integer height) {
		if (width != null) {
			primaryStage.setWidth(width);
		}
		if (height != null) {
			primaryStage.setHeight(height);
		}
	}

	public double getWidth() {
		return primaryStage.getWidth();
	}

	public void populateVariables(List<JsonObject> varMapJson) {
		JSObject window  = (JSObject) webEngine.executeScript("window");
		window.setMember("variableMapList", varMapJson);
		webEngine.executeScript("populateVariableList();");
		int windowHeight = varMapJson.isEmpty() ? DEFAULT_WINDOW_HEIGHT + WINDOW_HEIGHT_BUFFER :
				28 + (int)webEngine.executeScript("Math.max($(document).height(), $(window).height())");
		System.out.println("Window inner height: " + windowHeight);
		resize(null, windowHeight);
	}

	public void resetFrame() {
		populateVariables(Collections.EMPTY_LIST);
	}

	@SuppressWarnings("unchecked")
	public <T> T getClientVariable(String variableName) {
		return (T) webEngine.executeScript("variableName");
	}

}
