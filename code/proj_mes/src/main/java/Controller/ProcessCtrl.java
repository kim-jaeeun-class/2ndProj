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

@WebServlet("/process")
public class ProcessCtrl extends HttpServlet {
    private ProcessService processService = new ProcessService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

     // CUD 페이지 로직
        // new 모드
        if ("new".equals(mode)) {
            request.setAttribute("mode", mode);
            request.setAttribute("itemCodes", processService.getAllItemCodes());

            // 부서 목록 추가
            request.setAttribute("departLevels", processService.getUniqueDepartLevels());

            // 공정명 목록 (proc 테이블에서 가져오기)
            request.setAttribute("procNames", processService.getUniqueProcNamesFromProcTable());

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
                
                // 1. 해당 품목 코드에 대한 부서 목록을 조회하여 JSP로 전달
                List<String> departLevels = processService.getDepartLevelsByItemCode(process.getItem_code());
                request.setAttribute("departLevels", departLevels);
                
                // 2. 해당 품목 코드와 부서에 해당하는 공정 목록을 조회하여 JSP로 전달
                List<ProcessDTO> procNames = processService.getProcNamesByItemAndDepart(process.getItem_code(), process.getDepart_level());
                request.setAttribute("procNames", procNames);

            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_CUD.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        // 목록 페이지 로직
        String itemCode = request.getParameter("itemCode");
        String departLevel = request.getParameter("departLevel");
        String procName = request.getParameter("procName");

        request.setAttribute("itemCodes", processService.getUniqueItemCodes());
        request.setAttribute("selectedItemCode", itemCode);
        
        // departLevel 목록 세팅
        List<String> departLevels = (itemCode != null && !itemCode.isEmpty()) ?
                processService.getDepartLevelsByItemCode(itemCode) :
                processService.getUniqueDepartLevels();
        request.setAttribute("departLevels", departLevels);
        request.setAttribute("selectedDepart", departLevel);

        // procNames 목록 세팅 (depart_level 조건에 맞는 공정명만 가져오기)
        List<ProcessDTO> procList = processService.getProcNamesByItemAndDepart(itemCode, departLevel);
        List<String> procNames = new ArrayList<>();
        for (ProcessDTO dto : procList) {
            procNames.add(dto.getDepart_level() + " - " + dto.getProc_name());
        }
        request.setAttribute("procNames", procNames);
        request.setAttribute("selectedProcName", procName);

        // 검색 조건에 따른 전체 process 리스트
        List<ProcessDTO> processes = processService.getProcessesBySearch(itemCode, departLevel, procName);
        request.setAttribute("processes", processes);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_list.jsp");
        dispatcher.forward(request, response);
    }
    
    // POST 요청 처리
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String action = null;
        ProcessDTO dto = new ProcessDTO();
        
        // 파일 업로드 요청인 경우 (multipart/form-data)
        if (ServletFileUpload.isMultipartContent(request)) {
        	String filePath = null;
        	
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = upload.parseRequest(request);

                Map<String, String> formFields = new HashMap<>();
                String fileName = null;
                
                for (FileItem item : items) {
                    if (item.isFormField()) {
                        formFields.put(item.getFieldName(), item.getString("utf-8"));
                    } else {
                        if (item.getName() != null && !item.getName().isEmpty()) {
                            fileName = new File(item.getName()).getName();
                            filePath = "uploads/" + fileName;
                            File uploadedFile = new File(getServletContext().getRealPath(filePath));
                            
                            // 기존 파일이 있다면 삭제
                            if (uploadedFile.exists()) {
                                uploadedFile.delete();
                            }
                            
                            item.write(uploadedFile);
                        }
                    }
                }

                action = formFields.get("action");
                
                // DTO에 값 설정 (널 체크 및 변환 포함)
                dto.setProc_id(formFields.get("procId"));
                
                String procName = formFields.get("procName");
                dto.setProc_name(procName); 
                
                if (procName != null && (procName.contains("검사") || procName.contains("관리"))) {
                    dto.setProcess_check(1);
                } else {
                    dto.setProcess_check(0);
                }

                String procSeqStr = formFields.get("procSeq");
                if (procSeqStr != null && !procSeqStr.isEmpty()) {
                    try {
                        dto.setProc_seq(Integer.parseInt(procSeqStr));
                    } catch (NumberFormatException e) {
                        dto.setProc_seq(0);
                    }
                } else {
                    dto.setProc_seq(0);
                }
                
                String departLevel = formFields.get("departLevel");
                String departId = processService.getDepartIdByLevel(departLevel);

                dto.setDapart_id2(departId);
                dto.setProc_img(filePath);
                dto.setProc_info(formFields.get("procInfo"));
                dto.setItem_code(formFields.get("itemCode"));
                
                boolean deleteImageFlag = "true".equals(formFields.get("deleteImage"));
                if (deleteImageFlag) {
                    dto.setProc_img(null);
                }
                
            } catch (FileUploadException e) {
                e.printStackTrace();
                response.getWriter().println("<script>alert('파일 업로드 실패'); history.back();</script>");
                return;
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().println("<script>alert('데이터 처리 실패'); history.back();</script>");
                return;
            }

        } else { // 일반 폼 요청 (e.g., DELETE)
            action = request.getParameter("action");
            if ("delete".equals(action)) {
                dto.setProc_id(request.getParameter("procId"));
            }
        }

        boolean success = false;
        if (action != null) {
            switch (action) {
                case "create":
                    success = processService.createProcess(dto);
                    break;
                case "update":
                    success = processService.updateProcess(dto);
                    break;
                case "delete":
                    success = processService.deleteProcess(dto.getProc_id());
                    break;
            }
        }

        if (success) {
            response.sendRedirect(request.getContextPath() + "/process");
        } else {
            response.getWriter().println("<script>alert('작업 실패'); history.back();</script>");
        }
    }
}