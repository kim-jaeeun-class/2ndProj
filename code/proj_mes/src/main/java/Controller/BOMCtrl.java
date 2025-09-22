package Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.BOMDTO;
import Service.BOMService;

@WebServlet("/bom")
public class BOMCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		BOMService service = new BOMService();
		String action = request.getParameter("action");
		
		if("edit".equals(action)) {
		    String bomID = request.getParameter("bomID");
		    BOMDTO dto = service.getBOMByID(bomID);

		    request.setAttribute("edit", dto);
		    request.getRequestDispatcher("/Html/06_bom/06_bom_edit.jsp").forward(request, response);
		    return;
		}
		
		// 전체 조회용 서블릿
		List<BOMDTO> list = service.getAllBOM();
		
		request.setAttribute("list", list);
		request.getRequestDispatcher("/Html/06_bom/06_bom_list.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO 애초에 내 페이지랑 다르게 작성, 수정이 다른 페이지에 있으니까 다시 한 번 점검해보기
		// 한글 깨짐 방지
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		BOMService service = new BOMService();
		String action = request.getParameter("action");		// form 아래 hidden에서 "add", "delete", "search", "edit" 구분용
		
		if("add".equals(action)) {
			// 등록 영역 - 완
		    BOMDTO dto = new BOMDTO();
		    dto.setItem_code_1(request.getParameter("item-code-1"));
		    dto.setItem_code_2(request.getParameter("item-code-2"));
		    dto.setRequire_amount(Integer.parseInt(request.getParameter("require-amount")));

		    int result = service.addBOM(dto);
		    if(result > 0) {
		        request.setAttribute("msg", "등록 성공");
		    } 
		    else {
		        request.setAttribute("msg", "등록 실패");
		    }
		}
		else if("delete".equals(action)) {
		    String[] ids = request.getParameterValues("chk");
		    int delCount = 0;
		    if(ids != null) {
		        for(String id : ids) {
		            BOMDTO dto = new BOMDTO();
		            dto.setBomID(id); 
		            delCount += service.removeBOM(dto);
		            System.out.println("일단 null은 아님");
		        }
		    }
		    request.setAttribute("msg", delCount + "건 삭제");
		}

		else if("search".equals(action)) {
			// 필터링 검색 영역
			String item1 = request.getParameter("filter-item1");
			BOMDTO dto = new BOMDTO();
			// 아래 코드 생각 좀 해보기
			// TODO 필터링 name 설정할 때 잊지 말기!!! 이렇게 해놨으므로 jsp에도 그대로 적용
			dto.setItem_code_1(item1);
			System.out.println(item1);
			List<BOMDTO> filtered;
			// 필터 입력 null 아니고 안 비어있으면 bomID로 서치 가능하게 세팅
			if(item1 != null && !item1.isEmpty() && !"all".equals(item1)) {
				dto.setItem_code_1(item1); 
				filtered = service.getFilBOM(dto);
			}
			else if("all".equals(item1)) {
				// 비어 있으면 전체로 나오라고 하기
				filtered = service.getAllBOM();
			}
			else {
				filtered = service.getAllBOM();
			}
			
			request.setAttribute("list", filtered);
			request.getRequestDispatcher("/Html/06_bom/06_bom_list.jsp").forward(request, response);
			return;
			
		}
		else if("edit".equals(action)) {
		    BOMDTO dto = new BOMDTO();
		    dto.setBomID(request.getParameter("bomID"));
		    dto.setItem_code_1(request.getParameter("item-code-1"));
		    dto.setItem_code_2(request.getParameter("item-code-2"));
		    dto.setRequire_amount(Integer.parseInt(request.getParameter("require-amount")));
		    
		    int result = service.editBOM(dto);
		    if(result > 0) {
		        request.setAttribute("msg", "수정 성공");
		    } else {
		        request.setAttribute("msg", "수정 실패");
		    }
		}
		
		// 작업 후 항상 최신 리스트 출력되도록
		List<BOMDTO> list = service.getAllBOM();
		request.setAttribute("list", list);
		request.getRequestDispatcher("/Html/06_bom/06_bom_list.jsp").forward(request, response);

	}

}
