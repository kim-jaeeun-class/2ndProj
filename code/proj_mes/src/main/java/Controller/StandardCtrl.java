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

    public StandardCtrl() { super(); }

    @Override
    public void init() throws ServletException {
        super.init();
        standardService = new StandardService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String category = request.getParameter("category");
        if (category == null || category.trim().isEmpty()) {
            // ★ 처음 진입: 자동으로 '전체'로 간주하여 전체 요약 테이블 제공
            category = "전체";
        }

        if ("전체".equals(category)) {
            TableResult tr = standardService.buildSummaryAll();
            request.setAttribute("category", "전체"); // 셀렉트 유지
            request.setAttribute("columns",  tr.getColumns());
            request.setAttribute("data",     tr.getData());
        } else {
            TableResult tr = standardService.buildTable(category);
            request.setAttribute("category", tr.getCategory());
            request.setAttribute("columns",  tr.getColumns());
            request.setAttribute("data",     tr.getData());
        }

        request.getRequestDispatcher("Html/04_standard_gijun/04_standard_list.jsp")
               .forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
