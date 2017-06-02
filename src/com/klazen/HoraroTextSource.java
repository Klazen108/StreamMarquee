package com.klazen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class HoraroTextSource implements Runnable, TextSource {
	private Thread managerThread;
	public volatile boolean keepRunning = true;
	
	public CursorList<String> messages = new CursorList<String>();
	
	public Date nextUpdateTime = new Date();
	
	String apiUrl;
	
	public HoraroTextSource(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	public void begin() {
		keepRunning = true;
		managerThread = new Thread(this);
		managerThread.start();
	}
	
	public void stop() {
		keepRunning = false;
		managerThread.interrupt();
	}
	
	public void close() {
		stop();
	}
	
	public String getMessage() {
		return messages.get();
	}
	
	public void run() {
		while (keepRunning) {
			try {
				if (new Date().getTime() > nextUpdateTime.getTime()) {
					nextUpdateTime = update();
				}
				Thread.sleep(nextUpdateTime.getTime() - new Date().getTime());
			} catch (InterruptedException ie) {
				//ok; if interrupted to exit, keepRunning will be false and we will exit the loop
			}
		}
	}
	
	public Date update() {
		try {
			URL url = new URL(apiUrl);
			URLConnection connection = url.openConnection();
	        BufferedReader in = new BufferedReader(
	                                new InputStreamReader(
	                                    connection.getInputStream()));

	        StringBuilder rspBuilder = new StringBuilder();
	        String inputLine;

	        while ((inputLine = in.readLine()) != null) 
	            rspBuilder.append(inputLine);

	        in.close();
	        
	        String jsonMessage = rspBuilder.toString();
	        JSONObject response = new JSONObject(jsonMessage);
	        JSONObject ticker = response.getJSONObject("ticker");
	        
	        JSONArray jColumns = response.getJSONObject("schedule").getJSONArray("columns");
	        List<String> columns = decodeJsonArray(jColumns);
	        
	        CursorList<String> newMessages = new CursorList<String>();
	        String message = null;
	        message = getTickerMessage(ticker,"previous",columns);
	        if (message != null) newMessages.add("Previous: "+message);
	        message = getTickerMessage(ticker,"current",columns);
	        if (message != null) newMessages.add("Current: "+message);
	        message = getTickerMessage(ticker,"next",columns);
	        if (message != null) newMessages.add("Next: "+message);
	        messages = newMessages;
	        return new Date();
		} catch (IOException e) {
			System.out.println("Exception loading new message: "+e);
		}
		return new Date();
	}
	
	public String getTickerMessage(JSONObject ticker, String curModule, List<String> columns) {
		String result = null;
        if (ticker.has(curModule)) {
	        JSONObject previous = ticker.getJSONObject(curModule);
	        List<String> values = decodeJsonArray(previous.getJSONArray("data"));
	        result = zipLists(columns,values,": "," / ");
        }
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> decodeJsonArray(JSONArray array) {
        List<T> values = new LinkedList<>();
        for (Object item : array) {
        	values.add((T)item);
        }
        return values;
	}
	
	public String zipLists(List<String> keys, List<String> values, String kvSep, String itemSep) {
		if (keys.size() != values.size()) throw new IllegalArgumentException("key/value lists not the same size");
		StringBuilder rspBuilder = new StringBuilder();
		for (int i = 0; i < keys.size(); i++) {
			rspBuilder.append(keys.get(i)).append(kvSep).append(values.get(i));
			if (i < keys.size()-1) rspBuilder.append(itemSep);
		}
		return rspBuilder.toString();
	}
}
