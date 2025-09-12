package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.StockDTO;
import Service.StockService;

// (선택) 품목 모달용
import java.util.List;
import Dto.ItemDTO;
import Service.ItemService;

@WebServlet("/stockAdd")
public class StockAddCtrl extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    // 등록 폼 모드
    req.setAttribute("mode", "add");

    // 품목 모달용 목록 (있으면 사용, 실패해도 화면은 뜨도록 무시)
    try {
      List<ItemDTO> itemList = new ItemService().getAllItem();
      req.setAttribute("itemList", itemList);
    } catch (Throwable ignore) {}

    // 등록 폼 JSP
    req.getRequestDispatcher("/08_stockRegistration.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    // 파라미터 수집 (DTO 필드명과 1:1)
    String item_code    = trim(req.getParameter("item_code"));
    int stock_number    = parseInt(req.getParameter("stock_number"), 0);
    int stock_loc       = parseInt(req.getParameter("stock_loc"), 0);
    int stock_div       = parseInt(req.getParameter("stock_div"), 0);
    int stock_stat      = parseInt(req.getParameter("stock_stat"), 0);

    // DTO 구성
    StockDTO dto = new StockDTO();
    dto.setItem_code(item_code);
    dto.setStock_number(stock_number);
    dto.setStock_loc(stock_loc);
    dto.setStock_div(stock_div);
    dto.setStock_stat(stock_stat);
    // stock_id / stock_date 는 DAO에서 생성/기록

    try {
      // 저장 (DAO가 stock_id = item_code + 4자리 자동 생성)
      new StockService().addStock(dto);

      // 성공 → 목록으로
      resp.sendRedirect(req.getContextPath() + "/stockList");

    } catch (IllegalArgumentException iae) {
      // 필수값 검증 실패 → 폼 다시 표시 (기존 입력 유지)
      req.setAttribute("mode", "add");
      req.setAttribute("error", iae.getMessage());
      // 품목 모달 목록 재바인딩
      try {
        req.setAttribute("itemList", new ItemService().getAllItem());
      } catch (Throwable ignore) {}
      req.getRequestDispatcher("/08_stockRegistration.jsp").forward(req, resp);

    } catch (RuntimeException re) {
      // Service/DAO 래핑 예외
      throw new ServletException("재고 등록 실패: " + re.getMessage(), re);
    }
  }

  // ===== helpers =====
  private static String trim(String s){ return s==null? null: s.trim(); }
  private static int parseInt(String s, int def){
    if (s == null || s.isEmpty()) return def;
    try { return Integer.parseInt(s.replaceAll("[^0-9-]","")); }
    catch(Exception e){ return def; }
  }
}
