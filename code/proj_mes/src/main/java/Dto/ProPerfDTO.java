package Dto;

import java.sql.Date;

public class ProPerfDTO {
	private String csID;		// 생산실적ID
	private int woNum;			// 작업지시번호
	private Date woDate;		// 작업지시서 작성일
	private int csStat;			// 지시 상태
	private Date csDate;		// 일자(작성일자)
	private String csInType;	// 입고 형태
	private String csOutType;	// 출고 형태
	private String stockID;		// 재고 ID(LOT 대용) : FK
	
	
	public String getCsID() {
		return csID;
	}
	public void setCsID(String csID) {
		this.csID = csID;
	}
	public int getWoNum() {
		return woNum;
	}
	public void setWoNum(int woNum) {
		this.woNum = woNum;
	}
	public int getCsStat() {
		return csStat;
	}
	public void setCsStat(int csStat) {
		this.csStat = csStat;
	}
	public Date getCsDate() {
		return csDate;
	}
	public void setCsDate(Date csDate) {
		this.csDate = csDate;
	}
	public String getCsInType() {
		return csInType;
	}
	public void setCsInType(String csInType) {
		this.csInType = csInType;
	}
	public String getCsOutType() {
		return csOutType;
	}
	public void setCsOutType(String csOutType) {
		this.csOutType = csOutType;
	}
	public String getStockID() {
		return stockID;
	}
	public void setStockID(String stockID) {
		this.stockID = stockID;
	}
	public Date getWoDate() {
		return woDate;
	}
	public void setWoDate(Date woDate) {
		this.woDate = woDate;
	}
	
	@Override
	public String toString() {
		return "ProPerfDTO [csID=" + csID + ", woNum=" + woNum + ", woDate=" + woDate + ", csStat=" + csStat
				+ ", csDate=" + csDate + ", csInType=" + csInType + ", csOutType=" + csOutType + ", stockID=" + stockID
				+ "]";
	}

	
	
	
}
