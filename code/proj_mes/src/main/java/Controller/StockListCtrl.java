package Controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.StockDTO;
import Service.StockService;

@WebServlet("/stockList")
public class StockListCtrl extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    try {
      List<StockDTO> list = new StockService().getAllStock();
      req.setAttribute("stockList", list);
      req.getRequestDispatcher("/08_stockList.jsp").forward(req, resp);
    } catch (RuntimeException e) {
      // 에러 시 간단히 메시지 출력(원하면 전용 에러 페이지로 포워딩)
      req.setAttribute("error", "재고 목록 조회 실패: " + e.getMessage());
      req.setAttribute("stockList", java.util.Collections.emptyList());
      req.getRequestDispatcher("/08_stockList.jsp").forward(req, resp);
    }
  }
}
