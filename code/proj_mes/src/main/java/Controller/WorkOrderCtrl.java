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

import Dto.BOMDTO;
import Dto.ProPlanDTO;
import Dto.WorkOrderDTO;
import Service.WorkOrderService;

@WebServlet("/workorder")
public class WorkOrderCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		// 전체 조회 및 품목 조회
		WorkOrderService service = new WorkOrderService();
		WorkOrderDTO dto = new WorkOrderDTO();
		
		List<WorkOrderDTO> basicList = service.getAllOrders();
		List<WorkOrderDTO> itemList = service.getItemsWO(dto);
		
		request.setAttribute("list", basicList);
		request.setAttribute("itemAll", itemList);
		request.getRequestDispatcher("/Html/09_production/09_work_order.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost 진입");

		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		WorkOrderService service = new WorkOrderService();
		String action = request.getParameter("action");
		
		// 왜 안 되니
		System.out.println("action: " + request.getParameter("action"));
		
		if("add".equals(action)) {
			// 등록 영역
		    WorkOrderDTO dto = new WorkOrderDTO();
		    dto.setWoNum(request.getParameter("wo_num"));
		    dto.setWoDate(Date.valueOf(request.getParameter("wo_date")));
		    dto.setWoDuedate(Date.valueOf(request.getParameter("wo_duedate")));
		    dto.setWorkerID(request.getParameter("worker_id"));
		    dto.setWoPQ(Integer.parseInt(request.getParameter("wo_pq")));
		    dto.setItem_code(request.getParameter("item_code"));
		    service.addWorkOrder(dto);
		    
		    List<WorkOrderDTO> itemList = service.getItemsWO(new WorkOrderDTO());
		    request.setAttribute("itemAll", itemList);
		}
		else if("delete".equals(action)) {
            String[] ids = request.getParameterValues("chk");
            int delCount = 0;
            if(ids != null) {
                for(String id : ids) {
                    WorkOrderDTO dto = new WorkOrderDTO();
                    dto.setWoNum(id); 
                    delCount += service.removeWorkOrder(dto);
                }
            }
            request.setAttribute("msg", delCount + "건 삭제");
		}		    
		else if("search".equals(action)) {
			System.out.println();
		}
		else if("item-add".equals(action)) {
			System.out.println();
		}
		
	    List<WorkOrderDTO> basicList = service.getAllOrders();
	    request.setAttribute("list", basicList);
	    request.getRequestDispatcher("/Html/09_production/09_work_order.jsp").forward(request, response);
   }
	    
}
