package javaservant.bridge;

import javafx.scene.web.WebEngine;
import javaservant.core.JavaServant;

/**
 * Created by Josh on 7/17/2016.
 */
public class Bridge {
	private final JavaServant js;
	private final WebEngine webEngine;

	public Bridge(JavaServant js, WebEngine webEngine) {
		this.js = js;
		this.webEngine = webEngine;
	}

	public void sayHello() {
		System.out.println("Hello World");
	}

	public void runScript() {
		System.out.println("Run Script Called!");
		js.runScript();
	}

	public void stopScript() {
		System.out.println("Stop Script Called!");
		js.stopScript();
	}

	public void loadScript(String selectedScript) {
		System.out.println("Load Script Called!: " + selectedScript.toString());
		js.loadDropDownScript("scripts/test1", selectedScript.toString());
	}
}
