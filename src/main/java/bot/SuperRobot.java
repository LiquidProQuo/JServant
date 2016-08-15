package bot;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class SuperRobot extends Robot implements NativeKeyListener {

    private static final int MAX_ALLOWED_WAIT_TIME = 60000;
    private final String COMMENT_SYMBOL = "#";
    
	private HashMap<String,Integer> dict;
	private static volatile boolean enterPressed; //volatile as this is updated in separate thread to escape empty while loop
	
	public SuperRobot() throws AWTException {
		super();	
		fillDict();	
	}
	
	private void fillDict() {
		dict = new HashMap<>();
		
		//returns commands for key presses (letters are all returned lower case)
		dict.put("a", KeyEvent.VK_A);
		dict.put("b", KeyEvent.VK_B);
		dict.put("c", KeyEvent.VK_C);
		dict.put("d", KeyEvent.VK_D);
		dict.put("e", KeyEvent.VK_E);
		dict.put("f", KeyEvent.VK_F);
		dict.put("g", KeyEvent.VK_G);
		dict.put("h", KeyEvent.VK_H);
		dict.put("i", KeyEvent.VK_I);
		dict.put("j", KeyEvent.VK_J);
		dict.put("k", KeyEvent.VK_K);
		dict.put("l", KeyEvent.VK_L);
		dict.put("m", KeyEvent.VK_M);
		dict.put("n", KeyEvent.VK_N);
		dict.put("o", KeyEvent.VK_O);
		dict.put("p", KeyEvent.VK_P);
		dict.put("q", KeyEvent.VK_Q);
		dict.put("r", KeyEvent.VK_R);
		dict.put("s", KeyEvent.VK_S);
		dict.put("t", KeyEvent.VK_T);
		dict.put("u", KeyEvent.VK_U);
		dict.put("v", KeyEvent.VK_V);
		dict.put("w", KeyEvent.VK_W);
		dict.put("x", KeyEvent.VK_X);
		dict.put("y", KeyEvent.VK_Y);
		dict.put("z", KeyEvent.VK_Z);
       
		dict.put("A", KeyEvent.VK_AGAIN);
        dict.put("B", KeyEvent.VK_BEGIN);
        dict.put("C", KeyEvent.VK_CONVERT);
        dict.put("D", KeyEvent.VK_DEAD_VOICED_SOUND);
        dict.put("E", KeyEvent.VK_EURO_SIGN);
        dict.put("F", KeyEvent.VK_F24);
        dict.put("G", KeyEvent.VK_DEAD_OGONEK);
        dict.put("H", KeyEvent.VK_HALF_WIDTH);
        dict.put("I", KeyEvent.VK_INVERTED_EXCLAMATION_MARK);
        dict.put("J", KeyEvent.VK_JAPANESE_KATAKANA);
        dict.put("K", KeyEvent.VK_KATAKANA);
        dict.put("L", KeyEvent.VK_DEAD_ABOVEDOT);
		dict.put("M", KeyEvent.VK_MODECHANGE);
		dict.put("N", KeyEvent.VK_NONCONVERT);
		dict.put("O", KeyEvent.VK_DEAD_ABOVERING);
        dict.put("P", KeyEvent.VK_PREVIOUS_CANDIDATE);
        dict.put("Q", KeyEvent.VK_DEAD_DOUBLEACUTE);
        dict.put("R", KeyEvent.VK_ROMAN_CHARACTERS);
        dict.put("S", KeyEvent.VK_SUBTRACT);
        dict.put("T", KeyEvent.VK_DEAD_DIAERESIS);
        dict.put("U", KeyEvent.VK_DEAD_CEDILLA);
        dict.put("V", KeyEvent.VK_DEAD_BREVE);
        dict.put("W", KeyEvent.VK_PROPS);
        dict.put("X", KeyEvent.VK_ALT_GRAPH);
        dict.put("Y", KeyEvent.VK_JAPANESE_HIRAGANA);
		dict.put("Z", KeyEvent.VK_HIRAGANA);
		
		dict.put("0", KeyEvent.VK_0);
		dict.put("1", KeyEvent.VK_1);
		dict.put("2", KeyEvent.VK_2);
		dict.put("3", KeyEvent.VK_3);
		dict.put("4", KeyEvent.VK_4);
		dict.put("5", KeyEvent.VK_5);
		dict.put("6", KeyEvent.VK_6);
		dict.put("7", KeyEvent.VK_7);
		dict.put("8", KeyEvent.VK_8);
		dict.put("9", KeyEvent.VK_9);
		
		dict.put(".", KeyEvent.VK_DECIMAL);
		dict.put("/", KeyEvent.VK_SLASH);
		dict.put("\\",KeyEvent.VK_BACK_SLASH);
		dict.put("\n", KeyEvent.VK_ENTER);
		dict.put("=", KeyEvent.VK_EQUALS);
        dict.put("-", KeyEvent.VK_MINUS);
		dict.put(":", KeyEvent.VK_COLON);
        dict.put(";", KeyEvent.VK_SEMICOLON);
		dict.put("\"", KeyEvent.VK_QUOTEDBL);
        dict.put("`", KeyEvent.VK_BACK_QUOTE);
        dict.put("\'", KeyEvent.VK_QUOTE);
		dict.put("(", KeyEvent.VK_LEFT_PARENTHESIS);
		dict.put(")", KeyEvent.VK_RIGHT_PARENTHESIS);
        dict.put("[", KeyEvent.VK_OPEN_BRACKET);
        dict.put("]", KeyEvent.VK_CLOSE_BRACKET);
        dict.put(",", KeyEvent.VK_COMMA);
        dict.put("!", KeyEvent.VK_EXCLAMATION_MARK);
        dict.put("@", KeyEvent.VK_AT);
        dict.put("#", KeyEvent.VK_NUMBER_SIGN);
        dict.put("$", KeyEvent.VK_DOLLAR);
        dict.put("%", KeyEvent.VK_UNDO); //Can't find value for "%"
        dict.put("^", KeyEvent.VK_DEAD_CARON); //Can't find value for "^"
        dict.put("&", KeyEvent.VK_AMPERSAND);
        dict.put("*", KeyEvent.VK_ASTERISK);
        dict.put("_", KeyEvent.VK_UNDERSCORE);
        dict.put("+", KeyEvent.VK_PLUS);
        dict.put("{", KeyEvent.VK_BRACELEFT);
        dict.put("}", KeyEvent.VK_BRACERIGHT);
        dict.put("~", KeyEvent.VK_DEAD_TILDE);
        dict.put("?", KeyEvent.VK_DEAD_GRAVE); //Can't find value for "?"
        dict.put("<", KeyEvent.VK_LESS);
        dict.put(">", KeyEvent.VK_GREATER);
        dict.put("|", KeyEvent.VK_SEPARATOR);

		dict.put("down", KeyEvent.VK_DOWN);
		dict.put("up", KeyEvent.VK_UP);
		dict.put("left", KeyEvent.VK_LEFT);
		dict.put("right", KeyEvent.VK_RIGHT);
		dict.put("home", KeyEvent.VK_HOME);
		dict.put("end", KeyEvent.VK_END);
		dict.put("pgup", KeyEvent.VK_PAGE_UP);
		dict.put("pgdn", KeyEvent.VK_PAGE_DOWN);
		dict.put("f1", KeyEvent.VK_F1);
		dict.put("f2", KeyEvent.VK_F2);
		dict.put("f3", KeyEvent.VK_F3);
		dict.put("f4", KeyEvent.VK_F4);
		dict.put("f5", KeyEvent.VK_F5);
		dict.put("f6", KeyEvent.VK_F6);
		dict.put("f7", KeyEvent.VK_F7);
		dict.put("f8", KeyEvent.VK_F8);
		dict.put("f9", KeyEvent.VK_F9);
		dict.put("f10", KeyEvent.VK_F10);
		dict.put("f11", KeyEvent.VK_F11);
		dict.put("f12", KeyEvent.VK_F12);
		dict.put("esc", KeyEvent.VK_ESCAPE);
		dict.put("escape", KeyEvent.VK_ESCAPE);
		dict.put("caps", KeyEvent.VK_CAPS_LOCK);
		dict.put("capslock", KeyEvent.VK_CAPS_LOCK);
		dict.put("pause", KeyEvent.VK_PAUSE);
		dict.put("insert", KeyEvent.VK_INSERT);
		dict.put("delete", KeyEvent.VK_DELETE);
		dict.put("del", KeyEvent.VK_DELETE);
		dict.put(" ", KeyEvent.VK_SPACE);
		dict.put("space", KeyEvent.VK_SPACE);
		dict.put("SPACE", KeyEvent.VK_SPACE);
		dict.put("tab", KeyEvent.VK_TAB);
		dict.put("TAB", KeyEvent.VK_TAB);
		dict.put("shift", KeyEvent.VK_SHIFT);
		dict.put("SHIFT", KeyEvent.VK_SHIFT);
		dict.put("ctrl", KeyEvent.VK_CONTROL);
		dict.put("CTRL", KeyEvent.VK_CONTROL);
		dict.put("control", KeyEvent.VK_CONTROL);
		dict.put("windows", KeyEvent.VK_WINDOWS);
		dict.put("alt", KeyEvent.VK_ALT);
		dict.put("ALT", KeyEvent.VK_ALT);
        dict.put("enter", KeyEvent.VK_ENTER);
		dict.put("Enter", KeyEvent.VK_ENTER);
		dict.put("ENTER", KeyEvent.VK_ENTER);
		
		dict.put("backspace", KeyEvent.VK_BACK_SPACE);
		dict.put("BACKSPACE", KeyEvent.VK_BACK_SPACE);
	}
	
	private void commandTranslate(String cmd) throws NativeHookException {
		if (cmd.equals("copy")) {
			copy();
			return;
		}
		if (cmd.equals("paste")) {
			paste();
			return;
		}
		
		if (cmd.equals("leftclick")) {
			leftClick();
			return;
		}
		
		if (cmd.equals("rightclick")) {
			rightClick();
			return;
		}
		
		if (cmd.equals("undo")) {
			undo();
			return;
		}
		
		if (cmd.equals("redo")) {
			redo();
			return;
		}
        if (cmd.equals("newtab")) {
			newTab();
			return;
		}
        if (cmd.equals("closetab")) {
			closeTab();
			return;
		}
		if (cmd.equals("selectaddressbar")) {
			selectAddressBar();
			return;
		}
        if (cmd.equals("doubleclick")) {
			doubleClick();
			return;
		}
        if (cmd.equals("closeapp")) {
			closeApp();
		} else if ("waitforenter".equals(cmd) || "w4e".equals(cmd)) {
        	waitForEnter();
        } else if ("copyall".equals(cmd)) {
			copyAll();
		} else if ("selectall".equals(cmd)) {
			selectAll();
		} else if ("save".equals(cmd)) {
			save();
		}
	}


	/*
	 * Apparently Robot's key press method is literal and will only be able
     * to press keys that exist on the keyboard, so characters like "!"
     * will cause an invalid argument exception (assuming your keyboard
     doesn't actually have a "!" key).
     *
     * This method is a work around, catching these keys before they're
     * attempted to be pressed, and overriding it with the keyboard commands
     * for the specific key. E.g: "!" = "Shift+1".
     */
	private boolean checkShiftChar(int val) {
		switch (val) {
			case KeyEvent.VK_EXCLAMATION_MARK:
				this.hitSimultaneousKeysPlusDelimited("shift+1");
				return true;
			case KeyEvent.VK_AT:
				this.hitSimultaneousKeysPlusDelimited("shift+2");
				return true;
			case KeyEvent.VK_NUMBER_SIGN:
				this.hitSimultaneousKeysPlusDelimited("shift+3");
				return true;
			case KeyEvent.VK_DOLLAR:
				this.hitSimultaneousKeysPlusDelimited("shift+4");
				return true;
			case KeyEvent.VK_UNDO:
				this.hitSimultaneousKeysPlusDelimited("shift+5");
				return true;
			case KeyEvent.VK_DEAD_CARON:
				this.hitSimultaneousKeysPlusDelimited("shift+6");
				return true;
			case KeyEvent.VK_AMPERSAND:
				this.hitSimultaneousKeysPlusDelimited("shift+7");
				return true;
			case KeyEvent.VK_ASTERISK:
				this.hitSimultaneousKeysPlusDelimited("shift+8");
				return true;
			case KeyEvent.VK_UNDERSCORE:
				this.hitSimultaneousKeysPlusDelimited("shift+-");
				return true;
			case KeyEvent.VK_BRACELEFT:
				this.hitSimultaneousKeysPlusDelimited("shift+[");
				return true;
			case KeyEvent.VK_BRACERIGHT:
				this.hitSimultaneousKeysPlusDelimited("shift+]");
				return true;
			case KeyEvent.VK_DEAD_TILDE:
				this.hitSimultaneousKeysPlusDelimited("shift+`");
				return true;
			case KeyEvent.VK_DEAD_GRAVE:
				this.hitSimultaneousKeysPlusDelimited("shift+/");
				return true;
			case KeyEvent.VK_LESS:
				this.hitSimultaneousKeysPlusDelimited("shift+,");
				return true;
			case KeyEvent.VK_GREATER:
				this.hitSimultaneousKeysPlusDelimited("shift+.");
				return true;
			case KeyEvent.VK_SEPARATOR:
				this.hitSimultaneousKeysPlusDelimited("shift+\\");
				return true;
			case KeyEvent.VK_LEFT_PARENTHESIS:
				this.hitSimultaneousKeysPlusDelimited("shift+9");
				return true;
			case KeyEvent.VK_RIGHT_PARENTHESIS:
				this.hitSimultaneousKeysPlusDelimited("shift+0");
				return true;
			case KeyEvent.VK_COLON:
				this.hitSimultaneousKeysPlusDelimited("shift+;");
				return true;
			case KeyEvent.VK_QUOTEDBL:
				this.hitSimultaneousKeysPlusDelimited("shift+'");
				return true;
			case KeyEvent.VK_AGAIN:
				this.hitSimultaneousKeysPlusDelimited("shift+a");
				return true;
			case KeyEvent.VK_BEGIN:
				this.hitSimultaneousKeysPlusDelimited("shift+b");
				return true;
			case KeyEvent.VK_CONVERT:
				this.hitSimultaneousKeysPlusDelimited("shift+c");
				return true;
			case KeyEvent.VK_DEAD_VOICED_SOUND:
				this.hitSimultaneousKeysPlusDelimited("shift+d");
				return true;
			case KeyEvent.VK_EURO_SIGN:
				this.hitSimultaneousKeysPlusDelimited("shift+e");
				return true;
			case KeyEvent.VK_F24:
				this.hitSimultaneousKeysPlusDelimited("shift+f");
				return true;
			case KeyEvent.VK_DEAD_OGONEK:
				this.hitSimultaneousKeysPlusDelimited("shift+g");
				return true;
			case KeyEvent.VK_HALF_WIDTH:
				this.hitSimultaneousKeysPlusDelimited("shift+h");
				return true;
			case KeyEvent.VK_INVERTED_EXCLAMATION_MARK:
				this.hitSimultaneousKeysPlusDelimited("shift+i");
				return true;
			case KeyEvent.VK_JAPANESE_KATAKANA:
				this.hitSimultaneousKeysPlusDelimited("shift+j");
				return true;
			case KeyEvent.VK_KATAKANA:
				this.hitSimultaneousKeysPlusDelimited("shift+k");
				return true;
			case KeyEvent.VK_DEAD_ABOVEDOT:
				this.hitSimultaneousKeysPlusDelimited("shift+l");
				return true;
			case KeyEvent.VK_MODECHANGE:
				this.hitSimultaneousKeysPlusDelimited("shift+m");
				return true;
			case KeyEvent.VK_NONCONVERT:
				this.hitSimultaneousKeysPlusDelimited("shift+n");
				return true;
			case KeyEvent.VK_DEAD_ABOVERING:
				this.hitSimultaneousKeysPlusDelimited("shift+o");
				return true;
			case KeyEvent.VK_PREVIOUS_CANDIDATE:
				this.hitSimultaneousKeysPlusDelimited("shift+p");
				return true;
			case KeyEvent.VK_DEAD_DOUBLEACUTE:
				this.hitSimultaneousKeysPlusDelimited("shift+q");
				return true;
			case KeyEvent.VK_ROMAN_CHARACTERS:
				this.hitSimultaneousKeysPlusDelimited("shift+r");
				return true;
			case KeyEvent.VK_SUBTRACT:
				this.hitSimultaneousKeysPlusDelimited("shift+s");
				return true;
			case KeyEvent.VK_DEAD_DIAERESIS:
				this.hitSimultaneousKeysPlusDelimited("shift+t");
				return true;
			case KeyEvent.VK_DEAD_CEDILLA:
				this.hitSimultaneousKeysPlusDelimited("shift+u");
				return true;
			case KeyEvent.VK_DEAD_BREVE:
				this.hitSimultaneousKeysPlusDelimited("shift+v");
				return true;
			case KeyEvent.VK_PROPS:
				this.hitSimultaneousKeysPlusDelimited("shift+w");
				return true;
			case KeyEvent.VK_ALT_GRAPH:
				this.hitSimultaneousKeysPlusDelimited("shift+x");
				return true;
			case KeyEvent.VK_JAPANESE_HIRAGANA:
				this.hitSimultaneousKeysPlusDelimited("shift+y");
				return true;
			case KeyEvent.VK_HIRAGANA:
				this.hitSimultaneousKeysPlusDelimited("shift+z");
				return true;
			case KeyEvent.VK_PLUS:
				int[] temp = {KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS};
				this.hitSimultaneousKeys(temp);
				return true;
		}

		return false;
	}
	
	//normal case
	private void typeArray(int[] cmds) {
		for (int cmd : cmds) {
			//first let's check to see if this char is one that
			//requires holding shift
			if (!checkShiftChar(cmd)) {
				this.keyPress(cmd);
				this.keyRelease(cmd);
			}
		}
	}
	
	private void typeLiteralString(String str) {
		int [] toDo = new int[str.length()];
		
		for (int i = 0; i < str.length(); i++) {
			toDo[i] = dict.get(str.charAt(i)+""); // crashes if value is left as pure char
		}
		typeArray(toDo);
	}
	
	private void enterLiteralString(String str) {
		typeLiteralString(str);
    	wait("50");
    	press("enter");
    	wait("100");
	}
	
	private void hitSimultaneousKeys(int[] cmds) {
		for (int cmd1 : cmds) {
			this.keyPress(cmd1);
		}
		delay(100);
		//release afterwards
		for (int cmd : cmds) {
			this.keyRelease(cmd);
		}
	}
	
	private void hitSimultaneousKeys(Object[] cmds) {
		for (Object cmd1 : cmds) {
			this.keyPress((Integer) cmd1);
		}
		delay(100);
		//release afterwards
		for (Object cmd : cmds) {
			this.keyRelease((Integer) cmd);
		}
	}
	
	private void hitSimultaneousKeysPlusDelimited(String cmds) {
		ArrayList<Integer> toDo = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(cmds,"+");
		while (st.hasMoreTokens()) {
			toDo.add(dict.get(st.nextToken()));
		}
		
		hitSimultaneousKeys(toDo.toArray());
	}
	
	private void command(String cmd) throws NativeHookException {
		commandTranslate(cmd.toLowerCase());
	}
	
	private void command(String cmd, int numTimes) throws NativeHookException {
		for (int i = 0; i < numTimes; i++) {
			commandTranslate(cmd.toLowerCase());
		}
	}

    private void hold(String str)
    {
    	this.keyPress(dict.get(str));
    }

    private void release(String str)
    {
    	this.keyRelease(dict.get(str));
    }
	
	private void press(String str) {
		this.keyPress(dict.get(str));
		this.keyRelease(dict.get(str));
	}
	
	private void press(String str, int numTimes) {
		for (int i = 0; i < numTimes; i++) {
			this.keyPress(dict.get(str));
			this.keyRelease(dict.get(str));
		}
	}
	
	private void leftClick() {
		mousePress(InputEvent.BUTTON1_MASK);
		mouseRelease(InputEvent.BUTTON1_MASK);
	}

    private void doubleClick() {
		leftClick();
        this.delay(150);
        leftClick();
	}
	
	private void rightClick() {
		mousePress(InputEvent.BUTTON3_MASK);
		mouseRelease(InputEvent.BUTTON3_MASK);
	}

	private void selectAll() {
		hitSimultaneousKeysPlusDelimited("ctrl+a");
	}
	
	private void copy() {
		hitSimultaneousKeysPlusDelimited("ctrl+c");
	}

	private void copyAll() {
		selectAll();
		copy();
	}

	private void save() {
		hitSimultaneousKeysPlusDelimited("ctrl+s");
	}
	private void paste()
	{
		hitSimultaneousKeysPlusDelimited("ctrl+v");
	}
	
	private void newTab()
	{
		hitSimultaneousKeysPlusDelimited("ctrl+t");
	}
	
	private void closeTab()
	{
		hitSimultaneousKeysPlusDelimited("ctrl+w");
	}

	private void closeApp() {
		hitSimultaneousKeysPlusDelimited("alt+f4");
	}
	
	private void undo()
	{
		hitSimultaneousKeysPlusDelimited("ctrl+z");
	}
	
	private void redo()
	{
		hitSimultaneousKeysPlusDelimited("ctrl+y");
	}
	
	private void selectAddressBar()
	{
		hitSimultaneousKeysPlusDelimited("alt+d");
	}
	
	private void wait(String val) {
		int amount = Integer.parseInt(val);
        while (amount > MAX_ALLOWED_WAIT_TIME) {
            this.delay(MAX_ALLOWED_WAIT_TIME);
            amount -= MAX_ALLOWED_WAIT_TIME;
        }
        this.delay(amount);
	}

	/**
	 * Executes commands using given parameters.
	 * If parameter-less command is received, it is passed to the command() method.
	 *
	 * @param command found at current line of script. Expect form of "command:value"
	 */
	public void execute(String command) throws IOException, NativeHookException {
		if (command == null || "".equals(command.trim()) || command.trim().startsWith(COMMENT_SYMBOL)) {
			return;
		}
		//Split the line btwn the command and its value, split on the ":" symbol
		//command is normalized and the value is taken raw
		String cmd;
		String val;
		if (command.indexOf(':') != -1) {
			cmd = command.substring(0, command.indexOf(':'));
			val = command.substring(command.indexOf(':') + 1);
		} else {
			command(command.trim());
			return;
		}

		cmd = cmd.toLowerCase().trim();

		if ("wait".equals(cmd)) {
			wait(val);
		} else if ("type".equals(cmd)) {
			this.typeLiteralString(val);
		} else if ("press".equals(cmd)) {
			this.hitSimultaneousKeysPlusDelimited(val);
		} else if ("hold".equals(cmd)) {
			this.hold(val);
		} else if ("release".equals(cmd)) {
			this.release(val);
		} else if ("moveto".equals(cmd)) {
			String[] pos = val.split(",");
			int x = Integer.parseInt(pos[0]);
			int y = Integer.parseInt(pos[1]);

			this.mouseMove(x, y);
		} else if ("run".equals(cmd)) {
			runApplication(val);
		} else if ("enter".equals(cmd)) {
			enterLiteralString(val);
		} else if ("cmd".equals(cmd)) {
			this.command(val.trim());
		} else if ("multicmd".equals(cmd)) {
			String[] pos = val.split(",");
			String c = pos[0].trim();
			int i = Integer.parseInt(pos[1].trim());
			this.command(c, i);
		} else if ("multipress".equals(cmd)) {
			String[] pos = val.split(",");
			String c = pos[0].trim();
			int i = Integer.parseInt(pos[1].trim());

			this.press(c, i);
		} else if ("login".equals(cmd)) {
			String[] pos = val.split(",");
			String name = pos[0]; // no trim here since username/pw made include whitespace
			String pw = pos[1];

			this.login(name, pw);
		} else if ("chooseoption".equals(cmd)) {
			val = val.trim();
			int v = Integer.parseInt(val);
			this.chooseOption(v);
		}
	}

        private void login(String name, String pw) {
            this.typeLiteralString(name);
            this.delay(100);
            this.keyPress(KeyEvent.VK_TAB);
            this.keyRelease(KeyEvent.VK_TAB);
            this.delay(100);
            this.typeLiteralString(pw);
            this.delay(100);
            this.keyPress(KeyEvent.VK_ENTER);
            this.keyRelease(KeyEvent.VK_ENTER);
        }

        private void chooseOption(int num) {
            int val = num-1;
            for (int i = 0; i < val; i++) {
                keyPress(KeyEvent.VK_RIGHT);
                this.delay(100);
            }
            keyPress(KeyEvent.VK_ENTER);
        }
        
        /**
         * Block current thread until we detect the enter key has been pressed
         */
		private void waitForEnter() throws NativeHookException {
        	// Initialize native hook.
			GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            enterPressed = false;

			//noinspection StatementWithEmptyBody
			while(!enterPressed) {/*just wait*/}
        }

		@Override
		public void nativeKeyPressed(NativeKeyEvent arg0) {}

		@Override
		public void nativeKeyReleased(NativeKeyEvent e) {
			//check if enter is pressed
			if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
	            try {
	            	enterPressed = true;
					GlobalScreen.unregisterNativeHook();
					System.out.println("Enter Detected! Quitting listen");
				} catch (NativeHookException e1) {
					e1.printStackTrace();
				}
	        }
		}

		@Override
		public void nativeKeyTyped(NativeKeyEvent arg0) {}

	/**
	 * Will execute file at given path, returning true if successful
	 *
	 * @param path of Application to run
	 * @return true if successful
	 */
	private boolean runApplication(String path) throws IOException {
		if (path.endsWith(".lnk")) { //separate process for shortcuts
			new ProcessBuilder("cmd", "/c", path).start();
		} else {
			new ProcessBuilder(path).start();
		}
		return true;
	}
}
