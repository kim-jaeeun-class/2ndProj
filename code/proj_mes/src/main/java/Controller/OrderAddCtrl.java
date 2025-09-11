package Controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.OrderDTO;
import Service.Client_Service;
import Service.ItemService;
import Service.OrderService;


@WebServlet("/orderAdd")
public class OrderAddCtrl extends HttpServlet {
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		System.out.println("/orderAdd doGet 실행");
		
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		ItemService itemService = new ItemService();
		List itemList = itemService.getAllItem();
		
		Client_Service clientService = new Client_Service();
		List clientList = clientService.getAllItem();
		
		OrderService orderService = new OrderService();
		List deptList = orderService.getAllDep();
			
		request.setAttribute("itemList", itemList);
		request.setAttribute("clientList", clientList);
		request.setAttribute("deptList", deptList);
		request.getRequestDispatcher("/07_orderRegistration.jsp").forward(request, response);
	}
	
	private Integer toIntOrDefault(String s, int def) {
        try { return (s == null || s.isBlank()) ? def : Integer.parseInt(s.trim()); }
        catch (Exception e) { return def; }
    }
	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("/orderAdd doPost 실행");
		
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		OrderDTO dto = new OrderDTO();
		
		try {
			// parameter 및 dto
			String orderNumber = request.getParameter("order_num");
			dto.setOrder_number(orderNumber);
			
//			String sOrderDate = request.getParameter("order_date");
//			Date orderdate = Date.valueOf(sOrderDate);
//			dto.setOrder_date(orderdate);
			
			String sOrderPay = request.getParameter("order_pay");
			Date orderpay = Date.valueOf(sOrderPay);
			dto.setOrder_pay(orderpay);
			
			String sState = request.getParameter("order_state");
			int state = Integer.parseInt(sState);
			dto.setOrder_state(state);
			
			String clientId = request.getParameter("client_id");
			dto.setClient_id(clientId);
			System.out.println("client: "+ clientId);
			
			String workerId = request.getParameter("worker_id");
			dto.setWorker_id(workerId);
			
			String deptId = request.getParameter("dept_id"); // DTO의 필드명은 dapart_ID2
			dto.setDapart_ID2(deptId);
			int sstate = toIntOrDefault(request.getParameter("order_state"), 0); // 기본 0: 임시저장
			
			
			
			// DB 삽입
			OrderService orderService = new OrderService();
			int result = orderService.addOrder(dto);
			
			if(result > 0) {
			      response.sendRedirect(request.getContextPath() + "/orderList");
		    } else {
			      response.sendRedirect(request.getContextPath() + "/orderAdd?error=fail");
		    }
			
			System.out.println("삽입 결과 : "+ result);
			
			

		}catch (Exception e) {
			e.printStackTrace();
		}

    }

    
}
