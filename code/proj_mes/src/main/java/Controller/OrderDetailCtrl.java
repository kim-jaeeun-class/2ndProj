package Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.OrderDTO;
import Dto.OrderDetDTO;
import Service.Client_Service;
import Service.ItemService;
import Service.OrderService;

@WebServlet("/orderDetail")
public class OrderDetailCtrl extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    String key  = trim(req.getParameter("key"));   // A2025...
    String mode = trim(req.getParameter("mode"));  // add|edit|view

    OrderService svc = new OrderService();
    Client_Service cs = new Client_Service();
    ItemService is = new ItemService();

    OrderDTO order = null;
    List<OrderDetDTO> items = java.util.Collections.emptyList();

    if (key != null && !key.isEmpty()) {
      try {
        order = svc.getOrderByKey(key);
        items = svc.getOrderItemsByKey(key);
      } catch (Exception e) { e.printStackTrace(); }
    }
    if (mode == null || mode.isEmpty()) mode = (order == null ? "add" : "view");

    req.setAttribute("mode", mode);
    req.setAttribute("order", order);
    req.setAttribute("orderItems", items);
    req.setAttribute("deptList",   svc.getAllDep());
    req.setAttribute("clientList", cs.getAllClient());
    req.setAttribute("itemList",   is.getAllItem());

    req.getRequestDispatcher("/07_orderRegistration.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    req.setCharacterEncoding("utf-8");
    resp.setContentType("text/html;charset=utf-8");

    String orderKey = trim(req.getParameter("order_key"));
    if (orderKey == null || orderKey.isEmpty()) {
      resp.getWriter().write("저장 실패: order_key가 없습니다.");
      return;
    }

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
        int q = safeParseInt(qtyS, 0);

        OrderDetDTO d = new OrderDetDTO();
        d.setItem_code(enforceLen(code, 20));
        d.setItem_price(price == null ? "0" : price.trim());
        d.setQuantity(q);
        details.add(d);
      }
    }

    try {
      new OrderService().replaceOrderDetails(orderKey, details); // 0개면 전체 삭제
      resp.sendRedirect(req.getContextPath()+"/orderDetail?key="+orderKey+"&mode=view");
    } catch (Exception e) {
      e.printStackTrace();
      resp.getWriter().write("저장 실패: " + e.getMessage());
    }
  }

  // helpers
  private static String[] params(HttpServletRequest req, String base) {
    String[] v = req.getParameterValues(base);
    if (v == null) v = req.getParameterValues(base + "[]");
    return v;
  }
  private static String trim(String s){ return s == null ? null : s.trim(); }
  private static int safeParseInt(String s, int def){
    if (s == null) return def;
    try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return def; }
  }
  private static String enforceLen(String s, int max){
    if (s == null) return null;
    String v = s.trim();
    return v.length() <= max ? v : v.substring(0, max);
  }
}
