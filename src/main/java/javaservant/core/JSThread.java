package javaservant.core;

import bot.SuperRobot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Josh on 7/15/2016.
 */
public class JSThread extends Thread {
    private final static Logger LOG = Logger.getLogger(JSThread.class.getName());
    private static final String JUMP_TO_KEY = "jumpto";
    private static final String VAR_KEY = "var";
    private static final String LABEL_KEY = "label";
    private static final String VARIABLE_DECLARATION_PREFIX = "$(";
    private static final String VARIABLE_DECLARATION_POSTFIX = ")";

    private File script;
    private Map <String, String> variableMap = new HashMap<>();
    private SuperRobot bot;
    private int currentLineNumber;
    boolean running;

    public JSThread(SuperRobot bot, File s, Map<String, String> variableReplacementMap) {
        this.bot = bot;
        script = s;
        initializeVariablesFromUI(variableReplacementMap);
    }

    private void initializeVariablesFromUI(Map<String, String> variableReplacementMap) {
        variableMap.putAll(variableReplacementMap);
    }

    @Override
    public void run() {
        if (running || script == null)
            return;

        running = true;
        JavaServant.setScriptState(JavaServant.SCRIPT_STATE.RUNNING);

        try {
            Map<String, Integer> labelMap = new HashMap<>();
            Map<Integer, Integer> labelLineCtrMap = new HashMap<>(); //used to map current iteration of a loop

            String line;
            currentLineNumber = 0;

            FileReader fr = new FileReader(script);
            BufferedReader br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                if (!running) {
                    LOG.info("Thread " + this + " has been set to stop running");
                    br.close();
                    return;
                }

                if (hasVariableInstance(line)) {
                    line = replaceVariableNamesWithValues(line);
                }

                if (isLabelDeclaration(line)){
                    String [] vals = line.split(":");
                    if (vals.length != 2) { //we should only have a label declaration and label name
                        br.close();
                        throw new IllegalArgumentException("Error on line " + currentLineNumber + ": label name cannot include the symbol ':'. Aborting!");
                    }
                    String labelName = vals[1].trim();
                    if (labelName.contains(",")) {
                        br.close();
                        throw new IllegalArgumentException("Error on line " + currentLineNumber + ": label name cannot include the symbol ','. Aborting!");
                    }
                    labelMap.put(labelName, currentLineNumber);
                } else if (isVariableDeclaration(line)){
                    createVariableMappingIfAbsent(line);
                } else if (isLabelJump(line)) {
                    String [] vals = line.split(":");
                    String[] labelVars = vals[1].trim().split(",");
                    String label = labelVars[0].trim();
                    int labelCtr = -1;
                    if(labelVars.length > 1) {
                        labelCtr = Integer.parseInt(labelVars[1]);
                    }

                    int labelLine = labelMap.get(label);
                    if(labelCtr > -1) { // this is a counted (aka non infinite running loop)
                        if(labelLineCtrMap.containsKey(currentLineNumber)) { // then this is not the first time reaching this jumpto
                            int remainingIterations = labelLineCtrMap.get(currentLineNumber);
                            //System.out.println("Reached back on loop line " + currentLineNumber +  ", with " + remainingIterations + " loop(s) remaining.");
                            if(remainingIterations == 0) { //then we have just completed our last loop
                                //reset ctr by removing mapping in case this is an internal loop
                                labelLineCtrMap.remove(currentLineNumber);
                                //System.out.println("Removed loop mapping for line " + currentLineNumber);

                                currentLineNumber++;
                                continue; //move to next line then get out of counted loop
                            }
                            remainingIterations--;
                            labelLineCtrMap.put(currentLineNumber, remainingIterations);
                        } else {
                            //System.out.println("New mapping! Request to loop on line " + currentLineNumber +  ", " + labelCtr + " times.");
                            labelLineCtrMap.put(currentLineNumber, labelCtr-1);
                        }
                    }

                    br.close();
                    fr = new FileReader(script);
                    br =  new BufferedReader(fr);

                    for(int i = 0; i < labelLine+1;i++) //go 1 line after the label
                    {
                        br.readLine();
                    }

                    //update currentLineNumber
                    currentLineNumber = labelLine + 1;
                    continue; // start reading from new position
                }

                if ("END".equals(line)) {
                    break;
                }

                //execute each line
                bot.execute(line);
                currentLineNumber++;
            }
            br.close();
            JavaServant.setScriptState(JavaServant.SCRIPT_STATE.SUCCEEDED);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Fatal Error, due to: " + e.getMessage());
            e.printStackTrace();
            JavaServant.setScriptState(JavaServant.SCRIPT_STATE.FAILED);
        } finally {
            running = false;
            LOG.info("Thread " + this + " has finished running");
        }
    }

    private void createVariableMappingIfAbsent(String line) {
        int firstColon = line.indexOf(':');
        line = line.substring(firstColon + 1).trim();
        int firstEqual = line.indexOf('=');
        String key = line.substring(0, firstEqual).trim();
        String val = line.substring(firstEqual+1);
        variableMap.putIfAbsent(key, val);
    }

    private String replaceVariableNamesWithValues(String line) throws IOException {
        String val = line;
        int pos = line.indexOf(':');
        if (pos > -1){
            val = line.substring(pos+1); //take everything after first colon
        }
        String [] vars = val.split("\\Q$(\\E");
        for (int i = 0; i < vars.length; i++){
            if (i == 0){ // first split won't ever be needed here
                continue;
            }
            String key = vars[i].substring(0,vars[i].indexOf(')'));
            String value = variableMap.get(key);

            if (value == null) {
                //unknown variable; abort
                throw new IllegalArgumentException("Line " + currentLineNumber + ": \"" +
                        key + "\"" + " is not a recognized variable! Aborting script!");
            }
            line = line.replace(VARIABLE_DECLARATION_PREFIX + key + VARIABLE_DECLARATION_POSTFIX, value); // replace all occurrences off the bat
        }
        return line;
    }

    private boolean hasVariableInstance(String line) {
        return line.contains(VARIABLE_DECLARATION_PREFIX);
    }

    private boolean isLabelDeclaration(String line) {
        return line.toLowerCase().trim().startsWith(LABEL_KEY);
    }

    private boolean isVariableDeclaration(String line) {
        return line.toLowerCase().startsWith(VAR_KEY);
    }

    private boolean isLabelJump(String line) {
        return line.toLowerCase().trim().startsWith(JUMP_TO_KEY);
    }

}