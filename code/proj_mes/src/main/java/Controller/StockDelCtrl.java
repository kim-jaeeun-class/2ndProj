package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.StockDTO;
import Service.StockService;

@WebServlet("/stockDel")
public class StockDelCtrl extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    String idsParam = req.getParameter("stock_id"); // 단건 or "id1,id2"
    if (idsParam == null || idsParam.trim().isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/stockList" +
          "?error=" + enc("삭제할 항목을 선택하세요."));
      return;
    }

    StockService svc = new StockService();
    int deleted = 0;

    try {
      String[] ids = idsParam.split(",");
      for (String id : ids) {
        if (id == null) continue;
        String trimmed = id.trim();
        if (trimmed.isEmpty()) continue;

        StockDTO dto = new StockDTO();
        dto.setStock_id(trimmed);
        deleted += svc.removeStock(dto);
      }

      resp.sendRedirect(req.getContextPath() + "/stockList" +
          (deleted > 0 ? "" : ("?error=" + enc("삭제된 항목이 없습니다."))));

    } catch (IllegalArgumentException iae) {
      resp.sendRedirect(req.getContextPath() + "/stockList?error=" + enc(iae.getMessage()));
    } catch (RuntimeException re) {
      throw new ServletException("재고 삭제 실패: " + re.getMessage(), re);
    }
  }

  private static String enc(String s) {
    try { return java.net.URLEncoder.encode(s == null ? "" : s, "UTF-8"); }
    catch (Exception e) { return ""; }
  }
}
