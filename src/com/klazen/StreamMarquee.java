package com.klazen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import com.klazen.JMarqueeLabel.MarqueeEvent;

public class StreamMarquee extends JFrame implements MarqueeListener, KeyListener {
	private static final long serialVersionUID = 6291676800069861590L;
	private ReadMode readMode = ReadMode.SEQUENTIAL;
	
	private enum ReadMode { 
		RANDOM, SEQUENTIAL; 
	}

	public static void main(String[] args) {
		new StreamMarquee();
	}
	
	JMarqueeLabel lbl;
	Properties props;
	int curLine = -1; //start at -1 because we'll increment before first set
	int fontSize = 18;
	int marqueeDelayMs = 0;
	
	public static final String PROPS_FILE_NAME = "sm.properties";
	public static final String TEXT_FILE_NAME = "text.txt";
	
	private void loadProperties() {
		props = new Properties();
		File propsFile = new File(PROPS_FILE_NAME);
		if (propsFile.exists()) {
			try {
				props.load(new FileInputStream(propsFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String sReadMode = tryGetProperty("mode","SEQUENTIAL");
		try {
			readMode = ReadMode.valueOf(sReadMode);
		} catch (IllegalArgumentException e) {
			//ok
			System.out.println("Don't understand the read mode: "+sReadMode);
		}
		lbl.setTextFont(new Font(tryGetProperty("font", "Arial"),Font.PLAIN,tryParseInt(tryGetProperty("fontSize", "18"),18)));
		lbl.setBackground(Color.decode(tryGetProperty("bgcolor", "000000")));
		lbl.setForeground(Color.decode(tryGetProperty("fgcolor", "16777215")));
		lbl.setSpeed(tryParseInt(tryGetProperty("scrollSpeed", "10"),10));
		marqueeDelayMs = tryParseInt(tryGetProperty("marqueeDelay", "0"),0);
		fontSize = tryParseInt(tryGetProperty("fontSize", "18"),18);
	}
	
	private void setFontSize(int fontSize) {
		lbl.setTextFont(new Font(tryGetProperty("font", "Arial"),Font.PLAIN,fontSize));
		props.setProperty("fontSize", ""+fontSize);
	}
	
	public StreamMarquee() {
		super("Stream Marquee - by klazen108");
		addKeyListener(this);

		lbl = new JMarqueeLabel();
		lbl.addListener(this);
		setLabelText();
		add(lbl);
		
		loadProperties();
		//set size out here because I don't want to reset it on property reload
		setSize(tryParseInt(tryGetProperty("width", "400"),400),tryParseInt(tryGetProperty("height","64"),64));
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
		    	try {
					props.store(new FileOutputStream(PROPS_FILE_NAME), "Stream Marquee props by Klazen108");
				} catch (IOException e) {
					System.err.println("Error saving properties - "+e.getMessage());
				}
		    }
		});
	}
	
	/**
	 * Attempts to get a property from the properties file, returning a default value if the
	 * key for the property does not exist
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private String tryGetProperty(String key, String defaultValue) {
		if (props.containsKey(key)) return props.getProperty(key);
		else {
			props.setProperty(key, defaultValue);
			return defaultValue;
		}
	}
	
	/**
	 * Attempts to parse an integer from a string, returning a default value if the parse fails
	 * @param string
	 * @param defaultValue
	 * @return
	 */
	private int tryParseInt(String string, int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * returns a random value up to, but not including, the maximum specified
	 * @param max
	 * @return
	 */
	private static int irandom(int max) {
		return (int)(Math.floor(max*Math.random()));
	}
	
	/**
	 * Loads a line of text and sets the marquee. Takes random/sequential mode into account.
	 */
	public synchronized void setLabelText() {
	    String text = "";
		try {
			System.out.println("Setting new label text");
			int lineCount = TextLoader.countLines(TEXT_FILE_NAME);
		    System.out.println("detected line count: "+lineCount);
		    if (ReadMode.RANDOM.equals(readMode)) {
		    	curLine = irandom(lineCount);
		    } else {
		    	curLine++;
		    	if (curLine >= lineCount) curLine = 0;
		    }
			
		    System.out.println("chosen index: "+curLine);
		    text = TextLoader.loadLine(TEXT_FILE_NAME, curLine);
		} catch (IOException e) {
			text = "Unable to load file: text.txt";
			e.printStackTrace();
		} catch (Exception e) {
			text = "Unexpected error";
			e.printStackTrace();
		}
		lbl.setText(text);
	}

	@Override
	public void marqueeLoop(MarqueeEvent e) {
		setLabelText();
		e.setDelay(marqueeDelayMs);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keycode = e.getKeyCode();
		boolean withCtrl = ((e.getModifiers() & KeyEvent.CTRL_MASK)!=0);
		
		switch (keycode) {
		case KeyEvent.VK_SPACE:
			System.out.println("Skip requested");
			lbl.resetXPosition();
			setLabelText();
			break;
		case KeyEvent.VK_R:
			if (!withCtrl) break;
			System.out.println("Reload requested");
			loadProperties();
			break;
		case KeyEvent.VK_UP:
			if (!withCtrl) break;
			fontSize++;
			System.out.println("font size increased to: "+fontSize);
			setFontSize(fontSize);
			break;
		case KeyEvent.VK_DOWN:
			if (!withCtrl) break;
			fontSize--;
			System.out.println("font size increased to: "+fontSize);
			setFontSize(fontSize);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}