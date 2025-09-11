package Controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.WorkOrderDTO;
import Service.WorkOrderService;

@WebServlet("/workorder")
public class WorkOrderCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		WorkOrderService service = new WorkOrderService();
		List<WorkOrderDTO> basicList = service.getAllOrders();
		
		request.setAttribute("list", basicList);
		request.getRequestDispatcher("/Html/09_production/09_work_order.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		WorkOrderService service = new WorkOrderService();
		
		// 등록 영역
		// 얘네로 구분 지으니까 이 둘을... 가져오는 게? 맞지? 않나?? 
	    String woNumStr = request.getParameter("woNum");
	    
	    // 값... 준비???
	    WorkOrderDTO addDTO = new WorkOrderDTO();
	    addDTO.setWoNum(woNumStr);
	    // result에 서비스로 다리 놓은 등록 기능 호출해서 넣기
	    WorkOrderService service = new WorkOrderService();
	    int result = service.addWorkOrder(addDTO);

	    if(result > 0) {
	        response.sendRedirect("workorder"); // 등록 후 목록 갱신
	    }
	    else {
	        response.getWriter().write("등록 실패"); // TODO : 아니면 이렇게. 지금은 편의상 getWriter() 썼는데, 실제로는 경고창을 띄우든 해야 함.
	    }
	    
	    // 삭제 영역
	    // 
	    String[] selected = request.getParameterValues("chk");
	    if (selected != null) {
            String woNum = request.getParameter("woNum");
            
            // 삭제 조건 조회해서 그 조건과 일치하는 것들을 remove
            WorkOrderDTO delDTO = new WorkOrderDTO();
            delDTO.setWoNum(woNum);
            
            service.removeWorkOrder(delDTO);
        }
	        
	        response.sendRedirect("workorder");
	    }
	    
}
