package Dto;

import java.sql.Date;

public class ProPlanDTO {
	// 기본 조회 테이블
	private String cpID;
	private int cpCount;
	private Date startDate;
	private Date endDate;
	private double cpRate;
	private double successRate;
	private double defectRate;
	private String bigo;
	private String itemCode;
	
	private String itemName;	// 품목명 : join 필요
	
	private String woNum;		// 작업지시서 번호 : join 필요
	private Date woDate;		// 작업지시서 지시일 : join 필요
	private int woPQ;			// 작업지시서 지시 수량 : join 필요
	
	public String getCpID() {
		return cpID;
	}
	public void setCpID(String cpID) {
		this.cpID = cpID;
	}
	public int getCpCount() {
		return cpCount;
	}
	public void setCpCount(int cpCount) {
		this.cpCount = cpCount;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public double getCpRate() {
		return cpRate;
	}
	public void setCpRate(double cpRate) {
		this.cpRate = cpRate;
	}
	public double getSuccessRate() {
		return successRate;
	}
	public void setSuccessRate(double successRate) {
		this.successRate = successRate;
	}
	public double getDefectRate() {
		return defectRate;
	}
	public void setDefectRate(double defectRate) {
		this.defectRate = defectRate;
	}
	public String getBigo() {
		return bigo;
	}
	public void setBigo(String bigo) {
		this.bigo = bigo;
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
	public String getWoNum() {
		return woNum;
	}
	public void setWoNum(String woNum) {
		this.woNum = woNum;
	}
	public Date getWoDate() {
		return woDate;
	}
	public void setWoDate(Date woDate) {
		this.woDate = woDate;
	}
	public int getWoPQ() {
		return woPQ;
	}
	public void setWoPQ(int woPQ) {
		this.woPQ = woPQ;
	}
	
	@Override
	public String toString() {
		return "ProPlanDTO [cpID=" + cpID + ", cpCount=" + cpCount + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", cpRate=" + cpRate + ", successRate=" + successRate + ", defectRate=" + defectRate + ", bigo="
				+ bigo + ", itemCode=" + itemCode + ", itemName=" + itemName + ", woNum=" + woNum + ", woDate=" + woDate
				+ ", woPQ=" + woPQ + "]";
	}
	
}
