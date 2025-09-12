package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.StockDTO;
import Service.StockService;

// (선택) 품목 모달 재표시용
import java.util.List;
import Dto.ItemDTO;
import Service.ItemService;

@WebServlet("/stockUpdate")
public class StockUpdateCtrl extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    // 파라미터 수집 (DTO 필드와 1:1)
    String stock_id     = trim(req.getParameter("stock_id"));   // hidden
    String item_code    = trim(req.getParameter("item_code"));
    int stock_number    = parseInt(req.getParameter("stock_number"), 0);
    int stock_loc       = parseInt(req.getParameter("stock_loc"), 0);
    int stock_div       = parseInt(req.getParameter("stock_div"), 0);
    int stock_stat      = parseInt(req.getParameter("stock_stat"), 0);

    // DTO 구성
    StockDTO dto = new StockDTO();
    dto.setStock_id(stock_id);
    dto.setItem_code(item_code);
    dto.setStock_number(stock_number);
    dto.setStock_loc(stock_loc);
    dto.setStock_div(stock_div);
    dto.setStock_stat(stock_stat);

    try {
      int updated = new StockService().editStock(dto);
      if (updated <= 0) {
        // 수정 대상 없음 → 다시 edit 화면
        forwardBackToEdit(req, resp, dto, "수정 대상이 없습니다.");
        return;
      }
      // 성공 → 상세(view)로
      resp.sendRedirect(req.getContextPath() + "/stockDetail?id=" + enc(stock_id) + "&mode=view");

    } catch (IllegalArgumentException iae) {
      // 검증 실패 → 다시 edit 화면
      forwardBackToEdit(req, resp, dto, iae.getMessage());

    } catch (RuntimeException re) {
      throw new ServletException("재고 수정 실패: " + re.getMessage(), re);
    }
  }

  /* ===== 내부 유틸 ===== */

  private void forwardBackToEdit(HttpServletRequest req, HttpServletResponse resp,
                                 StockDTO dto, String msg)
      throws ServletException, IOException {
    req.setAttribute("mode", "edit");
    req.setAttribute("error", msg);
    req.setAttribute("stock", dto); // 사용자가 방금 입력한 값 유지

    // 품목 모달 목록 재바인딩(있으면)
    try {
      List<ItemDTO> itemList = new ItemService().getAllItem();
      req.setAttribute("itemList", itemList);
    } catch (Throwable ignore) {}

    req.getRequestDispatcher("/08_stockRegistration.jsp").forward(req, resp);
  }

  private static String trim(String s){ return s==null? null: s.trim(); }
  private static int parseInt(String s, int def){
    if (s == null || s.isEmpty()) return def;
    try { return Integer.parseInt(s.replaceAll("[^0-9-]","")); }
    catch(Exception e){ return def; }
  }
  private static String enc(String s){
    try { return java.net.URLEncoder.encode(s==null?"":s, "UTF-8"); }
    catch(Exception e){ return ""; }
  }
}
