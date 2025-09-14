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
        // 한글 깨짐 방지
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
            // 기본 전체조회
            basicList = service.getAllOrders();
        }

        // 품목 목록
        List<WorkOrderDTO> itemList = service.getItemsWO(new WorkOrderDTO());

        // 상세(뷰) 또는 수정폼(edit) 요청 처리 : ?action=view&wo_num=YYYY... or ?action=edit&wo_num=...
        String reqWoNum = request.getParameter("wo_num");
        if (reqWoNum != null && !reqWoNum.isEmpty()) {
            WorkOrderDTO single = service.getWorkOrder(reqWoNum);
            if (single != null) {
                // bom/proc 정보도 함께 세팅 (service에서 제공)
                WorkOrderDTO bomInfo = service.getBOMInfo(single.getItem_code());
                WorkOrderDTO procInfo = service.getProcInfo(single.getItem_code());

                request.setAttribute("detailWO", single); // 단건 데이터
                request.setAttribute("detailBOM", bomInfo);
                request.setAttribute("detailPROC", procInfo);
            }
            // action에 따라 JSP에서 패널 열기/편집 모드 판단
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
        System.out.println("doPost 진입");
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        WorkOrderService service = new WorkOrderService();
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            // 등록
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
            int delCount = 0;
            if (ids != null) {
                for (String id : ids) {
                    WorkOrderDTO dto = new WorkOrderDTO();
                    dto.setWoNum(id);
                    delCount += service.removeWorkOrder(dto);
                }
            }
            request.setAttribute("msg", delCount + "건 삭제");
        }
        else if ("update".equals(action)) {
            // 전체 수정(수정 폼에서 submit)
            WorkOrderDTO dto = new WorkOrderDTO();
            dto.setWoNum(request.getParameter("wo_num")); // 원래 키
            dto.setWoDuedate(Date.valueOf(request.getParameter("wo_duedate")));
            dto.setWorkerID(request.getParameter("worker_id"));
            dto.setWoPQ(Integer.parseInt(request.getParameter("wo_pq")));
            dto.setItem_code(request.getParameter("item_code"));

            // bom/proc 처리
            String bomId = request.getParameter("bom_id");
            if (bomId == null || bomId.isEmpty()) {
                dto.setBom_id("0");
            } else {
                dto.setBom_id(bomId);
            }
            String procId = request.getParameter("proc_id");
            if (procId == null || procId.isEmpty()) {
                dto.setProc_id("0");
            } else {
                dto.setProc_id(procId);
            }

            service.editAll(dto); // dao.updateAllWO 호출
        }
        else if ("editAQ".equals(action)) {
            // 생산수량만 변경
            String woNum = request.getParameter("wo_num");
            String aqStr = request.getParameter("wo_aq");
            if (woNum != null && !woNum.isEmpty() && aqStr != null && !aqStr.isEmpty()) {
                int aq = Integer.parseInt(aqStr);
                service.updateWorkOrderAQ(woNum, aq);
            }
        }

        // 변경 후 항상 목록 최신화해서 GET으로 화면 재로딩
        response.sendRedirect(request.getContextPath() + "/workorder");
    }
}
