package com.klazen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * http://stackoverflow.com/questions/2291760/how-to-add-marquee-behaviour-to-jlabel
 * @author Martijn Courteaux
 *
 */
public class JMarqueeLabel extends JPanel implements Runnable
{
    private static final long serialVersionUID = -2973353417536204185L;
    private int x;
    private FontMetrics fontMetrics;
    public static final int MAX_SPEED = 30;
    public static final int MIN_SPEED = 1;
    private int speed;
    public static final int SCROLL_TO_LEFT = 0;
    public static final int SCROLL_TO_RIGHT = 1;
    private int scrollDirection = 0;
    private boolean started = false;
    private JLabel label;
    
    private Set<MarqueeListener> listeners = new HashSet<MarqueeListener>();
    public void addListener(MarqueeListener listener) {
    	listeners.add(listener);
    }
    public void removeListener(MarqueeListener listener) {
    	listeners.remove(listener);
    }
    
    public JMarqueeLabel() {
    	this("");
    }
    
    public JMarqueeLabel(String text)
    {
        super();
        label = new JLabel(text)
        {
            private static final long serialVersionUID = -870580607070467359L;

            @Override
            protected void paintComponent(Graphics g)
            {
                g.translate(x, 0);
                super.paintComponent(g);
            }
        };
        setLayout(null);
        add(label);
        setSpeed(10);
        setScrollDirection(SCROLL_TO_LEFT);
    }

    @Override public void setForeground(Color c) {
		if (label != null) label.setForeground(c);
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        label.paintComponents(g);
    }

    public void setScrollDirection(int scrollDirection)
    {
        this.scrollDirection = scrollDirection;
    }

    public int getScrollDirection()
    {
        return scrollDirection;
    }

    public void setSpeed(int speed)
    {
        if (speed < MIN_SPEED || speed > MAX_SPEED)
        {
            throw new IllegalArgumentException("speed out of range");
        }
        this.speed = speed;
    }

    public int getSpeed()
    {
        return speed;
    }

    @Override
    public void validateTree()
    {
        //System.out.println("Validate...");
        super.validateTree();
        label.setBounds(0, 0, 2000, getHeight());
        if (!started)
        {
            resetXPosition();
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
            started = true;
        }
    }

    public String getText()
    {
        return label.getText();
    }

    public void setText(String text)
    {
        label.setText(text);
    }

    public void setTextFont(Font font)
    {
        label.setFont(font);
        fontMetrics = label.getFontMetrics(label.getFont());
    }
    
    private int onMarqueeLoop() {
    	MarqueeEvent e = new MarqueeEvent();
    	for (MarqueeListener curListener : listeners) {
    		curListener.marqueeLoop(e);
    	}
    	return e.getDelay();
    }
    
    public synchronized void resetXPosition() {
    	x = getWidth() + 10;
    }

    @Override
    public void run()
    {
        fontMetrics = label.getFontMetrics(label.getFont());
        try
        {
            Thread.sleep(100);
        } catch (Exception e)
        {
        }
        while (true)
        {
        	int delay = 0;
            if (scrollDirection == SCROLL_TO_LEFT)
            {
                x--;
                if (x < -fontMetrics.stringWidth(label.getText()) - 10)
                {
                    x = getWidth() + 10;
                    delay = onMarqueeLoop();
                }
            }
            if (scrollDirection == SCROLL_TO_RIGHT)
            {
                x++;
                if (x > getWidth() + 10)
                {
                    x = -fontMetrics.stringWidth(label.getText()) - 10;
                    delay = onMarqueeLoop();
                }
            }
            repaint();
            try
            {
                Thread.sleep(35 - speed + delay);
            } catch (Exception e)
            {
            }
        }
    }
    public class MarqueeEvent {
    	private int delay = 0;
    	/**
    	 * Define a delay, in milliseconds, in which the marquee should pause before
    	 * continuing the next cycle. Defaults to zero.
    	 * @param delay
    	 */
    	public void setDelay(int delay) {
    		this.delay = delay;
    	}
    	
    	/**
    	 * Get the delay, in milliseconds, in which the marquee should pause before
    	 * continuing the next cycle. Defaults to zero.
    	 * @param delay
    	 */
    	public int getDelay() {
    		return delay;
    	}
    }
}