package Dto;

import java.sql.Date;

public class OrderDetDTO {

	private int quantity;
	private String item_code;
	private String item_price;
	private String order_key;
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
	public String getItem_price() {
		return item_price;
	}
	public void setItem_price(String item_price) {
		this.item_price = item_price;
	}
	public String getOrder_key() {
		return order_key;
	}
	public void setOrder_key(String order_key) {
		this.order_key = order_key;
	}
	@Override
	public String toString() {
		return "OrderDetDTO [quantity=" + quantity + ", item_code=" + item_code + ", item_price=" + item_price
				+ ", order_key=" + order_key + "]";
	}
	
	
	
	
	
}
