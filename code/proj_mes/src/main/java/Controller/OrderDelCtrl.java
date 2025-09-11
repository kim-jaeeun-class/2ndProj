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
		
		String order_key = request.getParameter("order_key");
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrder_key(order_key);
		
		// 3. DB 접속해서 삭제
		OrderService orderService = new OrderService();
		int result = orderService.removeOrder(orderDTO);
		System.out.println(result + " 만큼 삭제 되었습니다.");
		
		// 4. 이후는 전체 목록으로 이동
		response.sendRedirect("orderList");
		
		
		
	}

}
