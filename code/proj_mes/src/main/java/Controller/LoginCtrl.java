package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//test  
//id : admin
//pw : admin
@WebServlet("/login")
public class LoginCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public LoginCtrl() {

    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Login Ctrl POST ");
		
		//html�� ���� ����
		String id = request.getParameter("userid");
		String pw = request.getParameter("password");
		
		System.out.println("userid = " + id);
		System.out.println("password = " + pw);
		
		//���� DB �۾� �����մϴ�.
		//DAO�Լ� -> Service����
		
		
		
		
		
		
	}

}
