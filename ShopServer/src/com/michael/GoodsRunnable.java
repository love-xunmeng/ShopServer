package com.michael;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoodsRunnable implements Runnable {

	private Socket socket = null;
	
	public GoodsRunnable(Socket ss){
		socket = ss;
	}
	
	@Override
	public void run(){
		try{
			System.out.println("GoodsRunnable");
			Configuration conf = new Configuration().configure();
			SessionFactory sf = conf.buildSessionFactory();
			Session session = sf.openSession();
			String sqlQuery = "select g.name, g.description, g.price, g.picturePath, g.id from TbCatalog c, TbGoods g where c.id=g.catalogId and c.name='Ï´·¢Ë®'";
			Query query = session.createQuery(sqlQuery);
			List list = query.list();
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObj = null;
			for(int i = 0; i < list.size(); ++i){
				Object[] obj = (Object[])list.get(i);
				try {
					jsonObj = new JSONObject();
					jsonObj.put("name", obj[0]);
					jsonObj.put("description", obj[1]);
					jsonObj.put("price", obj[2]);
					String imagePath = (String)obj[3];
					String imageString = convertImageToBase64String(imagePath);
					jsonObj.put("image", imageString);
					jsonObj.put("goods_id", obj[4]);
					jsonArray.put(jsonObj); 
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			System.out.println("JSON data to client: " + jsonArray.toString());
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);
			out.print(jsonArray.toString());
			out.close();
			session.close();
			sf.close();
		}
		catch(Exception e){
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
		
	public static String convertImageToBase64String(String imagePath){
		InputStream in = null;
		byte[] data = null;
		try{
			in = new FileInputStream(imagePath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		byte[] encodedData = Base64.encodeBase64(data, true);
		return new String(encodedData);
	}	

}
