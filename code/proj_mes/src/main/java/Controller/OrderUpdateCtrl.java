package Controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.OrderDTO;
import Dto.OrderDetDTO;
import Service.OrderService;


@WebServlet("/orderUpdate")
public class OrderUpdateCtrl extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		System.out.println("/orderUpdate doPost 실행");
	
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");
		    
		 // 파라미터 수집
		String orderKey = trim(req.getParameter("order_key"));
		
		if (isBlank(orderKey)) {
		  resp.getWriter().write("저장 실패: order_key가 없습니다.");
		  return;
		}
		
		OrderDTO order = new OrderDTO();
		
		order.setOrder_key(orderKey);
		order.setClient_id(trim(req.getParameter("client_id")));
		order.setWorker_id(trim(req.getParameter("worker_id")));
		order.setDapart_ID2(trim(req.getParameter("dapart_ID2")));
		order.setBigo(trim(req.getParameter("bigo")));
		order.setOrder_state(parseInt(req.getParameter("order_state"), 0)); // 기본 0
		
		String pay = trim(req.getParameter("order_pay")); // yyyy-MM-dd
		if (!isBlank(pay)) {
		  try { order.setOrder_pay(Date.valueOf(pay)); } catch (Exception ignore) {}
		} else {
		  order.setOrder_pay(null); // 비우면 NULL로 저장
		}
		
		// 상세(품목) 파라미터 수집 (item_code[] / item_price[] / quantity[])
		String[] codes  = params(req, "item_code");
		String[] prices = params(req, "item_price");
		String[] qtys   = params(req, "quantity");
		
		List<OrderDetDTO> details = new ArrayList<>();
		if (codes != null) {
		  for (int i = 0; i < codes.length; i++) {
		    String code = trim(codes[i]);
		    if (isBlank(code)) continue;
		
		String price = (prices != null && i < prices.length) ? trim(prices[i]) : "0";
		String qtyS  = (qtys   != null && i < qtys.length)   ? trim(qtys[i])   : "0";
		int qty = parseInt(qtyS, 0);
		
		OrderDetDTO d = new OrderDetDTO();
		
		d.setItem_code(enforceLen(code, 20));    // ORA-12899 방지
		d.setItem_price(price == null ? "0" : price);
		    d.setQuantity(qty);
		    details.add(d);
		  }
		}
		
		// 최소 1개 검증
		if (details.isEmpty()) {
		  resp.getWriter().write("저장 실패: 품목 1개 이상 필요");
		  return;
		}
		
		// 헤더 업데이트 + 상세 전체 교체
		try {
		  new OrderService().updateOrderFull(order, details);
		  resp.sendRedirect(req.getContextPath() + "/orderDetail?key=" + orderKey + "&mode=view");
		} catch (Exception e) {
		  e.printStackTrace();
		  resp.getWriter().write("저장 실패: " + e.getMessage());
		    }
		  }
		
		  // ===== helpers =====
		  private static String[] params(HttpServletRequest req, String base) {
		    String[] v = req.getParameterValues(base);
		    if (v == null) v = req.getParameterValues(base + "[]");
		    return v;
		  }
		  private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
		  private static String trim(String s){ return s == null ? null : s.trim(); }
		  private static int parseInt(String s, int def){
		    if (s == null) return def;
		    try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return def; }
		  }
		  private static String enforceLen(String s, int max){
		    if (s == null) return null;
		    String v = s.trim();
		    return v.length() <= max ? v : v.substring(0, max);
		  }
		
	}


