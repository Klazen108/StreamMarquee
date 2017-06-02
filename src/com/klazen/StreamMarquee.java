package com.klazen;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.klazen.JMarqueeLabel.MarqueeEvent;
import com.klazen.properties.PropsManager;

public class StreamMarquee extends JFrame implements MarqueeListener, KeyListener {
	public static final String PROPS_FILE_NAME = "sm.properties";
	
	static Logger LOG = LogManager.getLogger();
	
	private static final long serialVersionUID = 6291676800069861590L;
	
	public static void main(String[] args) {
		new StreamMarquee();
	}
	
	JMarqueeLabel lbl;
	PropsManager props;
	
	List<TextSource> textSources = new LinkedList<TextSource>();
	
	private synchronized void loadProperties() {
		try {
			props = new PropsManager(PROPS_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lbl.setTextFont(new Font(props.fontName.get(),Font.PLAIN,props.fontSize.get()));
		lbl.setBackground(props.bgColor.get());
		lbl.setForeground(props.fgColor.get());
		lbl.setSpeed(props.scrollSpeed.get());
		
		for (TextSource curSource : textSources) {
			curSource.close();
		}
		textSources.clear();
		textSources.add(new FileTextSource(props));
		textSources.add(new HoraroTextSource(props.horaroApiUrl.get()));
	}
	
	private void setFontSize(int fontSize) {
		lbl.setTextFont(new Font(props.fontName.get(),Font.PLAIN,fontSize));
		props.fontSize.set(fontSize);
	}
	
	public StreamMarquee() {
		super("Stream Marquee - by klazen108");
		addKeyListener(this);

		lbl = new JMarqueeLabel();
		lbl.addListener(this);
		add(lbl);
		
		loadProperties();
		
		setLabelText();
		//set size out here because I don't want to reset it on property reload
		setSize(props.width.get(),props.height.get());
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
		    	try {
					props.store();
				} catch (IOException e) {
					LOG.error("Error saving properties",e);
				}
		    }
		});
	}
	
	/**
	 * Loads a line of text and sets the marquee. Takes random/sequential mode into account.
	 */
	public synchronized void setLabelText() {
		String text = textSources.get(0).getMessage();
		LOG.info("Setting new label text to: {}",text);
		lbl.setText(text);
	}

	@Override
	public void marqueeLoop(MarqueeEvent e) {
		setLabelText();
		e.setDelay(props.marqueeDelay.get());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keycode = e.getKeyCode();
		boolean withCtrl = ((e.getModifiers() & KeyEvent.CTRL_MASK)!=0);
		
		switch (keycode) {
		case KeyEvent.VK_SPACE:
			LOG.info("Skip requested");
			lbl.resetXPosition();
			setLabelText();
			break;
		case KeyEvent.VK_R:
			if (!withCtrl) break;
			LOG.info("Reload requested");
			loadProperties();
			break;
		case KeyEvent.VK_UP:
			if (!withCtrl) break;
			props.fontSize.set(props.fontSize.get() + 1);
			LOG.info("font size increased to: {}",props.fontSize.get());
			setFontSize(props.fontSize.get());
			break;
		case KeyEvent.VK_DOWN:
			if (!withCtrl) break;
			props.fontSize.set(props.fontSize.get() - 1);
			LOG.info("font size dncreased to: {}",props.fontSize.get());
			setFontSize(props.fontSize.get());
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}