/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javaservant.core;

import bot.SuperRobot;
import javafx.application.Application;
import javafx.stage.Stage;
import javaservant.view.JServantFrameSimple;
import javaservant.view.JServantWebFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

/**
 *
 * @author Josh
 */
public class JavaServant extends Application {

    private static final boolean USE_WEB_VIEW = true;
	private static final String NONE = "(None)";
	public static final String NO_SCRIPT_TEXT = "No Script Currently Loaded.";
	private static final String SCRIPT_EXTENSION = "jss";
	private static final String SCRIPT_EXTENSION_TITLE = "JServant Scripts";
	public static final String DEFAULT_FILE_DIRECTORY = "scripts";
	private static final String APP_VERSION = "0.6 (Beta)";
	public static final String FRAME_TITLE = "Java Servant " + APP_VERSION;
	private final static Logger LOG = Logger.getLogger(JavaServant.class.getName());

	public enum SCRIPT_STATE {
		NONE,
		READY,
		RUNNING,
		SUCCEEDED,
		FAILED,
		STOPPED,
		ERROR_LOADING
	}

    private SuperRobot bot;
    private File script;
    private JSThread thread;
	private volatile static SCRIPT_STATE scriptState;

	private Map<String, String> variableReplacementMap;
    
    
    public JavaServant() {
        try {
        	Handler handler = new FileHandler("JavaServant.log");
        	LOG.addHandler(handler);
        	LOG.setLevel(Level.ALL);
        	handler.setFormatter(new SimpleFormatter());
        	
            bot = new SuperRobot();
			variableReplacementMap = new HashMap<>();
        } catch(Exception e) {
        	LOG.log(Level.SEVERE, "Fatal Error!: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (USE_WEB_VIEW) {
			initWebView(primaryStage);
		} else {
			initFrame();
		}
    }

	private void initWebView(Stage primaryStage) throws MalformedURLException, URISyntaxException {
		new JServantWebFrame().init(this, primaryStage);
	}

	private void initFrame() {
    	
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JServantFrameSimple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JServantFrameSimple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JServantFrameSimple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JServantFrameSimple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
			JFrame frame = new JServantFrameSimple(JavaServant.this);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle(FRAME_TITLE);
			frame.setResizable(false);
		});
    }
    
    public String[] getDefaultScriptListing() {
    	List<String> scriptList = new ArrayList<>();
    	scriptList.add(NONE);
    	
    	File dir = new File(DEFAULT_FILE_DIRECTORY);
    	File[] contents = dir.listFiles();
    	
    	if (contents != null) {
    		for(File file: contents) {
    			if(file.getName().endsWith("." + SCRIPT_EXTENSION)) {
    				scriptList.add(file.getName());
    			}
    		}
    	}
    	
    	return scriptList.toArray(new String[0]);
    }

    public String loadScript(JFrame frame, String currText) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            SCRIPT_EXTENSION_TITLE, SCRIPT_EXTENSION);
        chooser.setFileFilter(filter);

        //lets get the application's current directory
        File tmp = new File(".");
        chooser.setCurrentDirectory(tmp.getAbsoluteFile());

        String str = currText;

        int approveVal = chooser.showOpenDialog(frame);
        if(approveVal == JFileChooser.APPROVE_OPTION) {
            str = chooser.getSelectedFile().getName();
        }

        if(chooser.getSelectedFile() == null) { //somehow?
            str = currText;
        }
        else {
            script = chooser.getSelectedFile();
        }
        
        return str; 
    }
    
    private void loadScriptFromFileName(String filePath) {
    	File file = new File(filePath);
    	if (!file.exists()) {
    		LOG.warning("File at " +  filePath + " does not exist! Exiting.");
    		return;
    	}
    	
    	script = file;
    	
    }
    
    public String loadDropDownScript(String currText, String fileName) {
    	String  str = currText;
    	if(fileName != null) {
    		if(NONE.equals(fileName)) {
    			script = null;
    			str = NO_SCRIPT_TEXT;
				JavaServant.setScriptState(SCRIPT_STATE.NONE);
    		} else {
				File file = new File(DEFAULT_FILE_DIRECTORY + "/" + fileName);
				if (!file.exists()) {
					LOG.warning("File at " +  DEFAULT_FILE_DIRECTORY + "/" + fileName + " does not exist! Exiting.");
					return NO_SCRIPT_TEXT;
				}
    			script = file;
    			str = script.getName();
				JavaServant.setScriptState(SCRIPT_STATE.READY);
    		}
    	}
    	return str;
    }

	//TODO: scaffolding
	public File loadDropDownScriptGetFile(String currText, String fileName) {
		if (fileName != null) {
			if (NONE.equals(fileName)) {
				script = null;
				JavaServant.setScriptState(SCRIPT_STATE.NONE);
			} else {
				File file = new File(DEFAULT_FILE_DIRECTORY + "/" + fileName);
				if (!file.exists()) {
					LOG.warning("File at " +  DEFAULT_FILE_DIRECTORY + "/" + fileName + " does not exist! Exiting.");
					return null;
				}
				script = file;
				JavaServant.setScriptState(SCRIPT_STATE.READY);
			}
		}
		return script;
	}

    public boolean runScript() {
        if (script != null && (thread == null || !thread.running)) {
        	LOG.info("New Thread request to start running via start button.");
            thread = new JSThread(bot, script, variableReplacementMap);
            thread.start();
			return true;
        }
		return false;
    }

    public boolean stopScript() {
        if (thread != null && thread.isAlive()) {
			thread.interrupt();
			thread.running = false;
            LOG.info("Request " + thread.toString() + "stop running via stop button.");
			thread = null;
			JavaServant.setScriptState(SCRIPT_STATE.STOPPED);
			return true;
        }      
        thread = null;
		return false;
    }
    
    public static void main(String [] args) {
    	if (args.length == 0) {
    		launch(args);
    	} else {
			JavaServant js = new JavaServant();
    		js.loadScriptFromFileName(args[0]);
    		js.runScript();
    	}
    }

	public synchronized static void setScriptState(SCRIPT_STATE newState) {
		scriptState = newState;
	}
	public String getThreadStateDescription() {
		if (scriptState == SCRIPT_STATE.NONE) {
			return "NO SCRIPT LOADED";
		}
		return scriptState.name();
	}

	public Map<String, String> preprocessScript() throws IOException {
		if (script == null) {
			throw new IllegalArgumentException("Script cannot be null!");
		}

		Map<String, String> varMap = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(script));
		String line;
		while((line = br.readLine()) != null) {
			if (line.toLowerCase().startsWith("var")){
				int firstColon = line.indexOf(':');
				line = line.substring(firstColon+1).trim();
				int firstEqual = line.indexOf('=');
				String key = line.substring(0, firstEqual).trim();
				String val = line.substring(firstEqual+1);
				varMap.put(key, val);
			}
		}

		return varMap;
	}

	public void updateVariableMap(String name, String value) {
		variableReplacementMap.put(name, value);
	}
}
