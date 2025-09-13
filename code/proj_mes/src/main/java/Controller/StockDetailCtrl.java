package Controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.StockDTO;
import Dto.ItemDTO;
import Service.StockService;
import Service.ItemService;

//StockDetailCtrl.java
@WebServlet("/stockDetail")
public class StockDetailCtrl extends HttpServlet {
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
	req.setCharacterEncoding("utf-8");
	resp.setContentType("text/html;charset=utf-8");

	String mode    = nvl(req.getParameter("mode"));      // add | edit | view
	String stockId = nvl(req.getParameter("stock_id"));  // ← 통일
	
	try {
	   StockService ss = new StockService();
	   ItemService  is = new ItemService();
	
	   StockDTO stock = null;
	   if (!"add".equalsIgnoreCase(mode)) {
	     if (stockId.isEmpty()) throw new IllegalArgumentException("stock_id가 필요합니다.");
	     stock = ss.getOneStock(stockId);
	     if (stock == null) throw new IllegalArgumentException("존재하지 않는 재고입니다.");
	   }
	
	   // 모달 목록은 항상 바인딩
	   java.util.List<Dto.ItemDTO> itemList = is.getAllItem();
	
	   if (mode.isEmpty()) mode = (stock == null ? "add" : "view");
	
	   req.setAttribute("mode", mode);
	   req.setAttribute("stock", stock);
	   req.setAttribute("itemList", itemList);
	
	   req.getRequestDispatcher("/08_stockRegistration.jsp").forward(req, resp);
	
	 } catch (Exception e) {
	   e.printStackTrace();
	   throw new ServletException("재고 상세 조회 실패: " + e.getMessage(), e);
	 }
	}
	
	private static String nvl(String s){ return s==null ? "" : s.trim(); }
	}
