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
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.json.JSONObject;

public class RegisterRunnable implements Runnable {
	
	private Socket socket = null;
	
	public RegisterRunnable(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {
		try{
			System.out.println("RegisterRunnable");	
			Boolean isRegisterSuccess = true;
			//Receive user info.
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			String data = br.readLine();
			JSONObject jsonObject = new JSONObject(data);
			String userName = jsonObject.getString("user_name");
			String mobilePhone = jsonObject.getString("mobile_phone");
			String address = jsonObject.getString("address");
			String email = jsonObject.getString("email");
			//
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			Session session = sessionFactory.openSession();
			//Check if the mobile_phone number exists.
			String sqlQuery = "select * from tbcustomer where mobile_phone='" + mobilePhone + "'";
			//for debug
			System.out.println("Query sql: " + sqlQuery);
			//end for debug
			Query query = session.createQuery(sqlQuery);
			List list = query.list();
			if(list.size() > 0){
				//The mobileL_phone have been registered.
				isRegisterSuccess = false;
			}else{
				Transaction transaction = session.beginTransaction();
				transaction.begin();
				TbCustomer oneCustomer = new TbCustomer();
				oneCustomer.setName(userName);
				oneCustomer.setMobile_phone(mobilePhone);
				oneCustomer.setAddress(address);
				oneCustomer.setEmail(email);
				session.save(oneCustomer);
				transaction.commit();
				isRegisterSuccess = true;
			}			
			//send result back
			jsonObject = new JSONObject();
			jsonObject.put("register_result", isRegisterSuccess);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);
			pw.println(jsonObject.toString());
			
			br.close();
			pw.close();
			
			session.close();
			sessionFactory.close();
			
		}
		catch(Exception e){
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
