package Dto;

public class DefectRateDTO {
	private String procId;        // 공정 ID
	private String procName;      // 공정명
	private String inspectionDate;// 검사 일자
    private String itemCode;      // 품목 코드  
    private String itemName;      // 품목명
    private int productionQty;    // 생산 수량
    private int defectQty;        // 불량 수량
    private double defectRate;	  // 불량률
    
	public String getProcId() {
		return procId;
	}
	public void setProcId(String procId) {
		this.procId = procId;
	}
	public String getProcName() {
		return procName;
	}
	public void setProcName(String procName) {
		this.procName = procName;
	}
	public String getInspectionDate() {
		return inspectionDate;
	}
	public void setInspectionDate(String inspectionDate) {
		this.inspectionDate = inspectionDate;
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
	public int getProductionQty() {
		return productionQty;
	}
	public void setProductionQty(int productionQty) {
		this.productionQty = productionQty;
	}
	public int getDefectQty() {
		return defectQty;
	}
	public void setDefectQty(int defectQty) {
		this.defectQty = defectQty;
	}
	public double getDefectRate() {
		return defectRate;
	}
	public void setDefectRate(double defectRate) {
		this.defectRate = defectRate;
	}
	
	@Override
	public String toString() {
		return "DefectRateDTO [procId=" + procId + ", procName=" + procName + ", inspectionDate=" + inspectionDate
				+ ", itemCode=" + itemCode + ", itemName=" + itemName + ", productionQty=" + productionQty
				+ ", defectQty=" + defectQty + ", defectRate=" + defectRate + "]";
	}
}
  