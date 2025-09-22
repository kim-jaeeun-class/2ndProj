package Controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.DefectRateDTO;
import Dto.ItemDTO;
import Dto.ProcessDTO;
import Service.DefectRateService;

@WebServlet("/defectRate")
public class DefectRateCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DefectRateService service = new DefectRateService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 검색 조건
        String procId   = request.getParameter("procId");    // 공정 ID
        String date     = request.getParameter("date");      // 일자
        String itemCode = request.getParameter("itemCode");  // 품목 ID

        // 불량률 리스트 조회
        List<DefectRateDTO> defectRates = service.getDefectRateList(procId, itemCode, date);

        // 공정, 품목 목록 (드롭다운용)
        List<ProcessDTO> processList = service.getProcessList();
        List<ItemDTO> itemList = service.getItemList();

        // JSP로 전달
        request.setAttribute("defectRates", defectRates);
        request.setAttribute("processList", processList);
        request.setAttribute("itemList", itemList);
        request.setAttribute("selectedProcId", procId);
        request.setAttribute("selectedDate", date);
        request.setAttribute("selectedItemCode", itemCode);

        request.getRequestDispatcher("/Html/10_qc_pumjil/10_defect_rate.jsp").forward(request, response);
    }
}