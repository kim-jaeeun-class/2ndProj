package Dto;

import java.sql.Date;

public class OrderListDTO {

	private String order_key;
	private String order_number;
	private Date order_date;
	private String order_state;
	private String client_id;

	private String worker_id;
	private String worker_name;

	private String dapart_ID2;
	private String depart_level;

	private String totalQty;
	private String totalAmt;
	
	
	public String getOrder_key() {
		return order_key;
	}
	public void setOrder_key(String order_key) {
		this.order_key = order_key;
	}
	public String getOrder_number() {
		return order_number;
	}
	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
	public String getOrder_state() {
		return order_state;
	}
	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getWorker_id() {
		return worker_id;
	}
	public void setWorker_id(String worker_id) {
		this.worker_id = worker_id;
	}
	public String getWorker_name() {
		return worker_name;
	}
	public void setWorker_name(String worker_name) {
		this.worker_name = worker_name;
	}
	public String getDapart_ID2() {
		return dapart_ID2;
	}
	public void setDapart_ID2(String dapart_ID2) {
		this.dapart_ID2 = dapart_ID2;
	}
	public String getDepart_level() {
		return depart_level;
	}
	public void setDepart_level(String depart_level) {
		this.depart_level = depart_level;
	}
	public String getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(String totalQty) {
		this.totalQty = totalQty;
	}
	public String getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}
	
	
	@Override
	public String toString() {
		return "OrderListDTO [order_key=" + order_key + ", order_number=" + order_number + ", order_date=" + order_date
				+ ", order_state=" + order_state + ", client_id=" + client_id + ", worker_id=" + worker_id
				+ ", worker_name=" + worker_name + ", dapart_ID2=" + dapart_ID2 + ", depart_level=" + depart_level
				+ ", totalQty=" + totalQty + ", totalAmt=" + totalAmt + "]";
	} 
	
	
	

}
