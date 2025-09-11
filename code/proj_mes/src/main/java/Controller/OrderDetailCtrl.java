package Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.ItemDTO;
import Dto.OrderDTO;
import Service.OrderService;

@WebServlet("/orderDetail")
public class OrderDetailCtrl extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		System.out.println("/orderDetail doGet 실행");
		
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String key = request.getParameter("key");
	    if (key == null || key.isBlank()) { response.sendError(400, "key 필수"); return; }

	    OrderService svc = new OrderService();
	    OrderDTO cond = new OrderDTO(); cond.setOrder_key(key.trim());
	    OrderDTO order = svc.getOneOrder(cond);
	    if (order == null) { response.sendError(404, "없음"); return; }

	    List<ItemDTO> items = svc.getOrderItems(order);
	    // 기본은 상세 '보기'
	    String mode = request.getParameter("mode");
	    if (mode == null || mode.isBlank()) mode = "view";

	    request.setAttribute("mode", mode);   // view | edit
	    request.setAttribute("order", order);
	    request.setAttribute("items", items);
	    request.getRequestDispatcher("07_orderRegistration.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
