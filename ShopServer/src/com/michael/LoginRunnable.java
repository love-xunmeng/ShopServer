package com.michael;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.json.JSONObject;

public class LoginRunnable implements Runnable {
	
	private Socket socket = null;
	private Boolean isLoginValid = false;
	
	public LoginRunnable(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			String data = br.readLine();
			JSONObject jsonObject = new JSONObject(data);
			String mobilePhone = jsonObject.getString("mobile_phone");
			String sqlQuery = "select id, name, mobile_phone, address, email from TbCustomer where mobile_phone='" + mobilePhone + "'";
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			Session session = sessionFactory.openSession();
			//for debug
			System.out.println(sqlQuery);
			//end of debug
			Query query = session.createQuery(sqlQuery);
			List list = query.list();
			//for debug
			System.out.println("LoginRunnable::list.size()" + list.size());
			//end of for debug
			if(list.size() != 1){
				this.isLoginValid = false;
			}else{
				PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);
				
				jsonObject = new JSONObject();
				jsonObject.put("is_login_valid", true);
				pw.println(jsonObject.toString());
				
				Object[] obj = (Object[])list.get(0);
				jsonObject = new JSONObject();
				jsonObject.put("id", obj[0]);
				jsonObject.put("user_name", obj[1]);
				jsonObject.put("mobile_phone", obj[2]);
				jsonObject.put("address", obj[3]);
				jsonObject.put("email", obj[4]);
				pw.println(jsonObject.toString());
				
				this.isLoginValid = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(null != socket){
					socket.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
