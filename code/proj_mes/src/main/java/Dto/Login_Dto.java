package Dto;

public class Login_Dto {
	
	//���̵�� Ÿ�� ����
	private String us_id;
	private String us_pw;
	
	//���̵� �޴� �޼ҵ�
	public String getUs_id() {
		return us_id;
	}
	
	//���̵� �����ϴ� �޼ҵ�
	public void setUs_id(String us_id) {
		this.us_id = us_id;
	}
	
	//��й�ȣ �޾ƿ��� �޼ҵ�
	public String getUs_pw() {
		return us_pw;
	}
	
	//��й�ȣ �����ϴ� �޼ҵ�
	public void setUs_pw(String us_pw) {
		this.us_pw = us_pw;
	}
	
	
	@Override
	public String toString() {
		return "Login_Dto [us_id=" + us_id + ", us_pw=" + us_pw + "]";
	}
	
}
