package Dto;

public class Login_Dto {
	
	//아이디와 타입 선언
	private String us_id;
	private String us_pw;
	private int grade;
	
    public Login_Dto(String id,  int grade) {
        this.us_id = id;
        this.grade = grade;
    }
	
	
	public String getUs_id() {
		return us_id;
	}


	public void setUs_id(String us_id) {
		this.us_id = us_id;
	}


	public String getUs_pw() {
		return us_pw;
	}


	public void setUs_pw(String us_pw) {
		this.us_pw = us_pw;
	}


	public int getGrade() {
		return grade;
	}


	public void setGrade(int grade) {
		this.grade = grade;
	}


	@Override
	public String toString() {
		return "Login_Dto [us_id=" + us_id + ", us_pw=" + us_pw + ", grade=" + grade + "]";
	}



	
}
