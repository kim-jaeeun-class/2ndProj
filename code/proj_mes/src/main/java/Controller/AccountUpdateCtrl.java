package Controller;

import Service.Account_Service;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/AccountManage/update")
public class AccountUpdateCtrl extends HttpServlet {
  private final Account_Service service = new Account_Service();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      String workerId    = req.getParameter("workerId");
      String dapartId2   = req.getParameter("dapartId2");
      String workerCando = req.getParameter("workerCando");
      service.updateDeptAndRole(workerId, dapartId2, workerCando);
      resp.sendRedirect(req.getContextPath() + "/AccountManage");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
