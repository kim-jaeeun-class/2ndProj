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

        // Ajax 요청: 품목 코드에 따른 공정 정보 반환
        if ("getProcessByItemCode".equals(action)) {
            String itemCode = request.getParameter("itemCode");
            ProcessDTO process = processService.getProcessByItemCode(itemCode);

            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");

            if (process != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(process.getProc_seq()).append("|");
                sb.append(process.getItem_code()).append("|");
                sb.append(process.getDepart_level()).append("|");
                sb.append(process.getProc_name()).append("|");
                sb.append(process.getProc_info()).append("|");
                sb.append(process.getProc_img() != null ? process.getProc_img() : "");
                response.getWriter().write(sb.toString());
            } else {
                response.getWriter().write("");
            }
            return;
        }

        // CUD 페이지 로직
        if ("new".equals(mode) || "update".equals(mode)) {
            request.setAttribute("mode", mode);
            if ("update".equals(mode) && procId != null) {
                ProcessDTO process = processService.getProcessById(procId);
                String departLevel = request.getParameter("departLevel");
                if (process != null) {
                    process.setDepart_level(departLevel);
                }
                request.setAttribute("process", process);
            }
            
            // 이 두 리스트는 CUD 페이지의 select 박스에서 필요합니다.
            request.setAttribute("itemCodes", processService.getUniqueItemCodes());
            request.setAttribute("departLevels", processService.getUniqueDepartLevels());
            
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
        
        List<String> departLevels = (itemCode != null && !itemCode.isEmpty()) ?
                processService.getDepartLevelsByItemCode(itemCode) :
                processService.getUniqueDepartLevels();
        request.setAttribute("departLevels", departLevels);
        request.setAttribute("selectedDepart", departLevel);

        List<String> procNames = (itemCode != null && !itemCode.isEmpty() && departLevel != null && !departLevel.isEmpty()) ?
                processService.getProcNamesByItemAndDepart(itemCode, departLevel) :
                (departLevel != null && !departLevel.isEmpty()) ?
                        processService.getUniqueProcNamesByDepart(departLevel) :
                        processService.getUniqueProcNames();
        request.setAttribute("procNames", procNames);
        request.setAttribute("selectedProcName", procName);

        List<ProcessDTO> processes = processService.getProcessesBySearch(itemCode, departLevel, procName);
        request.setAttribute("processes", processes);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/Html/05_process_gongjeong/05_process_list.jsp");
        dispatcher.forward(request, response);
    }
    
    // POST 요청 처리: 등록/수정/삭제 + 파일 업로드
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String action = null;
        ProcessDTO dto = new ProcessDTO();
        String procIdToUpdate = null;
        String filePath = null;

        // multipart/form-data 요청 처리
        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = upload.parseRequest(request);
                
                Map<String, String> formFields = new HashMap<>();

                for (FileItem item : items) {
                    if (item.isFormField()) {
                        formFields.put(item.getFieldName(), item.getString("utf-8"));
                    } else {
                        if (item.getName() != null && !item.getName().isEmpty()) {
                            String fileName = new File(item.getName()).getName();
                            filePath = "uploads/" + fileName;
                            File uploadedFile = new File(getServletContext().getRealPath(filePath));
                            item.write(uploadedFile);
                        }
                    }
                }

                action = formFields.get("action");
                procIdToUpdate = formFields.get("procId");
                
                dto.setProc_id(formFields.get("procId"));
                dto.setProc_name(formFields.get("procName"));
                
                // 문자열을 숫자로 변환
                if (formFields.get("procSeq") != null) {
                    try {
                        dto.setProc_seq(Integer.parseInt(formFields.get("procSeq")));
                    } catch (NumberFormatException e) {
                        dto.setProc_seq(0); // 유효하지 않은 값 처리
                    }
                }
                
                dto.setDepart_level(formFields.get("departLevel"));
                dto.setProc_info(formFields.get("procInfo"));
                dto.setItem_code(formFields.get("itemCode"));

                // 이미지 삭제 플래그 처리
                boolean deleteImageFlag = "true".equals(formFields.get("deleteImage"));

                // 업데이트 로직 개선
                if ("update".equals(action)) {
                    // 기존 데이터를 먼저 가져옴
                    ProcessDTO existingDto = processService.getProcessById(dto.getProc_id());
                    if (existingDto != null) {
                        // 기존 DTO에 새로운 값들을 덮어씌움
                        existingDto.setProc_name(dto.getProc_name());
                        existingDto.setProc_seq(dto.getProc_seq());
                        existingDto.setDepart_level(dto.getDepart_level());
                        existingDto.setProc_info(dto.getProc_info());
                        existingDto.setItem_code(dto.getItem_code());
                        
                        if (deleteImageFlag) {
                            existingDto.setProc_img(null); // 이미지 삭제 요청 시
                        } else if (filePath != null) {
                            existingDto.setProc_img(filePath); // 새 이미지 업로드 시
                        }
                        dto = existingDto;
                    }
                } else if ("create".equals(action)) {
                    dto.setProc_img(filePath);
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
        } else {
            // 일반 HTTP 요청 처리 (예: 삭제 버튼)
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