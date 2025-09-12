package Controller;

import Dto.ProcessDTO;
import Service.ProcessService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/process")
public class ProcessCtrl extends HttpServlet {
    private ProcessService processService = new ProcessService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("/process의 doGet 실행");
    	
        // 한글 깨짐 방지
    	request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String action = request.getParameter("action");
        String mode = request.getParameter("mode");
        String procId = request.getParameter("procId");

        // AJAX 요청 처리
        if ("getDepartLevelsByItemCode".equals(action)) {
            String itemCode = request.getParameter("itemCode");
            List<String> departLevels = processService.getDepartLevelsByItemCode(itemCode);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.join(",", departLevels));
            return;
        }

        if ("getProcNamesByItemAndDepart".equals(action)) {
            String itemCodeParam = request.getParameter("itemCode");
            String departLevelParam = request.getParameter("departLevel");
            List<ProcessDTO> procList = processService.getProcNamesByItemAndDepart(itemCodeParam, departLevelParam);
            List<String> procNames = new ArrayList<>();
            for (ProcessDTO dto : procList) {
                procNames.add(dto.getDepart_level() + " - " + dto.getProc_name());
            }
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.join(",", procNames));
            return;
        }

        // new 모드
        if ("new".equals(mode)) {
            request.setAttribute("mode", mode);
            request.setAttribute("itemCodes", processService.getAllItemCodes());
            request.setAttribute("departLevels", processService.getUniqueDepartLevels());
            request.setAttribute("procNames", processService.getUniqueProcNames());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_CUD.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // update 모드
        if ("update".equals(mode) && procId != null) {
            request.setAttribute("mode", mode);
            ProcessDTO process = processService.getProcessById(procId);
            if (process != null) {
                request.setAttribute("process", process);
                request.setAttribute("itemCodes", processService.getAllItemCodes());
                request.setAttribute("departLevels", processService.getDepartLevelsByItemCode(process.getItem_code()));
                List<ProcessDTO> procList = processService.getProcNamesByItemAndDepart(process.getItem_code(), process.getDepart_level());
                List<String> procNames = new ArrayList<>();
                for (ProcessDTO dto : procList) procNames.add(dto.getProc_name());
                request.setAttribute("procNames", procNames);
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_CUD.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // 목록 페이지
        String itemCode = request.getParameter("itemCode");
        String departLevel = request.getParameter("departLevel");
        String procName = request.getParameter("procName");

        itemCode = (itemCode == null || itemCode.isEmpty()) ? null : itemCode;
        departLevel = (departLevel == null || departLevel.isEmpty()) ? null : departLevel;
        procName = (procName == null || procName.isEmpty()) ? null : procName;

        request.setAttribute("itemCodes", processService.getUniqueItemCodes());
        request.setAttribute("selectedItemCode", itemCode);
        List<String> departLevels = (itemCode != null) ? processService.getDepartLevelsByItemCode(itemCode) : processService.getUniqueDepartLevels();
        request.setAttribute("departLevels", departLevels);
        request.setAttribute("selectedDepart", departLevel);

        List<ProcessDTO> procList = processService.getProcNamesByItemAndDepart(itemCode, departLevel);
        List<String> procNames = new ArrayList<>();
        for (ProcessDTO dto : procList) procNames.add(dto.getProc_name());
        request.setAttribute("procNames", procNames);
        request.setAttribute("selectedProcName", procName);

        List<ProcessDTO> processes = processService.getProcessesBySearch(itemCode, departLevel, procName);
        request.setAttribute("processes", processes);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_list.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String action = null;
        ProcessDTO dto = new ProcessDTO();

        try {
            if (ServletFileUpload.isMultipartContent(request)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = upload.parseRequest(request);

                Map<String, String> formFields = new HashMap<>();
                String filePath = null;

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        formFields.put(item.getFieldName(), item.getString("utf-8"));
                    } else {
                        if (item.getName() != null && !item.getName().isEmpty()) {
                            String fileName = new File(item.getName()).getName();
                            filePath = "uploads/" + fileName;
                            File uploadedFile = new File(getServletContext().getRealPath(filePath));
                            if (uploadedFile.exists()) uploadedFile.delete();
                            item.write(uploadedFile);
                        }
                    }
                }

                action = formFields.get("action");

                // procId 처리 (신규 등록 시 자동 생성)
                String procId = formFields.get("procId");
                if ("create".equals(action) && (procId == null || procId.trim().isEmpty())) {
                    procId = UUID.randomUUID().toString();
                }
                dto.setProc_id(procId);

                // 필수 값 체크
                String procName = formFields.get("procName");
                if (procName == null || procName.trim().isEmpty()) {
                    response.getWriter().println("<script>alert('공정을 선택해주세요.'); history.back();</script>");
                    return;
                }
                dto.setProc_name(procName);
                dto.setProcess_check(procName.contains("검사") || procName.contains("관리") ? 1 : 0);

                String procSeqStr = formFields.get("procSeq");
                try {
                    dto.setProc_seq(procSeqStr != null && !procSeqStr.isEmpty() ? Integer.parseInt(procSeqStr) : 0);
                } catch (NumberFormatException e) {
                    dto.setProc_seq(0);
                }

                String departLevel = formFields.get("departLevel");
                if (departLevel != null && !departLevel.isEmpty()) {
                    dto.setDapart_id2(processService.getDepartIdByLevel(departLevel));
                } else {
                    dto.setDapart_id2(null);
                }

                dto.setProc_img(filePath);
                dto.setProc_info(formFields.get("procInfo"));
                dto.setItem_code(formFields.get("itemCode"));

                boolean deleteImageFlag = "true".equals(formFields.get("deleteImage"));
                if (deleteImageFlag) dto.setProc_img(null);

            } else { // 일반 폼 (DELETE)
                action = request.getParameter("action");
                if ("delete".equals(action)) {
                    dto.setProc_id(request.getParameter("procId"));
                }
            }

            boolean success = false;
            if (action != null) {
                switch (action) {
                    case "create": success = processService.createProcess(dto); break;
                    case "update": success = processService.updateProcess(dto); break;
                    case "delete": success = processService.deleteProcess(dto.getProc_id()); break;
                }
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/process");
            } else {
                response.getWriter().println("<script>alert('작업 실패'); history.back();</script>");
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('파일 업로드 실패'); history.back();</script>");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('데이터 처리 실패'); history.back();</script>");
        }
    }
}