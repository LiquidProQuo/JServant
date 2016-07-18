package javaservant.core;

/**
 * Created by Josh on 7/18/2016.
 */
public interface JSThreadListener {
	void onJsThreadStateChange(String newState, String message);
}
