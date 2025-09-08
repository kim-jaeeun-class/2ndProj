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
      throws ServletException, IOException {

    HttpSession s = request.getSession(false);
    Login_Dto loginUser = (s != null) ? (Login_Dto) s.getAttribute("loginUser") : null;

    if (loginUser == null) {
      response.sendRedirect(request.getContextPath() + "/login");
      return;
    }

    // 필요시 뷰에서 쓰도록 내려주기
    request.setAttribute("workerId", loginUser.getWorkerId());
    request.setAttribute("workerGrade", loginUser.getWorkerGrade());

    // forward: (CSS 상대경로가 깨지면 redirect로 바꾸거나, 절대경로/<base> 적용)
    request.getRequestDispatcher("/Html/02_main/mainpage.html")
           .forward(request, response);
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}
