package Controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.OrderDTO;
import Service.OrderService;

@WebServlet("/orderList")
public class OrderListCtrl extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    System.out.println("/orderList doGet 실행");
    request.setCharacterEncoding("utf-8");
    response.setContentType("text/html;charset=utf-8");

    List<OrderDTO> orderList = Collections.emptyList(); // 초기값

    try {
      orderList = new OrderService().getAllOrder();    
    } catch (Exception e) {
      log("주문 목록 조회 실패", e);
      request.setAttribute("error", "목록 조회 중 오류가 발생했습니다."); // JSP에서 표시 가능
    }

    request.setAttribute("orderList", orderList);
    request.getRequestDispatcher("/07_orderList.jsp").forward(request, response);
  }

  
}
