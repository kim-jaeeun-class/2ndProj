package Controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.OrderDTO;
import Dto.OrderDetDTO;
import Service.OrderService;

@WebServlet("/orderAdd")
public class OrderAddCtrl extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    // 1) 헤더 파라미터
    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setClient_id(req.getParameter("client_id"));
    orderDTO.setWorker_id(req.getParameter("worker_id"));
    orderDTO.setDapart_ID2(req.getParameter("dapart_ID2"));
    orderDTO.setOrder_state(parseIntOrDefault(req.getParameter("order_state"), 0));

    // 납기일(yyyy-MM-dd) 저장
    String pay = req.getParameter("order_pay");
    if (pay != null && !pay.isBlank()) {
      orderDTO.setOrder_pay(Date.valueOf(pay));
    }

    // 2) 품목 파라미터 (item_code / item_code[] 모두 수용)
    String[] codes  = params(req, "item_code");
    String[] prices = params(req, "item_price");
    String[] qtys   = params(req, "quantity");

    List<OrderDetDTO> details = new ArrayList<>();
    if (codes != null) {
      for (int i = 0; i < codes.length; i++) {
        String code = trim(codes[i]);
        if (code == null || code.isEmpty()) continue;

        String price = (prices != null && i < prices.length) ? prices[i] : "0";
        String qtyS  = (qtys   != null && i < qtys.length)   ? qtys[i]   : "0";
        int qty = safeParseInt(qtyS, 0);

        OrderDetDTO d = new OrderDetDTO();
        d.setItem_code(enforceLen(code, 20));             // ORA-12899 방지
        d.setItem_price(price == null ? "0" : price.trim());
        d.setQuantity(qty);
        details.add(d);
      }
    }

    if (details.isEmpty()) {
      resp.getWriter().write("등록 실패: 품목 1개 이상 필요");
      return;
    }

    // 3) 저장
    try {
      String newKey = new OrderService().addOrderDet(orderDTO, details);
      resp.sendRedirect(req.getContextPath()+"/orderList");
    } catch (Exception e) {
      e.printStackTrace();
      resp.getWriter().write("등록 실패: " + e.getMessage());
    }
  }

  // ===== helpers =====
  private static String[] params(HttpServletRequest req, String base) {
    String[] v = req.getParameterValues(base);
    if (v == null) v = req.getParameterValues(base + "[]");
    return v;
  }
  private static int safeParseInt(String s, int def){
    if (s == null) return def;
    try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return def; }
  }
  private static String enforceLen(String s, int max){
    if (s == null) return null;
    String v = s.trim();
    return v.length() <= max ? v : v.substring(0, max);
  }
  private static int parseIntOrDefault(String s, int def){
    if (s == null) return def;
    try { return Integer.parseInt(s.replaceAll("[^0-9]","")); } catch(Exception e){ return def; }
  }
  private static String trim(String s){ return s == null ? null : s.trim(); }
}
