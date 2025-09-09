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
		
		// 여기는 필터 값 전달 받기 영역! 근데 필터 버튼에서 받을 때 name 뭘로 했는지 확인해야 함 -> 확인해서 수정함
        String stat = request.getParameter("stat");
        List<WorkOrderDTO> filteredList = service.getFilteredOrders(stat);

        request.setAttribute("list", filteredList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/workorderList.jsp");
        dispatcher.forward(request, response);
        
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		// 등록 영역
		// 얘네로 구분 지으니까 이 둘을... 가져오는 게? 맞지? 않나?? 
	    String woNumStr = request.getParameter("woNum");
	    String woDateStr = request.getParameter("woDate");
	    Date woDate = Date.valueOf(woDateStr);
	    
	    // 값... 준비???
	    WorkOrderDTO addDTO = new WorkOrderDTO();
	    addDTO.setWoNum(Integer.parseInt(woNumStr));
	    addDTO.setWoDate(woDate);
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
	        for (String s : selected) {
	        	// jsp에서 value 값 실제 작업지시코드처럼 줬으니까 split로 분리하기
	            String[] parts = s.split("-");
	            // split으로 잘 나뉘었을 경우,
	            if(parts.length == 2) {
	                Date date = Date.valueOf(parts[0]);
	                int woNum = Integer.parseInt(parts[1]);
	                
	                // 삭제 조건 조회해서 그 조건과 일치하는 것들을 remove
	                WorkOrderDTO delDTO = new WorkOrderDTO();
	                delDTO.setWoDate(date);
	                delDTO.setWoNum(woNum);
	                
	                service.removeWorkOrder(delDTO);
	            }
	        }
	        response.sendRedirect("workorder");
	    }
	    
	}

}
