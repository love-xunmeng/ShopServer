package com.michael;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

public class OrderRunnable implements Runnable {

	private Socket socket = null;
	
	public OrderRunnable(Socket ss){
		socket = ss;
	}
	
	@Override
	public void run(){
		try
		{
			System.out.println("OrderRunnalbe");
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8")); 
			String data = in.readLine();
			System.out.print(data);
			JSONArray jsonArray = new JSONArray(data);
			for(int i = 0; i < jsonArray.length(); ++i){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				int customer_id = 1;
				int goods_id = jsonObject.getInt("goods_id");
				int quantity = jsonObject.getInt("quantity");
				Configuration conf = new Configuration().configure();
				SessionFactory sf = conf.buildSessionFactory();
				Session session = sf.openSession();
				Transaction tx = session.beginTransaction();
				TbOrder oneOrder = new TbOrder();
				oneOrder.setCustomerId(customer_id);
				oneOrder.setGoodsId(goods_id);
				oneOrder.setQuantity(quantity);
				oneOrder.setBookTime(new Date());
				session.save(oneOrder);
				tx.commit();
				session.close();
				sf.close();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			if(null != socket){
				try{
					socket.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
