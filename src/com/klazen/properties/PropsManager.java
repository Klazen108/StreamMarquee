package com.klazen.properties;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.klazen.ReadMode;

public class PropsManager {
	static Logger LOG = LogManager.getLogger();
	
	final String filename;
	Properties props;
	
	public final Prop<Integer > fontSize     = new IntegerProp("fontSize"    ,18); 
	public final Prop<Integer > scrollSpeed  = new IntegerProp("scrollSpeed" ,10); 
	public final Prop<Integer > marqueeDelay = new IntegerProp("marqueeDelay",0); 
	public final Prop<Integer > width        = new IntegerProp("width"       ,400); 
	public final Prop<Integer > height       = new IntegerProp("height"      ,64); 
	public final Prop<Integer > curLine      = new IntegerProp("curLine"     ,0); 
	public final Prop<Color   > bgColor      = new ColorProp  ("bgcolor"     ,Color.BLACK); 
	public final Prop<Color   > fgColor      = new ColorProp  ("fgcolor"     ,Color.WHITE); 
	public final Prop<String  > fontName     = new StringProp ("font"        ,"Arial"); 
	public final Prop<ReadMode> readMode     = new EnumProp<ReadMode>("mode",ReadMode.SEQUENTIAL); 
	public final Prop<String  > horaroApiUrl = new StringProp ("horaroApiUrl","");
	
	public PropsManager(String filename) throws IOException {
		LOG.info("Initializing...");
		this.filename = filename;
		props = new Properties();
		File propsFile = new File(filename);
		if (!propsFile.exists()) {
			LOG.info("Creating new props file");
			propsFile.createNewFile();
		}
		props.load(new FileInputStream(propsFile));
		LOG.info("Initialized.");
	}
	
	public void store() throws FileNotFoundException, IOException {
		props.store(
			new FileOutputStream(filename), 
			"Stream Marquee Properties - check the readme for details"
		);
	}
	
	private class EnumProp<T extends Enum<?>> extends Prop<T> {

		public EnumProp(String key, T defaultValue) {
			super(key, defaultValue);
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get() {
			T retVal = defaultValue;
			try {
				if (props.containsKey(key)) {
					String sReadMode = props.getProperty(key);
					retVal = (T)Enum.valueOf(defaultValue.getClass(),sReadMode);
				} else {
					set(defaultValue);
				}
			} catch (IllegalArgumentException e) {
				set(defaultValue);
			}
			LOG.debug("returning: {}", retVal);
			return defaultValue;
		}

		@Override
		public void set(T value) {
			props.setProperty(key, ""+value);
		}
		
	}
	
	private class IntegerProp extends Prop<Integer> {
		public IntegerProp(String key, Integer defaultValue) {
			super(key,defaultValue);
		}

		@Override
		public Integer get() {
			Integer retVal = defaultValue;
			try {
				if (props.containsKey(key)) {
					retVal = Integer.parseInt(props.getProperty(key));
				} else {
					set(defaultValue);
				}
			} catch (NumberFormatException e) {
				set(defaultValue);
				retVal = defaultValue;
			}
			LOG.debug("returning: {}", retVal);
			return retVal;
		}

		@Override
		public void set(Integer value) {
			props.setProperty(key, ""+value);
		}
	}
	
	private class ColorProp extends Prop<Color> {
		public ColorProp(String key, Color defaultValue) {
			super(key,defaultValue);
		}

		@Override
		public Color get() {
			Color retVal = defaultValue;
			if (!props.containsKey(key)) {
				set(defaultValue);
			}
			try {
				retVal = Color.decode(props.getProperty(key));
			} catch (NumberFormatException e) {
				/* ok */
			}
			LOG.info("returning: {}",retVal);
			return retVal;
		}

		@Override
		public void set(Color value) {
			props.setProperty(key, ""+value);
		}
	}

	private class StringProp extends Prop<String> {
		public StringProp(String key, String defaultValue) {
			super(key,defaultValue);
		}

		@Override
		public String get() {
			String retVal = defaultValue;
			if (props.containsKey(key)) {
				retVal = props.getProperty(key);
			} else {
				set(defaultValue);
			}
			return retVal;
		}

		@Override
		public void set(String value) {
			props.setProperty(key, value);
		}
	}
}
