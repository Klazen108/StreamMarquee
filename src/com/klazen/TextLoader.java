package com.klazen;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class TextLoader {
	
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
