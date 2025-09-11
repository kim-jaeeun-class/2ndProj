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
import java.util.List;
import java.util.ArrayList;

@WebServlet("/process")
public class ProcessCtrl extends HttpServlet {
    private ProcessService processService = new ProcessService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("/process doGet 실행");
		
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String mode = request.getParameter("mode");
        String procId = request.getParameter("procId");
        String action = request.getParameter("action");
        
        // Ajax 요청 처리: 품목 코드에 따른 공정 정보 반환
        if ("getProcessByItemCode".equals(action)) {
            String itemCode = request.getParameter("itemCode");
            ProcessDTO process = processService.getProcessByItemCode(itemCode); // 해당 품목의 공정 정보를 가져오는 새로운 서비스 메소드
            
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            
            if (process != null) {
                // 데이터를 파이프(|)로 구분하여 문자열로 만듭니다.
                StringBuilder sb = new StringBuilder();
                sb.append(process.getProc_id()).append("|");
                sb.append(process.getItem_code()).append("|");
                sb.append(process.getDepart_level()).append("|");
                sb.append(process.getProc_name()).append("|");
                sb.append(process.getProc_info()).append("|");
                sb.append(process.getProc_img() != null ? process.getProc_img() : "");
                response.getWriter().write(sb.toString());
            } else {
                // 데이터가 없을 경우 빈 문자열 반환
                response.getWriter().write("");
            }
            return; // Ajax 요청 처리 후 메소드 종료
        }
        
        // CUD 페이지의 드롭다운을 위한 데이터 미리 로드
        List<String> uniqueItemCodes = processService.getUniqueItemCodes();
        List<String> uniqueDepartLevels = processService.getUniqueDepartLevels();
        List<String> uniqueProcNames = processService.getUniqueProcNames();
        
        // 1. CUD 페이지 로직 분기 (신규/수정)
        if ("new".equals(mode) || "update".equals(mode)) {
            request.setAttribute("mode", mode);
            
            if ("update".equals(mode) && procId != null) {
                ProcessDTO process = processService.getProcessById(procId);
                request.setAttribute("process", process);
            }
            
            request.setAttribute("itemCodes", uniqueItemCodes);
            request.setAttribute("departLevels", uniqueDepartLevels);
            request.setAttribute("procNames", uniqueProcNames);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_CUD.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // 2. 목록 페이지 로직 분기 (기본값)
        String itemCode = request.getParameter("itemCode");
        String departLevel = request.getParameter("departLevel");
        String procName = request.getParameter("procName");

        request.setAttribute("itemCodes", uniqueItemCodes);
        request.setAttribute("selectedItemCode", itemCode);
        
        List<String> departLevels = new ArrayList<>();
        if (itemCode != null && !itemCode.isEmpty()) {
            departLevels = processService.getDepartLevelsByItemCode(itemCode);
        } else {
            departLevels = uniqueDepartLevels;
        }
        request.setAttribute("departLevels", departLevels);
        request.setAttribute("selectedDepart", departLevel);

        List<String> procNames = new ArrayList<>();
        if (itemCode != null && !itemCode.isEmpty() && departLevel != null && !departLevel.isEmpty()) {
            procNames = processService.getProcNamesByItemAndDepart(itemCode, departLevel);
        } else if (departLevel != null && !departLevel.isEmpty()) {
            procNames = processService.getUniqueProcNamesByDepart(departLevel);
        } else {
            procNames = uniqueProcNames;
        }
        request.setAttribute("procNames", procNames);
        request.setAttribute("selectedProcName", procName);

        List<ProcessDTO> processes = processService.getProcessesBySearch(itemCode, departLevel, procName);
        request.setAttribute("processes", processes);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_list.jsp");
        dispatcher.forward(request, response);
    }
    
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("/process doPost 실행");

        // 한글 깨짐 방지
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String action = null;            // CUD 구분
        String filePath = null;          // 업로드 이미지 경로
        ProcessDTO dto = new ProcessDTO(); // 공정 DTO 객체 생성
        boolean deleteImageFlag = false; // 이미지 삭제 여부

        // 1️⃣ multipart/form-data 요청인지 확인
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = upload.parseRequest(request);

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        // 2️⃣ 일반 폼 필드 처리
                        switch (item.getFieldName()) {
                            case "action":        // create/update/delete
                                action = item.getString("utf-8");
                                break;
                            case "procId":        // 공정 순서
                                dto.setProc_id(item.getString("utf-8"));
                                break;
                            case "procName":      // 공정 이름
                                dto.setProc_name(item.getString("utf-8"));
                                break;
                            case "departLevel":   // 부서
                                dto.setDepart_level(item.getString("utf-8"));
                                break;
                            case "procInfo":      // 공정 설명
                                dto.setProc_info(item.getString("utf-8"));
                                break;
                            case "itemCode":      // 품목 코드
                                dto.setItem_code(item.getString("utf-8"));
                                break;
                            case "deleteImage":   // 이미지 삭제 여부
                                deleteImageFlag = "true".equals(item.getString("utf-8"));
                                break;
                        }
                    } else {
                        // 3️⃣ 파일 필드 처리
                        if (item.getName() != null && !item.getName().isEmpty()) {
                            String fileName = new File(item.getName()).getName();
                            filePath = "uploads/" + fileName; // 웹앱 기준 저장 경로
                            File uploadedFile = new File(getServletContext().getRealPath(filePath));
                            item.write(uploadedFile);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().println("<script>alert('파일 업로드 실패'); history.back();</script>");
                return;
            }
        } else {
            // multipart/form-data가 아닐 경우 (삭제 버튼 등)
            action = request.getParameter("action");
            dto.setProc_id(request.getParameter("procId"));
        }

        boolean success = false;

        // 4️⃣ CUD 처리
        if (action != null) {
            switch (action) {
                case "create":  // 4-1. 등록
                    dto.setProc_img(filePath);
                    success = processService.createProcess(dto);
                    break;
                case "update":  // 4-2. 수정
                    if (deleteImageFlag) {
                        dto.setProc_img(null); // 이미지 삭제 처리
                    } else if (filePath != null) {
                        dto.setProc_img(filePath); // 새로운 이미지 교체
                    }
                    success = processService.updateProcess(dto);
                    break;
                case "delete":  // 4-3. 삭제
                    success = processService.deleteProcess(dto.getProc_id());
                    break;
            }
        }

        // 5️⃣ PRG 패턴: 결과에 따라 리다이렉트
        if (success) {
            response.sendRedirect(request.getContextPath() + "/process");
        } else {
            response.getWriter().println("<script>alert('작업 실패'); history.back();</script>");
        }
    }
    
    // HttpServletRequest에서 공정 정보를 추출하여 ProcessDTO 객체를 생성
    private ProcessDTO createProcessDTOFromRequest(HttpServletRequest request) {
        ProcessDTO dto = new ProcessDTO();
        dto.setProc_id(request.getParameter("procId"));
        dto.setProc_name(request.getParameter("procName"));
        dto.setDapart_id2(request.getParameter("departLevel")); // departLevel -> dapart_id2 매핑 필요
        dto.setProc_info(request.getParameter("procInfo"));
        dto.setItem_code(request.getParameter("itemCode"));
        return dto;
    }
}

