package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dto.Login_Dto;

/**
 * Servlet implementation class AccountManageCtrl
 */
@WebServlet("/AccountManage")
public class AccountManageCtrl extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession s = request.getSession(false);
    Login_Dto loginUser = (s != null) ? (Login_Dto) s.getAttribute("loginUser") : null;

    if (loginUser == null) {
      response.sendRedirect(request.getContextPath() + "/login");
      return;
    }
    
    boolean isAdmin = (loginUser.getWorkerGrade() == 0);
    if (!isAdmin) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    // (A) forward 사용 시: CSS가 절대경로면 정상 로드
    request.getRequestDispatcher("/Html/03_admin/03_account_manage.html")
           .forward(request, response);

    // (B) 상대경로 CSS를 유지하고 싶다면 redirect 사용
    // response.sendRedirect(request.getContextPath() + "/Html/03_admin/03_account_manage.html");
  }



//		/proj_mes/Html/03_admin/03_account_manage.html
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
