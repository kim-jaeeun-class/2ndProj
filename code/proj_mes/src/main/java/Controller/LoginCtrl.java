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
		
		//html과 연결 성공
		String id = request.getParameter("userid");
		String pw = request.getParameter("password");
		
		System.out.println("userid = " + id);
		System.out.println("password = " + pw);
		
		//이제 DB 작업 시작합니다.
		//DAO함수 -> Service연결
		
		
		
		
		
		
	}

}
