package Dto;

import java.sql.Date;

public class OrderDetDTO {

	private int order_index;
	private int quantity;
	private String item_code;
	private String order_key;
	private Date order_takeday;
	
	
	public int getOrder_index() {
		return order_index;
	}
	public void setOrder_index(int order_index) {
		this.order_index = order_index;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	public String getOrder_key() {
		return order_key;
	}
	public void setOrder_key(String order_key) {
		this.order_key = order_key;
	}
	public Date getOrder_takeday() {
		return order_takeday;
	}
	public void setOrder_takeday(Date order_takeday) {
		this.order_takeday = order_takeday;
	}
	
	@Override
	public String toString() {
		return "OrderDetDTO [order_index=" + order_index + ", quantity=" + quantity + ", item_code=" + item_code
				+ ", order_key=" + order_key + ", order_takeday=" + order_takeday + "]";
	}
	
	
	
}
