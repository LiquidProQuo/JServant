package javaservant.bridge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javaservant.core.JavaServant;
import javaservant.view.JServantWebFrame;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		File script = js.loadDropDownScriptGetFile(selectedScript.toString(), selectedScript.toString());
		if (script == null || !script.exists()) {
			frame.resetFrame();
			return;
		} else {
			frame.updateRunState("LOADING...");
		}
		try {
			Map<String, String> varMap = js.preprocessScript();
			List<JsonObject> list = new ArrayList<>();
			for (Map.Entry<String, String> entry : varMap.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue();
				JsonObject obj = new JsonObject();
				obj.addProperty("name", name);
				obj.addProperty("value", value);
				list.add(obj);
			}
			frame.populateVariables(list);
		} catch (Exception e) {
			frame.updateRunState(JavaServant.SCRIPT_STATE.ERROR_LOADING.name());
		}
	}

	public void updateScriptRunState() {
		frame.updateRunState(js.getThreadStateDescription());
	}
}
