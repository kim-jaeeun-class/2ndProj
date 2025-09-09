package Dto;

import java.sql.Date;

public class StockDTO {
	
	private String stock_id;	// 재고ID
	private Date stock_date;	// 등록일
	private int stock_loc;		// 위치
	private int stock_div;		// 구분
	private int stock_number;	// 수량
	private String item_code;	// 품목코드	
	
	
	public String getStock_id() {
		return stock_id;
	}
	public void setStock_id(String stock_id) {
		this.stock_id = stock_id;
	}
	public Date getStock_date() {
		return stock_date;
	}
	public void setStock_date(Date stock_date) {
		this.stock_date = stock_date;
	}
	public int getStock_loc() {
		return stock_loc;
	}
	public void setStock_loc(int stock_loc) {
		this.stock_loc = stock_loc;
	}
	public int getStock_div() {
		return stock_div;
	}
	public void setStock_div(int stock_div) {
		this.stock_div = stock_div;
	}
	public int getStock_number() {
		return stock_number;
	}
	public void setStock_number(int stock_number) {
		this.stock_number = stock_number;
	}
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	
	@Override
	public String toString() {
		return "StockDTO [stock_id=" + stock_id + ", stock_date=" + stock_date + ", stock_loc=" + stock_loc
				+ ", stock_div=" + stock_div + ", stock_number=" + stock_number + ", item_code=" + item_code + "]";
	}
	
}
