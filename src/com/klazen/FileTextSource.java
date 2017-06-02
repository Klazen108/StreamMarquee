package com.klazen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import com.klazen.properties.PropsManager;

public class FileTextSource implements TextSource {
	public static final String TEXT_FILE_NAME = "text.txt";
	
	PropsManager props;
	
	public FileTextSource(PropsManager props) {
		this.props = props;
	}
	
	public String getMessage() {
		try {
			int lineCount = countLines(TEXT_FILE_NAME);
		    System.out.println("detected line count: "+lineCount);
		    if (ReadMode.RANDOM.equals(props.readMode.get())) {
		    	props.curLine.set(irandom(lineCount));
		    } else {
		    	int curLine = props.curLine.get();
		    	curLine++;
		    	if (curLine >= lineCount) curLine = 0;
		    	props.curLine.set(curLine);
		    }
			
		    return loadLine(TEXT_FILE_NAME, props.curLine.get());
		} catch (IOException e) {
			return null;
		}
	}
	
	public void close() {
		
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
	 * Counts lines in a text file
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static int countLines(String filename) throws IOException {
	    try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
	        byte[] c = new byte[1024];
	        int count = 1;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    }
	}
	
	/**
	 * Loads a line of text from file. If the line does not exist, then the last line of
	 * the file is loaded instead.
	 * @param filename
	 * @param curLine
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String loadLine(String filename, int curLine) throws FileNotFoundException, IOException {
		String lastText = "";
		String text = "";
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    int i = 0;
		    do {
		    	lastText = text;
		    	text = br.readLine(); //read the current line
		    	i++;
		    } while (i <= curLine && text != null);
		}
		return text==null?lastText:text;
	}
}
