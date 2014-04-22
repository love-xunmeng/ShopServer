package com.michael;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Engine implements Runnable {
	
	public enum MessageType{
		Catalog, Goods, Order
	}
	
	public Boolean start()
	{
		return true;
	}
	
	public void stop()
	{
		
	}

	@Override
	public void run() {
		Socket socket = null;
		try{
			ServerSocket server = new ServerSocket(7777);
			while(true){
				socket = server.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
				String message = in.readLine();
				if(message.equals("Catalog")){
					Thread thread = new Thread(new CatalogRunnable(socket));
					thread.start();
				}
				if(message.equals("Goods")){
					Thread thread = new Thread(new GoodsRunnable(socket));
					thread.start();
				}
				if(message.equals("Order")){
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
