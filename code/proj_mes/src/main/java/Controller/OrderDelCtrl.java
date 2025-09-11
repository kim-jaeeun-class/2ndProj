package Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Service.OrderService;

@WebServlet("/orderDel")
public class OrderDelCtrl extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    System.out.println("/orderDel doPost 실행");
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=utf-8");

    String param = request.getParameter("order_key"); // "A20250912..,A20250913.." 가능
    System.out.println("[DEBUG] received order_key param = [" + param + "]");

    if (param == null || param.trim().isEmpty()) {
      response.sendRedirect(request.getContextPath() + "/orderList");
      return;
    }

    // CSV → List<String>
    List<String> keys = new ArrayList<>();
    for (String s : param.split(",")) {
      if (s != null) {
        String k = s.trim();
        if (!k.isEmpty()) keys.add(k);
      }
    }

    try {
      new OrderService().removeOrdersCascade(keys); // 상세→헤더 트랜잭션으로 삭제
      response.sendRedirect(request.getContextPath() + "/orderList");
    } catch (Exception e) {
      e.printStackTrace();
      response.getWriter().write("삭제 실패: " + e.getMessage());
    }
  }
}
