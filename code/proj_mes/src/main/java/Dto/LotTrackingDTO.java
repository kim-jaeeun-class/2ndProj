package Dto;

public class LotTrackingDTO {
	
	private String lotId;       // LOT 번호
    private String itemCode;    // 품목 코드
    private String itemName;    // 품목명
    private String procName;    // 공정명
    private String procId;      // 공정 ID
    private String startTime;   // 시작 시간
    private String endTime;     // 종료 시간
    private int quantity;       // 수량
    private String qualityState;// 품질 상태
    private String workerId;    // 작업자
    private String irType;      // 검사 유형
    private String defectName;  // 불량 유형
    private int bdQuantity;     // 불량 개수
    private String inspectTime; // 검사 시간
    
	public String getLotId() {
		return lotId;
	}
	public void setLotId(String lotId) {
		this.lotId = lotId;
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
	public String getProcName() {
		return procName;
	}
	public void setProcName(String procName) {
		this.procName = procName;
	}
	public String getProcId() {
		return procId;
	}
	public void setProcId(String procId) {
		this.procId = procId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getQualityState() {
		return qualityState;
	}
	public void setQualityState(String qualityState) {
		this.qualityState = qualityState;
	}
	public String getWorkerId() {
		return workerId;
	}
	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}
	public String getIrType() {
		return irType;
	}
	public void setIrType(String irType) {
		this.irType = irType;
	}
	public String getDefectName() {
		return defectName;
	}
	public void setDefectName(String defectName) {
		this.defectName = defectName;
	}
	public int getBdQuantity() {
		return bdQuantity;
	}
	public void setBdQuantity(int bdQuantity) {
		this.bdQuantity = bdQuantity;
	}
	public String getInspectTime() {
		return inspectTime;
	}
	public void setInspectTime(String inspectTime) {
		this.inspectTime = inspectTime;
	}
	
	@Override
	public String toString() {
		return "LotTrackingDTO [lotId=" + lotId + ", itemCode=" + itemCode + ", itemName=" + itemName + ", procName="
				+ procName + ", procId=" + procId + ", startTime=" + startTime + ", endTime=" + endTime + ", quantity="
				+ quantity + ", qualityState=" + qualityState + ", workerId=" + workerId + ", irType=" + irType
				+ ", defectName=" + defectName + ", bdQuantity=" + bdQuantity + ", inspectTime=" + inspectTime + "]";
	}
}