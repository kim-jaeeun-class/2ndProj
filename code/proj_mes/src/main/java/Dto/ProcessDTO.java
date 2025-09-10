package Dto;

public class ProcessDTO {
	
	private String proc_id; 		// 공정 id
	private String proc_name;		// 공정 이름
	private int process_check; 	// 검사 여부 
	private String dapart_id2; 		// 부서 코드
	private String proc_img;   		// 공정 사진
	private String proc_info;		// 공정 정보
	private int proc_seq;			// 공정 순서
	private String item_code;		// 품목 코드
	private String depart_level;	// 부서 이름
	
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
	public int getProcess_check() {
		return process_check;
	}
	public void setProcess_check(int process_check) {
		this.process_check = process_check;
	}
	public String getDapart_id2() {
		return dapart_id2;
	}
	public void setDapart_id2(String dapart_id2) {
		this.dapart_id2 = dapart_id2;
	}
	public String getProc_img() {
		return proc_img;
	}
	public void setProc_img(String proc_img) {
		this.proc_img = proc_img;
	}
	public String getProc_info() {
		return proc_info;
	}
	public void setProc_info(String proc_info) {
		this.proc_info = proc_info;
	}
	public int getProc_seq() {
		return proc_seq;
	}
	public void setProc_seq(int proc_seq) {
		this.proc_seq = proc_seq;
	}
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
    public String getDepart_level() {
        return depart_level;
    }
    public void setDepart_level(String depart_level) {
        this.depart_level = depart_level;
    }
    
	@Override
	public String toString() {
		return "ProcessDTO [proc_id=" + proc_id + ", proc_name=" + proc_name + ", process_check=" + process_check
				+ ", dapart_id2=" + dapart_id2 + ", proc_img=" + proc_img + ", proc_info=" + proc_info + ", proc_seq="
				+ proc_seq + ", item_code=" + item_code + ", depart_level=" + depart_level + "]";
	}
	


}
