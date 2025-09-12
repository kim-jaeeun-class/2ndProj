package Controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.ProPlanDTO;
import Service.ProPlanService;

@WebServlet("/proplan")
public class ProPlanCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		ProPlanService service = new ProPlanService();
		List<ProPlanDTO> basicList = service.getAllPlans();
		
		request.setAttribute("list", basicList);
		request.getRequestDispatcher("/Html/09_production/09_proPlan.jsp").forward(request, response);
	}
	// TODO : 지금 문제... 새로고침하면 똑같은 데이터가 input됨...
	// 나중에 신경쓰고 작업 지시서나 하자
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
	    ProPlanService service = new ProPlanService();
	    String action = request.getParameter("action"); // "add", "delete", "search" 구분
	    
	    if ("add".equals(action)) {
	        // 등록
	        ProPlanDTO dto = new ProPlanDTO();
	        dto.setItemCode(request.getParameter("item-no"));
	        dto.setCpCount(Integer.parseInt(request.getParameter("plan-amount")));
	        dto.setStartDate(Date.valueOf(request.getParameter("plan-start")));
	        dto.setEndDate(Date.valueOf(request.getParameter("plan-end")));
	        dto.setBigo(request.getParameter("bigo"));

	        int result = service.addPlan(dto);
	        request.setAttribute("msg", result > 0 ? "등록 성공" : "등록 실패");
	    }
	    else if ("delete".equals(action)) {
	        // 메인 체크박스 삭제
	        String[] ids = request.getParameterValues("chk");
	        String cpID = request.getParameter("cpID"); // 상세 삭제용
	        int deleteCount = 0;
	        if (ids != null) {
	            for (String id : ids) {
	                ProPlanDTO dto = new ProPlanDTO();
	                dto.setCpID(id);
	                deleteCount += service.removePlan(dto);
	            }
	        } else if (cpID != null && !cpID.isEmpty()) {
	            // 상세 패널 삭제
	            ProPlanDTO dto = new ProPlanDTO();
	            dto.setCpID(cpID);
	            deleteCount += service.removePlan(dto);
	        }
	        request.setAttribute("msg", deleteCount + "건 삭제 완료");
	    }
	    else if ("search".equals(action)) {
	        // 필터 조회
	        ProPlanDTO dto = new ProPlanDTO();
	        String start = request.getParameter("filter-startDate");
	        String end = request.getParameter("filter-endDate");
	        if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
	            dto.setStartDate(java.sql.Date.valueOf(start));
	            dto.setEndDate(java.sql.Date.valueOf(end));
	        }

	        String align = request.getParameter("align"); // "upDate" or "downDate"
	        List<ProPlanDTO> filtered;
	        if ("downDate".equals(align)) {
	            filtered = service.getDateDownPlans(dto);
	        }
	        else {
	            filtered = service.getDateUpPlans(dto);
	        }
	        request.setAttribute("list", filtered);
	        request.getRequestDispatcher("/Html/09_production/09_proPlan.jsp").forward(request, response);
	        return; // 조회 후 바로 JSP 출력
	    }

	    // 작업 후 항상 최신 리스트 출력
	    List<ProPlanDTO> basicList = service.getAllPlans();
	    request.setAttribute("list", basicList);
	    request.getRequestDispatcher("/Html/09_production/09_proPlan.jsp").forward(request, response);
	}

}
