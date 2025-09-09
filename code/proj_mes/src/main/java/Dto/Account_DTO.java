package Dto;

public class Account_DTO {
    private String workerId;
    private String workerName;
    private int workerGrade;
    private String workerEmail;
    private String workerPhone;
    private String workerBacode; // 주민번호
    private String workerCando;
	private String workerPwHash; 
    private String dapartId2;    
    
    
    public String getWorkerId() {
		return workerId;
	}
	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public int getWorkerGrade() {
		return workerGrade;
	}
	public void setWorkerGrade(int workerGrade) {
		this.workerGrade = workerGrade;
	}
	public String getWorkerEmail() {
		return workerEmail;
	}
	public void setWorkerEmail(String workerEmail) {
		this.workerEmail = workerEmail;
	}
	public String getWorkerPhone() {
		return workerPhone;
	}
	public void setWorkerPhone(String workerPhone) {
		this.workerPhone = workerPhone;
	}
	public String getWorkerBacode() {
		return workerBacode;
	}
	public void setWorkerBacode(String workerBacode) {
		this.workerBacode = workerBacode;
	}
	public String getWorkerCando() {
		return workerCando;
	}
	public void setWorkerCando(String workerCando) {
		this.workerCando = workerCando;
	}
	public String getWorkerPwHash() {
		return workerPwHash;
	}
	public void setWorkerPwHash(String workerPwHash) {
		this.workerPwHash = workerPwHash;
	}
	public String getDapartId2() {
		return dapartId2;
	}
	public void setDapartId2(String dapartId2) {
		this.dapartId2 = dapartId2;
	}
	
	
	@Override
	public String toString() {
		return "Account_DTO [workerId=" + workerId + ", workerName=" + workerName + ", workerGrade=" + workerGrade
				+ ", workerEmail=" + workerEmail + ", workerPhone=" + workerPhone + ", workerBacode=" + workerBacode
				+ ", workerCando=" + workerCando + ", workerPwHash=" + workerPwHash + ", dapartId2=" + dapartId2 + "]";
	}

	

}
