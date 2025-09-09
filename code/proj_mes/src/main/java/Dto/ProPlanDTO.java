package Dto;

import java.sql.Date;

public class ProPlanDTO {
	private String cpID; 		// 생산계획ID
	private int cpCount; 		// 계획 수량
	private int cpStat;	 		// 진행 상태
	private Date startDate; 	// 계획 시작일
	private Date endDate;		// 계획 종료일
	private int cpRate;	 		// 진행률
	private int successRate;	// 달성률
	private int defectRate;		// 불량률
	private String bigo;		// 비고
	private String itemCode;	// 품목 코드 : FK
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
	public int getCpStat() {
		return cpStat;
	}
	public void setCpStat(int cpStat) {
		this.cpStat = cpStat;
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
	public int getCpRate() {
		return cpRate;
	}
	public void setCpRate(int cpRate) {
		this.cpRate = cpRate;
	}
	public int getSuccessRate() {
		return successRate;
	}
	public void setSuccessRate(int successRate) {
		this.successRate = successRate;
	}
	public int getDefectRate() {
		return defectRate;
	}
	public void setDefectRate(int defectRate) {
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
	@Override
	public String toString() {
		return "ProPlanDTO [cpID=" + cpID + ", cpCount=" + cpCount + ", cpStat=" + cpStat + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", cpRate=" + cpRate + ", successRate=" + successRate + ", defectRate="
				+ defectRate + ", bigo=" + bigo + ", itemCode=" + itemCode + "]";
	}
	
	
}
