// package Dto;
package Dto;

public class Login_Dto {
    private String workerId;
    private int workerGrade;

    public Login_Dto(String workerId, int workerGrade) {
        this.workerId = workerId;
        this.workerGrade = workerGrade;
    }

    public String getWorkerId() { 
    	return workerId; 
    }
    public void setWorkerId(String workerId) {
    	this.workerId = workerId; 
    	}

    public int getWorkerGrade() {
    	return workerGrade; 
    	}
    public void setWorkerGrade(int workerGrade) {
    	this.workerGrade = workerGrade;
    	}

    @Override
    public String toString() {
        return "Login_Dto [workerId=" + workerId + ", workerGrade=" + workerGrade + "]";
    }
}
