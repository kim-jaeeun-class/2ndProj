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

        String action = request.getParameter("action");
        List<WorkOrderDTO> basicList = null;

        if ("search".equals(action)) {
            String dateStr = request.getParameter("wo-filter-date");
            if (dateStr != null && !dateStr.isEmpty()) {
                dto.setWoDate(Date.valueOf(dateStr));
                basicList = service.getFilteredOrders(dto);
            }
            else {
                basicList = service.getAllOrders();
            }
        }
        else {
            basicList = service.getAllOrders();
        }

        // 품목 목록
        List<WorkOrderDTO> itemList = service.getItemsWO(new WorkOrderDTO());

        // 상세 조회 또는 수정폼 진입
        String reqWoNum = request.getParameter("wo_num");
        if (reqWoNum != null && !reqWoNum.isEmpty()) {
            WorkOrderDTO single = service.getWorkOrder(reqWoNum);
            if (single != null) {
                WorkOrderDTO bomInfo = service.getBOMInfo(single.getItem_code());
                WorkOrderDTO procInfo = service.getProcInfo(single.getItem_code());

                request.setAttribute("detailWO", single);
                request.setAttribute("detailBOM", bomInfo);
                request.setAttribute("detailPROC", procInfo);
            }
            if ("edit".equals(action)) {
                request.setAttribute("editMode", "true");
            }
            else {
                request.setAttribute("viewMode", "true");
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
            WorkOrderDTO dto = new WorkOrderDTO();
            dto.setWoNum(request.getParameter("wo_num"));
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
