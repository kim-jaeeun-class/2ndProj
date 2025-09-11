package Controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.OrderDTO;
import Service.OrderService;

@WebServlet("/orderDel")
public class OrderDelCtrl extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("/orderDel doPost 실행");
		
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String order_key_param = request.getParameter("order_key"); // "13,14,15" 형태 가능
	    System.out.println("[DEBUG] received order_key param = [" + order_key_param + "]");

	    if (order_key_param == null || order_key_param.trim().isEmpty()) {
	      // 값이 비었으면 바로 목록으로 (또는 에러 표시)
	      response.sendRedirect("orderList");
	      return;
	    }

	    OrderService orderService = new OrderService();
	    int totalAffected = 0;

	    // 콤마 분할 삭제
	    for (String key : order_key_param.split(",")) {
	      String k = key.trim();
	      if (k.isEmpty()) continue;

	      OrderDTO dto = new OrderDTO();
	      dto.setOrder_key(k);
	      // dto.setOrder_number(null); // 명확히

	      int r = orderService.removeOrder(dto);
	      totalAffected += r;
	    }

	    System.out.println(totalAffected + " 만큼 삭제 되었습니다.");
	    response.sendRedirect("orderList");
		
		
		
	}

}
