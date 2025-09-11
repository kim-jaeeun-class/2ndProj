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
		
		OrderService service = new OrderService();

		
		// 1) 파라미터 받기: 목록에서 ?key=발주번호&mode=view 로 넘어옴
        String key = request.getParameter("key");   // 발주키
        String mode        = request.getParameter("mode");  // "view" or "edit"
        if (mode == null || mode.isBlank()) mode = "view";  // 기본값

        // 2) DTO에 담기
        OrderDTO dto = new OrderDTO();
        dto.setOrder_key(key);

        // 3) 서비스 호출 - 헤더(기본정보) 1건
        OrderDTO order = service.getOneOrder(dto);

        // 4) 없는 번호면 404
        String skey = request.getParameter("key");
        if (key == null || key.trim().isEmpty()) {
            response.sendError(400, "필수 파라미터 누락(key)");
            return;
        }
        OrderDTO sorder = new OrderDTO();
        sorder.setOrder_key(key.trim());
        OrderDTO orderDTO = service.getOneOrder(sorder);


        // 5) 서비스 호출 - 상세(품목) 목록
        //    (service.getOrderItemsByKey는 orderDTO.getOrder_key()를 사용)
        List<ItemDTO> items = service.getOrderItems(order);

        // 6) JSP에서 쓸 값 요청영역에 담기
        request.setAttribute("order", order);   // 헤더
        request.setAttribute("items", items);   // 상세 라인들
        request.setAttribute("mode",  mode);    // 보기/수정 모드

        // 7) 상세/등록 JSP 재사용해서 forward
        request.getRequestDispatcher("07_orderRegistration.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
