package javaservant.view;

import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javaservant.bridge.Bridge;
import javaservant.core.JSThreadListener;
import javaservant.core.JavaServant;
import netscape.javascript.JSObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by Josh on 7/17/2016.
 */
public class JServantWebFrame implements JSThreadListener{
	private static final int DEFAULT_WINDOW_HEIGHT = 385;
	private static final int DEFAULT_WINDOW_WIDTH = 425;
	private static final String JAVASCRIPT_BRIDGE_NAME = "javaServant";
	private static final String WEB_PATH = "web/dialog.html";

	Bridge bridge;
	WebEngine webEngine;

	public void init(JavaServant js, Stage primaryStage) throws MalformedURLException, URISyntaxException {
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

	@Override
	public void onJsThreadStateChange(String newState, String message) {
		updateRunState(newState);
	}
}
