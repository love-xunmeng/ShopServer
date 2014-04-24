package com.michael;

import java.util.Date;

public class TbOrder implements java.io.Serializable {
	
	private Integer id;
	private Integer goods_id;
	private Integer customer_id;
	private Integer quantity;
	private Date book_time;
	private Date close_time;
	private int state;

	public Integer getId(){
		return this.id;
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getGoodsId(){
		return this.goods_id;
	}
	
	public void setGoodsId(Integer goods_id){
		this.goods_id = goods_id;
	}
	
	public Integer getCustomerId(){
		return this.customer_id;
	}
	
	public void setCustomerId(Integer customer_id){
		this.customer_id = customer_id;
	}
	
	public Integer getQuantity(){
		return this.quantity;
	}
	
	public void setQuantity(Integer quantity){
		this.quantity = quantity;
	}
	
	public Date getBookTime(){
		return this.book_time;
	}
	
	public void setBookTime(Date book_time){
		this.book_time = book_time;
	}
	
	public Date getCloseTime(){
		return this.close_time;
	}
	
	public void setCloseTime(Date close_time){
		this.close_time = close_time;
	}
	
	public Integer getState(){
		return this.state;
	}
	
	public void setState(Integer state){
		this.state = state;
	}
}
