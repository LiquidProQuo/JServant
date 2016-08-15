package javaservant.core;

import bot.SuperRobot;
import com.jhs.utils.IOUtils;
import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import org.jnativehook.NativeHookException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Very simple high level testing for now. Save output and compared to expected output files
 * Will need to add some mocking for anything more detailed.
 * Created by Josh on 8/14/2016.
 */
public class JSThreadTest {

    private File defaultScript;
    private SuperRobot bot;
    private Map<String, String> varReplacementMap;
    private JSThread jsThread;

    @Before
    public void setUp() throws AWTException {
        defaultScript = new File("scripts/test.jss");
        bot = new SuperRobot();
        varReplacementMap = new HashMap<>();
    }

    @Test
    public void testDefaultRun() throws Exception {
        String testName = new Object(){}.getClass().getEnclosingMethod().getName();
        prepareTest(testName);

        jsThread = initJSThreadDefault();
        jsThread.run();

        cleanUpAndCheckOutputSanity(testName);
    }

    @Test
    public void testDefaultRunWithVariables() throws Exception {
        String testName = new Object(){}.getClass().getEnclosingMethod().getName();
        prepareTest(testName);

        jsThread = initJSThread("scripts/test2.jss");
        jsThread.run();

        cleanUpAndCheckOutputSanity(testName);
    }

    @Test
    public void testRunWithModifiedVariables() throws Exception {
        String testName = new Object(){}.getClass().getEnclosingMethod().getName();
        prepareTest(testName);

        varReplacementMap.put("tricky", "modifiedTrickyVar");
        varReplacementMap.put("alsoTricky", "12345_Modified_$");

        jsThread = initJSThread("scripts/test2.jss");
        jsThread.run();

        cleanUpAndCheckOutputSanity(testName);
    }

    // --------------------- UTILITY METHODS -------------------------

    private JSThread initJSThread(String fileName) {
        File script = new File(fileName);
        return jsThread = new JSThread(bot, script, varReplacementMap);
    }

    private JSThread initJSThreadDefault() {
        return jsThread = new JSThread(bot, defaultScript, varReplacementMap);
    }

    private String getTargetSaveFileName(String testName) {
        return "testResults/" + testName + ".txt";
    }

    private void prepareTest(String testName) {
        String targetSaveFile = getTargetSaveFileName(testName);
        cleanUpOldTestResults(targetSaveFile);
    }

    private void cleanUpOldTestResults(String targetSaveFile) {
        File saveResult = new File(targetSaveFile);
        if (saveResult.exists()) {
            Assert.assertTrue(saveResult.delete());
        }
    }

    private void closeNotepadAndSave(String savePath) throws IOException, NativeHookException {
        bot.execute("save");
        bot.execute("wait:200");
        bot.execute("enter:" + savePath);
        bot.execute("wait:200");
        bot.execute("closeapp");
    }

    private void cleanUpAndCheckOutputSanity(String testName) throws IOException, NativeHookException {
        File saveResult = new File(getTargetSaveFileName(testName));
        closeNotepadAndSave(saveResult.getAbsolutePath());
        assertOutputIsExpected(testName);
    }

    private void assertOutputIsExpected(String testName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File correctResult = new File(classLoader.getResource("testRunComparisons/" + testName + ".txt").getFile());
        Assert.assertTrue(correctResult.exists());

        File testResultsFile = new File(getTargetSaveFileName(testName));
        Assert.assertTrue(testResultsFile.exists());

        List<String> correctLines = IOUtils.readIntoList(correctResult);
        List<String> testLines = IOUtils.readIntoList(testResultsFile);

        Assert.assertEquals(correctLines.size(), testLines.size());
        for (int i = 0; i < correctLines.size(); i++) {
            Assert.assertEquals(correctLines.get(i), testLines.get(i));
        }
    }

}