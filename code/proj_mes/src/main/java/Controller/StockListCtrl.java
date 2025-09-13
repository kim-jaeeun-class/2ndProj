package Controller;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.StockDTO;
import Service.StockService;

@WebServlet("/stockList")
public class StockListCtrl extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    String big   = trim(req.getParameter("big"));        // ex) RM / FP / CM / HP
    String mid   = trim(req.getParameter("mid"));        // ex) GL / CH / CT ...
    String small = trim(req.getParameter("small"));      // ex) LC / OL / AM ...
    String sFrom = trim(req.getParameter("startDate"));  // yyyy-MM-dd
    String sTo   = trim(req.getParameter("endDate"));

    Date from = parseYmd(sFrom);
    Date to   = parseYmd(sTo);

    try {
      StockService svc = new StockService();

      // 목록: 필터 없으면 전체, 있으면 검색
      List<StockDTO> list;
      
      if (isEmpty(big) && isEmpty(mid) && isEmpty(small) && from == null && to == null) {
        list = svc.getAllStock();
      } else {
        list = svc.searchStocks(big, mid, small, from, to);
      }
      req.setAttribute("stockList", list);

      // 드롭다운 데이터
      req.setAttribute("bigList",   svc.getBigList());
      req.setAttribute("midList",   isEmpty(big) ? Collections.emptyList() : svc.getMidListByBig(big));
      req.setAttribute("smallList", isEmpty(mid) ? Collections.emptyList() : svc.getSmallListByMid(mid));

      // 선택값 유지
      req.setAttribute("selBig", big);
      req.setAttribute("selMid", mid);
      req.setAttribute("selSmall", small);
      req.setAttribute("startDate", sFrom);
      req.setAttribute("endDate",   sTo);

      req.getRequestDispatcher("/08_stockList.jsp").forward(req, resp);

    } catch (Exception e) {
      e.printStackTrace();
      req.setAttribute("error", "재고 목록 조회 실패: " + e.getMessage());
      req.setAttribute("stockList", Collections.emptyList());
      req.setAttribute("bigList", Collections.emptyList());
      req.setAttribute("midList", Collections.emptyList());
      req.setAttribute("smallList", Collections.emptyList());
      
      req.getRequestDispatcher("/08_stockList.jsp").forward(req, resp);
    }
  }

  private static String trim(String s){ return s==null? null: s.trim(); }
  private static boolean isEmpty(String s){ return s==null || s.trim().isEmpty(); }
  private static Date parseYmd(String s){
    if (s == null || s.isEmpty()) return null;
    try {
      java.util.Date d = new SimpleDateFormat("yyyy-MM-dd").parse(s);
      return new Date(d.getTime());
    } catch (Exception e) { return null; }
  }
}
