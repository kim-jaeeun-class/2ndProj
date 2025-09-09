package Controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.Login_Dto;
import Dto.Account_DTO;
import Service.Account_Service;

@WebServlet("/AccountManage")
public class AccountManageCtrl extends HttpServlet {
  private final Account_Service service = new Account_Service();

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

    try {
      String q = request.getParameter("q");
      List<Account_DTO> accounts = (q == null || q.trim().isEmpty())
          ? service.listAll()
          : service.search(q.trim());
      request.setAttribute("accounts", accounts);
    } catch (Exception e) {
      throw new ServletException(e);
    }


    request.getRequestDispatcher("/Html/03_admin/03_account_manage.jsp")
           .forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}
