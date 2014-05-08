package com.michael;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Engine implements Runnable {
	
	private ServerSocket server = null;
	
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
			server = new ServerSocket(7777);
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
					System.out.println("Received Order");
					Thread thread = new Thread(new OrderRunnable(socket));
					thread.start();
				}
				if(message.equals("Register")){
					Thread thread = new Thread(new RegisterRunnable(socket));
					thread.start();
				}
				if(message.equals("Login")){
					Thread thread = new Thread(new LoginRunnable(socket));
					thread.start();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				server.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
}
