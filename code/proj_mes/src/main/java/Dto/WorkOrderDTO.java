package Dto;

import java.sql.Date;

public class WorkOrderDTO {
	// 여기에서 변수 지정 하고 GETTER, SETTER, TOSTRING 설정
	// 다른 식으로 얘기하자면 DB 테이블의 한 행을 담는 느낌?
	
	private int woNum;	// 번호
	private Date woDate;	// 지시일 : '지시일-번호' 형태로 작업지시번호 생성
	private Date woDuedate;	// 마감일
	private int woPQ;	// 목표
	private int woAQ;	// 실제 생산
	private String woPS;	// 진행 상태
	private String workerID; // 사번 : FK
	private String itemCode; // 품목 코드 : FK
	
	// 품목 조회용 : 품목 테이블 영역
	private String itemName; 
	private String itemBigo;
	private int itemType;
	private int itemUnit;
	private String itemPrice;
	
	// 거래처 조회용 : clientname이 전체 조회에 있어서
	private String clientName;
	
	public int getWoNum() {
		return woNum;
	}
	public void setWoNum(int woNum) {
		this.woNum = woNum;
	}
	public Date getWoDate() {
		return woDate;
	}
	public void setWoDate(Date woDate) {
		this.woDate = woDate;
	}
	public Date getWoDuedate() {
		return woDuedate;
	}
	public void setWoDuedate(Date woDuedate) {
		this.woDuedate = woDuedate;
	}
	public int getWoPQ() {
		return woPQ;
	}
	public void setWoPQ(int woPQ) {
		this.woPQ = woPQ;
	}
	public int getWoAQ() {
		return woAQ;
	}
	public void setWoAQ(int woAQ) {
		this.woAQ = woAQ;
	}
	public String getWoPS() {
		return woPS;
	}
	public void setWoPS(String woPS) {
		this.woPS = woPS;
	}
	public String getWorkerID() {
		return workerID;
	}
	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	
	
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemBigo() {
		return itemBigo;
	}
	public void setItemBigo(String itemBigo) {
		this.itemBigo = itemBigo;
	}
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	public int getItemUnit() {
		return itemUnit;
	}
	public void setItemUnit(int itemUnit) {
		this.itemUnit = itemUnit;
	}
	public String getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}
	@Override
	public String toString() {
		return "WorkOrderDTO [woNum=" + woNum + ", woDate=" + woDate + ", woDuedate=" + woDuedate + ", woPQ=" + woPQ
				+ ", woAQ=" + woAQ + ", woPS=" + woPS + ", workerID=" + workerID + ", itemCode=" + itemCode
				+ ", itemName=" + itemName + ", itemBigo=" + itemBigo + ", itemType=" + itemType + ", itemUnit="
				+ itemUnit + ", itemPrice=" + itemPrice + "]";
	}

	
	
	
}
