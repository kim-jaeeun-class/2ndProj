package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.InspectionDTO;
import Dto.ProcessDTO;
import Dto.WorkerDTO;
import Service.InspectionService;

@WebServlet("/inspection")
public class InspectionCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private InspectionService service;

    public InspectionCtrl() {
        super();
        service = new InspectionService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("/inspection doGet 실행");

        String action = request.getParameter("action");

        if ("getProcessesByLotId".equals(action)) {
            String lotId = request.getParameter("lotId");
            List<ProcessDTO> processList = service.getProcessesByLotId(lotId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            StringBuilder jsonResponse = new StringBuilder("[");
            for (int i = 0; i < processList.size(); i++) {
                ProcessDTO dto = processList.get(i);
                jsonResponse.append("{")
                        .append("\"proc_id\":\"").append(dto.getProc_id()).append("\",")
                        .append("\"proc_name\":\"").append(dto.getProc_name()).append("\"")
                        .append("}");
                if (i < processList.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            jsonResponse.append("]");
            out.print(jsonResponse.toString());

        } else if ("getResultsByLot".equals(action)) {
            String lotId = request.getParameter("lotId");
            List<InspectionDTO> resultList = service.getResultsByLotId(lotId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            StringBuilder jsonResponse = new StringBuilder("[");
            for (int i = 0; i < resultList.size(); i++) {
                InspectionDTO dto = resultList.get(i);
                jsonResponse.append("{")
                        .append("\"ir_id\":\"").append(dto.getIr_id()).append("\",")
                        .append("\"ir_type\":").append(dto.getIr_type()).append(",")
                        .append("\"gd_quantity\":").append(dto.getGd_quantity()).append(",")
                        .append("\"bd_quantity\":").append(dto.getBd_quantity()).append(",")
                        .append("\"bd_reason\":\"").append(dto.getBd_reason()).append("\",")
                        .append("\"cp_id\":\"").append(dto.getCp_id()).append("\",")
                        .append("\"proc_name\":\"").append(dto.getProc_name()).append("\",")
                        .append("\"worker_name\":\"").append(dto.getWorker_name()).append("\"")
                        .append("}");
                if (i < resultList.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            jsonResponse.append("]");
            out.print(jsonResponse.toString());

        } else if ("delete".equals(action)) {
            String irId = request.getParameter("irId");
            if (irId != null) {
                service.deleteInspection(irId);
            }
            response.sendRedirect(request.getContextPath() + "/inspection");

        } else {
            // 초기 페이지 로딩
            response.setContentType("text/html");
            List<WorkerDTO> workerList = service.getWorkers();
            request.setAttribute("workers", workerList);

            request.setAttribute("lotNumbers", service.getLotNumbers());
            request.setAttribute("inspectionResults", service.getInspectionResultsWithNames());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            request.setAttribute("today", today);

            String newId = service.generateInspectionId();
            request.setAttribute("irId", newId);

            request.getRequestDispatcher("/Html/10_qc_pumjil/10_inspection.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("/inspection doPost 실행");

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("saveAll".equals(action)) {
            try {
                BufferedReader reader = request.getReader();
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                String jsonData = jsonBuilder.toString();
                System.out.println("Received JSON data: " + jsonData);

                // 대괄호 제거
                if (jsonData.startsWith("[") && jsonData.endsWith("]")) {
                    jsonData = jsonData.substring(1, jsonData.length() - 1);
                }

                // 객체 단위로 분리
                String[] items = jsonData.split("\\},\\{");

                for (String item : items) {
                    // 앞뒤 { } 정리
                    item = item.replaceAll("^\\{", "").replaceAll("\\}$", "");

                    String[] parts = item.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    InspectionDTO dto = new InspectionDTO();

                    for (String part : parts) {
                        String[] keyValue = part.split(":", 2);
                        if (keyValue.length < 2) continue;
                        String key = keyValue[0].trim().replace("\"", "");
                        String value = keyValue[1].trim().replace("\"", "");

                        switch (key) {
                            case "ir_id":
                                dto.setIr_id(value);
                                break;
                            case "ir_type":
                                dto.setIr_type(Integer.parseInt(value));
                                break;
                            case "lot_id":
                                dto.setCp_id(value);
                                break;
                            case "proc_id":
                                dto.setCp_id(value); // proc_id와 cp_id 매핑
                                break;
                            case "worker_id":
                                dto.setWorker_id(value);
                                break;
                            case "bd_reason":
                                dto.setBd_reason(value);
                                break;
                            case "bd_quantity":
                                dto.setBd_quantity(Integer.parseInt(value));
                                break;
                            case "gd_quantity":
                                dto.setGd_quantity(Integer.parseInt(value));
                                break;
                            case "quality_state":
                                dto.setQuality_state(Integer.parseInt(value));
                                break;
                            case "inspection_date":
                                // 날짜만 들어오면 start/end_time 세팅
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date parsed = sdf.parse(value);
                                    Timestamp ts = new Timestamp(parsed.getTime());
                                    dto.setStart_time(ts);
                                    dto.setEnd_time(ts);
                                } catch (Exception e) {
                                    dto.setStart_time(new Timestamp(new Date().getTime()));
                                    dto.setEnd_time(new Timestamp(new Date().getTime()));
                                }
                                break;
                            case "remark":
                                dto.setRemark(value);
                                break;                                    
                        }
                    }

                    // ✅ 공정 필수 검증
                    if (dto.getCp_id() == null || dto.getCp_id().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().print("{\"success\":false, \"message\":\"공정은 반드시 선택해야 합니다.\"}");
                        return;
                    }

                    // 누락 값 기본 세팅
                    if (dto.getQuality_state() == 0) dto.setQuality_state(1);
                    if (dto.getStart_time() == null) dto.setStart_time(new Timestamp(new Date().getTime()));
                    if (dto.getEnd_time() == null) dto.setEnd_time(new Timestamp(new Date().getTime()));

                    service.createInspection(dto);
                }

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print("{\"success\":true, \"message\":\"데이터가 성공적으로 저장되었습니다.\"}");

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print("{\"success\":false, \"message\":\"데이터 저장 중 오류가 발생했습니다.\"}");
            }

        } else {
            // 기존 form 제출
            try {
                InspectionDTO dto = new InspectionDTO();

                dto.setIr_id(request.getParameter("ir_id"));
                dto.setIr_type(Integer.parseInt(request.getParameter("ir_type")));
                dto.setQuality_state(Integer.parseInt(request.getParameter("quality_state")));
                dto.setGd_quantity(Integer.parseInt(request.getParameter("gd_quantity")));
                dto.setBd_quantity(Integer.parseInt(request.getParameter("bd_quantity")));

                String dateStr = request.getParameter("inspection_date");
                String startTimeStr = request.getParameter("start_time");
                String endTimeStr = request.getParameter("end_time");

                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                dto.setStart_time(new Timestamp(dateTimeFormat.parse(dateStr + " " + startTimeStr).getTime()));
                dto.setEnd_time(new Timestamp(dateTimeFormat.parse(dateStr + " " + endTimeStr).getTime()));

                dto.setWorker_id(request.getParameter("worker_id"));
                dto.setCp_id(request.getParameter("lot_id"));
                dto.setBd_reason(request.getParameter("bd_reason"));

                if ("update".equals(action)) {
                    service.updateInspection(dto);
                } else {
                    service.createInspection(dto);
                }

                response.sendRedirect(request.getContextPath() + "/inspection");

            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("Error during form submission: " + e.getMessage());
            }
        }
    }
}