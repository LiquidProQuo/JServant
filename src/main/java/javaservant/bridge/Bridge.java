package javaservant.bridge;

import javaservant.core.JavaServant;
import javaservant.view.JServantWebFrame;

/**
 * Created by Josh on 7/17/2016.
 */
public class Bridge {
	private final JavaServant js;
	private final JServantWebFrame frame;

	public Bridge(JavaServant js, JServantWebFrame frame) {
		this.js = js;
		this.frame = frame;
	}

	public void runScript() {
		System.out.println("Run Script Called!");
		boolean success = js.runScript();
	}

	public void stopScript() {
		System.out.println("Stop Script Called!");
		js.stopScript();
	}

	public void loadScript(String selectedScript) {
		System.out.println("Load Script Called!: " + selectedScript.toString());
		js.loadDropDownScript(selectedScript.toString(), selectedScript.toString());
	}

	public void updateScriptRunState() {
		frame.updateRunState(js.getThreadStateDescription());
	}
}
