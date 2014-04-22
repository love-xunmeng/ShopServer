package com.michael;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;


//import org.hibernate.Query;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//import org.hibernate.classic.Session;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

public class Server implements Runnable {
	
	@Override
	public void run(){
		//Engine engine = new Engine();
		//Thread
	}
	
	//@Override
	public void run2(){
		Socket socket = null;
		try{
			ServerSocket server = new ServerSocket(7777);
			while(true){
				socket = server.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
				String message = in.readLine();
				//System.out.println(message);
				if(message.equals("Catalog")){
					System.out.println("Received catalog request");
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
					//System.out.print(jsonData);
				}else {
					System.out.println("Goods catalog: " + message);
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
							//jsonObj.put("ip", "192.168.21.101");
							//jsonObj.put("image_path", obj[2]);
							String imagePath = (String)obj[3];
							String imageString = convertImageToBase64String(imagePath);
							jsonObj.put("image", imageString);
							jsonObj.put("goods_id", obj[4]);
							jsonArray.put(jsonObj); 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
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
				in.close();
			}
		}catch(IOException e) {
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
	
	public static void main(String[] args) {
	
		Thread thread = new Thread(new Engine());
		thread.start();
		
		//Thread server = new Thread(new Server());
		//server.start();
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
	
	public static BufferedImage convertBase64StringToImage(String image){
		if(null == image)
			return null;
		try{
			byte[] decodedData = Base64.decodeBase64(image.getBytes());
			for(int i = 0; i < decodedData.length; ++i){
				if(decodedData[i] < 0){
					decodedData[i] += 256;
				}
			}
			
			//the following is for test.
			String imageFilePath = "d:\\222.jpg";
			FileOutputStream out = new FileOutputStream(imageFilePath);
			out.write(decodedData);
			out.flush();
			out.close();
			//end of test
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}
