package Dto;

import java.sql.Date;
import java.sql.Timestamp;

public class InspectionDTO {
	private String ir_id;			// 검사 이력 코드
	private int ir_type;			// 검사 종류
	private int quality_state; 		// 검사 후 상태
	private int gd_quantity;		// 양품 수량
	private int bd_quantity;		// 불량 수량
	private Timestamp end_time;		// 검사 종료 시간
	private Timestamp start_time;	// 검사 시작 시
	private String worker_id;		// 작업자 ID
	private String cp_id;			// 작업지시서 ID
	private String bd_reason;		// 불량 사유 입력
    private String proc_name;
    private String worker_name;
    private String remark;  		// 비고 칸
    
    
	public String getIr_id() {
		return ir_id;
	}
	public void setIr_id(String ir_id) {
		this.ir_id = ir_id;
	}
	public int getIr_type() {
		return ir_type;
	}
	public void setIr_type(int ir_type) {
		this.ir_type = ir_type;
	}
	public int getQuality_state() {
		return quality_state;
	}
	public void setQuality_state(int quality_state) {
		this.quality_state = quality_state;
	}
	public int getGd_quantity() {
		return gd_quantity;
	}
	public void setGd_quantity(int gd_quantity) {
		this.gd_quantity = gd_quantity;
	}
	public int getBd_quantity() {
		return bd_quantity;
	}
	public void setBd_quantity(int bd_quantity) {
		this.bd_quantity = bd_quantity;
	}
	public Timestamp getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}
	public Timestamp getStart_time() {
		return start_time;
	}
	public void setStart_time(Timestamp start_time) {
		this.start_time = start_time;
	}
	public String getWorker_id() {
		return worker_id;
	}
	public void setWorker_id(String worker_id) {
		this.worker_id = worker_id;
	}
	public String getCp_id() {
		return cp_id;
	}
	public void setCp_id(String cp_id) {
		this.cp_id = cp_id;
	}
	public String getBd_reason() {
		return bd_reason;
	}
	public void setBd_reason(String bd_reason) {
		this.bd_reason = bd_reason;
	}
	public String getProc_name() {
		return proc_name;
	}
	public void setProc_name(String proc_name) {
		this.proc_name = proc_name;
	}
	public String getWorker_name() {
		return worker_name;
	}
	public void setWorker_name(String worker_name) {
		this.worker_name = worker_name;
	}
	public String getRemark() { 
		return remark; 
	}
	public void setRemark(String remark) { 
		this.remark = remark; 
	}
	
	@Override
	public String toString() {
		return "InspectionDTO [ir_id=" + ir_id + ", ir_type=" + ir_type + ", quality_state=" + quality_state
				+ ", gd_quantity=" + gd_quantity + ", bd_quantity=" + bd_quantity + ", end_time=" + end_time
				+ ", start_time=" + start_time + ", worker_id=" + worker_id + ", cp_id=" + cp_id + ", bd_reason="
				+ bd_reason + ", proc_name=" + proc_name + ", worker_name=" + worker_name + ", remark=" + remark + "]";
	}
	
}
