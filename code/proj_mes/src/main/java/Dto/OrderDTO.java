package Dto;

import java.sql.Date;

public class OrderDTO {

	private String order_key;		// 발주키
	private String order_number;	// 발주번호
	private Date order_date;		// 발주날짜
	private Date order_pay;			// 결제일
	private int order_state;		// 진행 상태

	private String client_id;		// 거래처ID
	private String client_name;		// 거래처명
	private String client_phone;
    private String business_number;
    
	private String dapart_ID2;		// 부서ID
	private String depart_level;	// 부서명

	private String worker_id;		// 사번
	private String worker_name; 	// 담당자 이름
	
	private String totalQty;		// 총 수량
	private String totalAmt; 		// 총 합계
	
	private String bigo;
	
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
	public String getWorker_name() {
		return worker_name;
	}
	public void setWorker_name(String worker_name) {
		this.worker_name = worker_name;
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
	
	public String getClient_name() {
		return client_name;
	}
	public void setClient_name(String client_name) {
		this.client_name = client_name;
	}
	public String getClient_phone() {
		return client_phone;
	}
	public void setClient_phone(String client_phone) {
		this.client_phone = client_phone;
	}
	public String getBusiness_number() {
		return business_number;
	}
	public void setBusiness_number(String business_number) {
		this.business_number = business_number;
	}
	
	public String getBigo() {
		return bigo;
	}
	public void setBigo(String bigo) {
		this.bigo = bigo;
	}
	@Override
	public String toString() {
		return "OrderDTO [order_key=" + order_key + ", order_number=" + order_number + ", order_date=" + order_date
				+ ", order_pay=" + order_pay + ", order_state=" + order_state + ", client_id=" + client_id
				+ ", client_name=" + client_name + ", client_phone=" + client_phone + ", business_number="
				+ business_number + ", dapart_ID2=" + dapart_ID2 + ", depart_level=" + depart_level + ", worker_id="
				+ worker_id + ", worker_name=" + worker_name + ", totalQty=" + totalQty + ", totalAmt=" + totalAmt
				+ ", bigo=" + bigo + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
}
