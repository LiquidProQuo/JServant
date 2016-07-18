package javaservant.bridge;

import javaservant.core.JSThread;
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
		if (success) {
			js.getThread().addSubscriber(frame);
			frame.updateRunState(JSThread.RUNNING);
		}
	}

	public void stopScript() {
		System.out.println("Stop Script Called!");
		boolean success = js.stopScript();
		if (success) {
			frame.updateRunState(JSThread.STOPPED);
		}
	}

	public void loadScript(String selectedScript) {
		System.out.println("Load Script Called!: " + selectedScript.toString());
		String result = js.loadDropDownScript(selectedScript.toString(), selectedScript.toString());
		if (!JavaServant.NO_SCRIPT_TEXT.equals(result)) {
			frame.updateRunState(JSThread.READY);
		} else {
			frame.updateRunState(JSThread.NOT_RUNNING);
		}
	}
}
