package Controller;

import Dto.ProcessDTO;
import Service.ProcessService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/process")
public class ProcessController extends HttpServlet {
    private ProcessService processService = new ProcessService();

    // GET 요청을 처리하는 메서드
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String departLevel = request.getParameter("departLevel"); // 부서 선택 드롭다운에서 전달된 값
        
        // 1. 모든 부서 목록을 가져와 request에 저장 (초기 드롭다운을 채우기 위함)
        List<String> departLevels = processService.getUniqueDepartLevels();
        request.setAttribute("departLevels", departLevels);

        // 2. 부서가 선택되었으면, 해당 부서의 공정 목록을 가져옴
        if (departLevel != null && !departLevel.isEmpty()) {
            List<ProcessDTO> processes = processService.getProcessesByDepartLevel(departLevel);
            request.setAttribute("processes", processes);
            request.setAttribute("selectedDepart", departLevel); // 선택된 부서 값을 JSP에 전달
        }
        
        // 3. JSP 페이지로 포워딩하여 결과를 렌더링
        RequestDispatcher dispatcher = request.getRequestDispatcher("05_process_list.jsp");
        dispatcher.forward(request, response);
    }
}