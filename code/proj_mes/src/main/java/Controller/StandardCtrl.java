package Controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Service.StandardService;
import Service.StandardService.TableResult;

@WebServlet("/StandardCtrl")
public class StandardCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private StandardService standardService;

    public StandardCtrl() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
        super.init();
        // 통합 서비스 1회 초기화
        standardService = new StandardService();
//        standardService.register("공정", new StandardService.ProcProvider());
        // 필요 시 여기서 표준 외 Provider 추가 등록 가능:
        // standardService.register("공정", new StandardService.ProcessProvider(...));
        // standardService.register("BOM",  new StandardService.BomProvider(...));
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		//자 일단 체이지를 불러와
		//그 다음 select에 들어가는 목록들중 선택된 값을 떙겨와
		//그 다음 db에서 불러와, 그전에 select내의 옵션이 어떤 테이블인지 살펴봐 
		
        String category = request.getParameter("category");
        if (category == null) category = "";
        
        // 선택된 카테고리에 맞춰 컬럼/데이터 구성
        TableResult tr = standardService.buildTable(category);
        request.setAttribute("category", tr.getCategory());
        request.setAttribute("columns",  tr.getColumns());
        request.setAttribute("data",     tr.getData());


        request.getRequestDispatcher("Html/04_standard_gijun/04_standard_list.jsp")
        .forward(request, response);
        
        //다른 select 마다의 데이러를 가져오는 파일을 활용하자
        
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
