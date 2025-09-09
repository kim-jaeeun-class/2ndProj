package Dto;

import java.sql.Date;

public class OrderDTO {

	private String order_key;		// 발주키
	private String order_number;	// 발주번호
	private Date order_date;		// 발주날짜
	private Date order_pay;			// 결제일
	private int order_state;		// 진행 상태
	private String client_id;		// 거래처ID
	private String worker_id;		// 사번
	private String dapart_ID2;		// 부서ID
	
	
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
	public Date getOrder_pay() {
		return order_pay;
	}
	public void setOrder_pay(Date order_pay) {
		this.order_pay = order_pay;
	}
	public int getOrder_state() {
		return order_state;
	}
	public void setOrder_state(int order_state) {
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
	public String getDapart_ID2() {
		return dapart_ID2;
	}
	public void setDapart_ID2(String dapart_ID2) {
		this.dapart_ID2 = dapart_ID2;
	}
	
	@Override
	public String toString() {
		return "OrderDTO [order_key=" + order_key + ", order_number=" + order_number + ", order_date=" + order_date
				+ ", order_pay=" + order_pay + ", order_state=" + order_state + ", client_id=" + client_id
				+ ", worker_id=" + worker_id + ", dapart_ID2=" + dapart_ID2 + "]";
	}
	
	
	
	
}
