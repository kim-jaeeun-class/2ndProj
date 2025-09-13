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

 String id   = req.getParameter("stock_id");   // stock_id
 String mode = req.getParameter("mode"); // add | edit | view

 try {
   StockService ss = new StockService();
   ItemService  is = new ItemService();  // 모달용 목록

   StockDTO stock = ss.getOneStock(id);      // ★ 여기서 item_price까지 채워짐
   List<ItemDTO> itemList = is.getAllItem(); // 모달용

   req.setAttribute("mode",  mode == null ? (stock == null ? "add" : "view") : mode);
   req.setAttribute("stock", stock);
   req.setAttribute("itemList", itemList);

   req.getRequestDispatcher("/08_stockRegistration.jsp").forward(req, resp);
 } catch (Exception e) {
   e.printStackTrace();
   throw new ServletException("재고 상세 조회 실패: " + e.getMessage());
 }
}
}
