package com.michael;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;

public class CatalogRunnable implements Runnable {
	
	private Socket socket = null;
	
	public CatalogRunnable(Socket ss){
		socket = ss;
	}
	
	@Override
	public void run()
	{
		try
		{
			System.out.println("CatalogRunnable");
			JSONArray data = new JSONArray();
			Configuration conf = new Configuration().configure();
			SessionFactory sf = conf.buildSessionFactory();
			Session session = sf.openSession();
			Query query = session.createQuery("from TbCatalog");
			List list = query.list();
			for(int i = 0; i < list.size(); ++i){
				TbCatalog oneCatalog = (TbCatalog)list.get(i);
				data.put(oneCatalog.getName());
			}
			session.close();
			sf.close();
			String jsonData = data.toString();
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);
			out.print(jsonData);
			out.close();
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
