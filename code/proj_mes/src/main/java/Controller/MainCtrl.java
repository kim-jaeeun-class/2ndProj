package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dto.Login_Dto;

@WebServlet("/mainpage")
public class MainCtrl extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {HttpSession s = request.getSession(false);
    
    Login_Dto loginUser = (s != null) ? (Login_Dto) s.getAttribute("loginUser") : null;
    
    
//   만일 로그인 세션이 없는경우 로그인 페이지로 넘어가게
    if (loginUser == null) {
      response.sendRedirect(request.getContextPath() + "/login");
      return;
    }

    request.setAttribute("workerId", loginUser.getWorkerId());
    request.setAttribute("workerGrade", loginUser.getWorkerGrade());

    request.getRequestDispatcher("/Html/02_main/mainpage.jsp")
           .forward(request, response);
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}
