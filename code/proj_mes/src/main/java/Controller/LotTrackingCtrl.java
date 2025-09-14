package Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.LotTrackingDTO;
import Dto.ItemDTO;
import Dto.ProcessDTO;
import Service.LotTrackingService;

@WebServlet("/lotTracking")
public class LotTrackingCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LotTrackingService service = new LotTrackingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        // Ajax 요청 처리 (공정 이력 / 검사 결과)
        if ("history".equals(action) || "inspection".equals(action)) {
            String lotId = request.getParameter("lotId");
            List<LotTrackingDTO> list = "history".equals(action)
                    ? service.getProcessHistoryByLot(lotId)
                    : service.getInspectionResultByLot(lotId);

            response.setContentType("application/json; charset=UTF-8");
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                LotTrackingDTO d = list.get(i);
                if (i > 0) sb.append(',');
                sb.append('{');

                if ("history".equals(action)) {
                    sb.append("\"procName\":\"").append(d.getProcName() == null ? "" : d.getProcName()).append("\",")
                      .append("\"startTime\":\"").append(d.getStartTime() == null ? "" : d.getStartTime()).append("\",")
                      .append("\"endTime\":\"").append(d.getEndTime() == null ? "" : d.getEndTime()).append("\",")
                      .append("\"workerId\":\"").append(d.getWorkerId() == null ? "" : d.getWorkerId()).append("\"");
                } else {
                    sb.append("\"procName\":\"").append(d.getProcName() == null ? "" : d.getProcName()).append("\",")
                      .append("\"irType\":\"").append(d.getIrType() == null ? "" : d.getIrType()).append("\",")
                      .append("\"defectName\":\"").append(d.getDefectName() == null ? "" : d.getDefectName()).append("\",")
                      .append("\"bdQuantity\":").append(d.getBdQuantity()).append(",")
                      .append("\"workerId\":\"").append(d.getWorkerId() == null ? "" : d.getWorkerId()).append("\",")
                      .append("\"inspectTime\":\"").append(d.getEndTime() == null ? "" : d.getEndTime()).append("\"");
                }

                sb.append('}');
            }
            sb.append(']');

            try (PrintWriter out = response.getWriter()) {
                out.write(sb.toString());
            }
            return;
        }

        // JSP 페이지 요청 처리
        String itemCode = request.getParameter("itemCode");
        String date = request.getParameter("date");

        List<LotTrackingDTO> lotList = service.getLotTrackingList(itemCode, date);
        List<ItemDTO> itemList = service.getItemList();
        List<ProcessDTO> processList = service.getAllProcessList();

        request.setAttribute("lotList", lotList);
        request.setAttribute("itemList", itemList);
        request.setAttribute("processList", processList);
        request.setAttribute("selectedItemCode", itemCode);
        request.setAttribute("selectedDate", date);

        RequestDispatcher rd = request.getRequestDispatcher("/Html/10_qc_pumjil/10_lot_tracking.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            LotTrackingDTO dto = new LotTrackingDTO();
            dto.setLotId(request.getParameter("lotId"));
            dto.setItemCode(request.getParameter("itemCode"));
            try {
                dto.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            } catch (Exception e) {
                dto.setQuantity(0);
            }
            dto.setStartTime(request.getParameter("startTime"));
            dto.setEndTime(request.getParameter("endTime"));
            dto.setWorkerId(request.getParameter("workerId"));

            service.addLotTracking(dto);

        } else if ("update".equals(action)) {
            LotTrackingDTO dto = new LotTrackingDTO();
            dto.setLotId(request.getParameter("lotId"));
            dto.setQualityState(request.getParameter("qualityState"));
            try {
                dto.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            } catch (Exception e) {
                dto.setQuantity(0);
            }
            dto.setEndTime(request.getParameter("endTime"));

            service.updateLotTracking(dto);

        } else if ("delete".equals(action)) {
            String lotId = request.getParameter("lotId");
            service.deleteLotTracking(lotId);
        }

        response.sendRedirect(request.getContextPath() + "/lotTracking");
    }
}