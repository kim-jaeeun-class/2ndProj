package Dto;

import java.sql.Date;

public class WorkOrderDTO {
	// 기본 조회 테이블
	private String woNum;		// 작업지시번호
	private Date woDate;		// 지시일
	private Date woDuedate;		// 마감일
	private int woPQ;			// 지시 수량
	private int woAQ;			// 생산 수량
	private String workerID;	// 담당자
	private String item_code;	// 품목코드 : 이걸 기준으로 join해서 품목명까지 가져오기
	
	private String worker_name;	// 사원명 : 뭐... 실제 담당자명 가져와야 하니까?
	
	private String item_name;	// 품목명 : 품목 추가 영역에서 필요
	
	private String bom_id;		// 품목에 맞는 bom 확인 및 조회
	private int bom_reqAm;		// bom 소요량
	private String cp_id;		// 품목에 맞는 생산 계획 확인 및 조회
	private String proc_id;		// 공정 불러오기용
	private String proc_name;	// 공정 이름
	
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
	public String getWorkerID() {
		return workerID;
	}
	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	public String getWorker_name() {
		return worker_name;
	}
	public void setWorker_name(String worker_name) {
		this.worker_name = worker_name;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getBom_id() {
		return bom_id;
	}
	public void setBom_id(String bom_id) {
		this.bom_id = bom_id;
	}
	public String getCp_id() {
		return cp_id;
	}
	public void setCp_id(String cp_id) {
		this.cp_id = cp_id;
	}
	
	
	public int getBom_reqAm() {
		return bom_reqAm;
	}
	public void setBom_reqAm(int bom_reqAm) {
		this.bom_reqAm = bom_reqAm;
	}
	public String getProc_id() {
		return proc_id;
	}
	public void setProc_id(String proc_id) {
		this.proc_id = proc_id;
	}
	public String getProc_name() {
		return proc_name;
	}
	public void setProc_name(String proc_name) {
		this.proc_name = proc_name;
	}
	@Override
	public String toString() {
		return "WorkOrderDTO [woNum=" + woNum + ", woDate=" + woDate + ", woDuedate=" + woDuedate + ", woPQ=" + woPQ
				+ ", woAQ=" + woAQ + ", workerID=" + workerID + ", item_code=" + item_code + ", worker_name="
				+ worker_name + ", item_name=" + item_name + ", bom_id=" + bom_id + ", bom_reqAm=" + bom_reqAm
				+ ", cp_id=" + cp_id + ", proc_id=" + proc_id + ", proc_name=" + proc_name + "]";
	}

	
	
	
	
	
}
