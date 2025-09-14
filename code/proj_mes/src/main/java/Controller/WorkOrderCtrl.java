package Controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

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
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        WorkOrderService service = new WorkOrderService();
        WorkOrderDTO dto = new WorkOrderDTO();

        List<WorkOrderDTO> basicList;
        String action = request.getParameter("action");
        String dateStr = request.getParameter("wo-filter-date");


        // 전체 / 필터 조회
        if ("search".equals(action) && dateStr != null && !dateStr.isEmpty()) {
            basicList = service.getFilteredOrders(new WorkOrderDTO(){{
                setWoDate(Date.valueOf(dateStr));
            }});
        } else {
            basicList = service.getAllOrders();
        }

        // 품목 목록
        List<WorkOrderDTO> itemList = service.getItemsWO(new WorkOrderDTO());

        // 단일 조회
        String reqWoNum = request.getParameter("wo_num");
        if (reqWoNum != null && !reqWoNum.isEmpty()) {
            WorkOrderDTO single = service.getWorkOrder(reqWoNum);
            if (single != null) {
                List<WorkOrderDTO> bomList = service.getBOMList(single.getItem_code());
                List<WorkOrderDTO> procList = service.getPROCList(single.getItem_code());

                request.setAttribute("detailWO", single);
                request.setAttribute("detailBOM", bomList);
                request.setAttribute("detailPROC", procList);
                request.setAttribute("viewMode", true); // 상세 패널 열기
            }
        }

        request.setAttribute("list", basicList);
        request.setAttribute("itemAll", itemList);
        request.getRequestDispatcher("/Html/09_production/09_work_order.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        WorkOrderService service = new WorkOrderService();
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            WorkOrderDTO dto = new WorkOrderDTO();
            dto.setWoDate(Date.valueOf(request.getParameter("wo_date")));
            dto.setWoDuedate(Date.valueOf(request.getParameter("wo_duedate")));
            dto.setWorkerID(request.getParameter("worker_id"));
            dto.setWoPQ(Integer.parseInt(request.getParameter("wo_pq")));
            dto.setItem_code(request.getParameter("item_code"));

            String bomId = request.getParameter("bom_id");
            if (bomId == null || bomId.isEmpty()) {
                dto.setBom_id("0");
            }
            else {
                dto.setBom_id(bomId);
            }
            String procId = request.getParameter("proc_id");
            if (procId == null || procId.isEmpty()) {
                dto.setProc_id("0");
            }
            else {
                dto.setProc_id(procId);
            }

            try {
                String woNum = service.generateWoNum(request.getParameter("wo_date"));
                dto.setWoNum(woNum);
                service.addWorkOrder(dto);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if ("delete".equals(action)) {
            String[] ids = request.getParameterValues("chk");
            if (ids != null) {
                for (String id : ids) {
                    WorkOrderDTO dto = new WorkOrderDTO();
                    dto.setWoNum(id);
                    service.removeWorkOrder(dto);
                }
            }

        }
        else if ("update".equals(action)) {
            String woNum = request.getParameter("wo_num");
            String dueDateStr = request.getParameter("wo_duedate");
            String pqStr = request.getParameter("wo_pq");

            // 필수 값 체크
            if(woNum == null || woNum.isEmpty() || dueDateStr == null || dueDateStr.isEmpty() || pqStr == null || pqStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "필수 파라미터 누락");
                return;
            }

            WorkOrderDTO dto = new WorkOrderDTO();
            dto.setWoNum(woNum);
            dto.setWoDuedate(Date.valueOf(dueDateStr)); // yyyy-MM-dd 형식 확인
            dto.setWorkerID(request.getParameter("worker_id"));
            dto.setWoPQ(Integer.parseInt(pqStr));
            dto.setItem_code(request.getParameter("item_code"));

            String bomId = request.getParameter("bom_id");
            dto.setBom_id((bomId == null || bomId.isEmpty()) ? "0" : bomId);

            String procId = request.getParameter("proc_id");
            dto.setProc_id((procId == null || procId.isEmpty()) ? "0" : procId);

            service.editAll(dto);

        }
        else if ("editAQ".equals(action)) {
            String woNum = request.getParameter("wo_num");
            String aqStr = request.getParameter("wo_aq");
            if (woNum != null && !woNum.isEmpty() && aqStr != null && !aqStr.isEmpty()) {
                int aq = Integer.parseInt(aqStr);
                service.updateWorkOrderAQ(woNum, aq);
            }
        }

        response.sendRedirect(request.getContextPath() + "/workorder");
    }
}
