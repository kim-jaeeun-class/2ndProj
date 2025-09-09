package Controller;

import Service.Account_Service;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;

@WebServlet("/AccountManage/delete")
public class AccounDelCtrl extends HttpServlet {
  private final Account_Service service = new Account_Service();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      String[] ids = req.getParameterValues("ids");
      if (ids != null && ids.length > 0) {
        service.deleteByIds(Arrays.asList(ids));
      }
      resp.sendRedirect(req.getContextPath() + "/AccountManage");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
