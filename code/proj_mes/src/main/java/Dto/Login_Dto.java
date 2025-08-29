package Dto;

public class Login_Dto {
	
	//아이디와 타입 선언
	private String us_id;
	private String us_pw;
	
	//아이디 받는 메소드
	public String getUs_id() {
		return us_id;
	}
	
	//아이디 설정하는 메소드
	public void setUs_id(String us_id) {
		this.us_id = us_id;
	}
	
	//비밀번호 받아오는 메소드
	public String getUs_pw() {
		return us_pw;
	}
	
	//비밀번호 설정하는 메소드
	public void setUs_pw(String us_pw) {
		this.us_pw = us_pw;
	}
	
	
	@Override
	public String toString() {
		return "Login_Dto [us_id=" + us_id + ", us_pw=" + us_pw + "]";
	}
	
}
