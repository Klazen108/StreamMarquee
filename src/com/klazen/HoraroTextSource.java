package com.klazen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class HoraroTextSource implements Runnable, TextSource {
	static Logger LOG = LogManager.getLogger();
	
	private Thread managerThread;
	public volatile boolean keepRunning = true;
	
	public CursorList<String> messages = new CursorList<String>();
	
	public Date nextUpdateTime = new Date();
	
	String apiUrl;
	
	public HoraroTextSource(String apiUrl) {
		this.apiUrl = apiUrl;
		begin();
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
		String message = messages.get();
		if (message == null) message = "Ticker Updating...";
		return message;
	}
	
	public void run() {
		LOG.debug("Horaro Runnable Event Loop: starting");
		while (keepRunning) {
			try {
				if (new Date().getTime() >= nextUpdateTime.getTime()) {
					nextUpdateTime = update();
				}
				long sleepTime = Math.max(60*1000, nextUpdateTime.getTime() - new Date().getTime());
				Thread.sleep(sleepTime);
			} catch (InterruptedException ie) {
				//ok; if interrupted to exit, keepRunning will be false and we will exit the loop
			}
		}
		LOG.debug("Horaro Runnable Event Loop: end");
	}
	
	public Date update() {
		try {
			LOG.info("Updating Ticker");
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
	        LOG.debug("Horaro response: {}",jsonMessage);
	        JSONObject response = new JSONObject(jsonMessage);
	        JSONObject ticker = response.getJSONObject("data").getJSONObject("ticker");
	        
	        JSONArray jColumns = response.getJSONObject("data").getJSONObject("schedule").getJSONArray("columns");
	        List<String> columns = decodeJsonArray(jColumns);
	        
	        CursorList<String> newMessages = new CursorList<String>();
	        String message = null;
	        message = getTickerMessage(ticker,"previous",columns);
	        if (message != null) newMessages.add("Previous "+message);
	        message = getTickerMessage(ticker,"current",columns);
	        if (message != null) newMessages.add("Current "+message);
	        message = getTickerMessage(ticker,"next",columns);
	        if (message != null) newMessages.add("Next "+message);
	        messages = newMessages;
	        return addMinutes(new Date(), 10);
		} catch (IOException e) {
			LOG.error("Exception while loading horaro data, delaying for 5 minutes",e);
			return addMinutes(new Date(), 5);
		}
	}
	
	public Date addMinutes(Date start, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
	
	public String getTickerMessage(JSONObject ticker, String curModule, List<String> columns) {
		String result = null;
        if (ticker.has(curModule) && !ticker.get(curModule).equals(JSONObject.NULL)) {
	        JSONObject previous = ticker.getJSONObject(curModule);
	        List<String> values = decodeJsonArray(previous.getJSONArray("data"));
	        values = values.stream()
        		.map(s -> s
    				.replaceAll("\\(.*?\\)", "")
    				.replaceAll("[\\[\\]]", "")
    				.trim())
        		.collect(Collectors.toList());
	        List<String> columns2 = columns.stream()
        		.map(s -> s
	        		.replaceAll("Category", "")
	        		.replaceAll("Runner\\(s\\)", "by")
	        		.trim())
        		.collect(Collectors.toList());
	        result = zipLists(columns2,values,": "," ");
	        LOG.info("new ticker message: {} {}",curModule,result);
        } else {
	        LOG.debug("no ticker message: {}",curModule);
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
			if (keys.get(i).length()>0) rspBuilder.append(keys.get(i)).append(kvSep);
			rspBuilder.append(values.get(i));
			if (i < keys.size()-1) rspBuilder.append(itemSep);
		}
		return rspBuilder.toString();
	}
}
