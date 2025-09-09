package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/StandardCtrl")
public class StandardCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public StandardCtrl() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//자 일단 체이지를 불러와
		//그 다음 select에 들어가는 목록들중 선택된 값을 떙겨와
		//그 다음 db에서 불러와, 그전에 select내의 옵션이 어떤 테이블인지 살펴봐 
		//인덱싱 붙히고 출력
        request.getRequestDispatcher("Html/04_standard_gijun/04_standard_list.jsp")
        .forward(request, response);
		
		
		
		//등록하기 버튼을 누르면 다음 창으로 넘어가게 해
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
