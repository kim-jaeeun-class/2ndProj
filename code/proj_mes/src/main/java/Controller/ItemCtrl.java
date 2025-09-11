package Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dao.ItemDAO;
import Dto.ItemDTO;

@WebServlet(urlPatterns = {"/items"})
public class ItemCtrl extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String ctx  = request.getContextPath();
    String view = nv(request.getParameter("view"));
    String code = nv(request.getParameter("code"));

    ItemDAO dao = new ItemDAO();

    // 상세 보기
    if ("detail".equalsIgnoreCase(view) && !code.isEmpty()) {
      ItemDTO param = new ItemDTO();
      param.setItem_code(code);
      ItemDTO one = dao.selectOneItem(param);
      if (one == null) {
        redirectWithMsg(response, ctx + "/items", "존재하지 않는 품목입니다.");
        return;
      }
      request.setAttribute("item", one);
      request.getRequestDispatcher("/Html/11_Item/item_detail.jsp").forward(request, response);
      return;
    }

    // 목록/필터 (0=전체, 1/2/3 또는 한글 라벨 허용)
    String sel = nv(request.getParameter("select-wo"));
    Integer typeCode = mapTypeCode(sel); // null=전체

    @SuppressWarnings("unchecked")
    List<ItemDTO> all = dao.selectAll();

    List<ItemDTO> filtered = new ArrayList<>();
    if (all != null) {
      if (typeCode == null) filtered = all;
      else for (ItemDTO d : all) if (d.getItem_type() == typeCode) filtered.add(d);
    }

    request.setAttribute("items", filtered);
    request.setAttribute("selectedType", mapTypeLabel(typeCode));
    request.getRequestDispatcher("/Html/11_Item/items.jsp").forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    request.setCharacterEncoding("UTF-8");
    String ctx = request.getContextPath();
    String action = nv(request.getParameter("action"));
    ItemDAO dao = new ItemDAO();

    // 삭제
    if ("delete".equalsIgnoreCase(action)) {
      String[] codes = request.getParameterValues("selectedCode");
      if (codes == null || codes.length == 0) {
        redirectWithMsg(response, ctx + "/items", "삭제할 품목을 선택하세요.");
        return;
      }
      int ok = 0, fail = 0;
      for (String code : codes) {
        try {
          ItemDTO dto = new ItemDTO();
          dto.setItem_code(code);
          int r = dao.deleteItem(dto);
          if (r > 0) ok++; else fail++;
        } catch (Exception e) { fail++; }
      }
      redirectWithMsg(response, ctx + "/items",
        (fail == 0) ? ok + "건 삭제 완료" : ok + "건 삭제, " + fail + "건 실패");
      return;
    }

    // 수정(UPDATE)
    if ("update".equalsIgnoreCase(action)) {
      String itemCode  = nv(request.getParameter("itemCode")); // PK
      String itemName  = nv(request.getParameter("itemName"));
      String itemPrice = nv(request.getParameter("itemPrice")); // "1,000" 허용
      String itemType  = nv(request.getParameter("itemType"));  // "1"/"완제품" 등
      String itemBigo  = nv(request.getParameter("itemBigo"));

      String priceNumeric = itemPrice.replaceAll("[^0-9]", "");
      Integer t = mapTypeCode(itemType);
      int typeCode = (t == null ? -1 : t);

      StringBuilder err = new StringBuilder();
      if (itemCode.isEmpty())     err.append("품목 코드가 없습니다. ");
      if (itemName.isEmpty())     err.append("품목명을 입력하세요. ");
      if (priceNumeric.isEmpty()) err.append("단가를 입력하세요. ");
      if (typeCode < 0)           err.append("품목 유형을 선택하세요.");

      if (err.length() > 0) {
        redirectWithMsg(response, ctx + "/items?view=detail&code=" + url(itemCode), err.toString());
        return;
      }

      ItemDTO dto = new ItemDTO();
      dto.setItem_code(itemCode);
      dto.setItem_name(itemName);
      dto.setItem_bigo(itemBigo);
      dto.setItem_type(typeCode);
      dto.setItem_price(priceNumeric);

      int r = dao.updateItem(dto);
      redirectWithMsg(response, ctx + "/items?view=detail&code=" + url(itemCode),
                      (r > 0) ? "수정 완료" : "수정 실패");
      return;
    }

    // 등록(CREATE)
    if (action.isEmpty() || "create".equalsIgnoreCase(action)) {
      String itemCode  = nv(request.getParameter("itemCode"));
      String itemName  = nv(request.getParameter("itemName"));
      String itemPrice = nv(request.getParameter("itemPrice"));
      String itemType  = nv(request.getParameter("itemType"));
      String itemBigo  = nv(request.getParameter("itemBigo"));

      String priceNumeric = itemPrice.replaceAll("[^0-9]", "");
      Integer t = mapTypeCode(itemType);
      int typeCode = (t == null ? -1 : t);

      StringBuilder err = new StringBuilder();
      if (itemCode.isEmpty())     err.append("품목 코드는 필수입니다. ");
      if (itemName.isEmpty())     err.append("품목명은 필수입니다. ");
      if (priceNumeric.isEmpty()) err.append("단가는 필수입니다. ");
      if (typeCode < 0)           err.append("품목 유형을 선택하세요.");

      if (err.length() > 0) {
        redirectWithMsg(response, ctx + "/items/new", err.toString());
        return;
      }

      ItemDTO dto = new ItemDTO();
      dto.setItem_code(itemCode);
      dto.setItem_name(itemName);
      dto.setItem_bigo(itemBigo);
      dto.setItem_type(typeCode);
      dto.setItem_price(priceNumeric);

      int r = dao.insertItem(dto);
      redirectWithMsg(response, ctx + "/items",
                      (r > 0) ? "저장 완료" : "저장 실패(중복 코드 등)");
      return;
    }

    // 기타 액션 → 목록
    response.sendRedirect(response.encodeRedirectURL(ctx + "/items"));
  }

  // ---- helpers ----
  private static String nv(String s){ return s == null ? "" : s.trim(); }
  private static String url(String s){
    try { return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8.name()); }
    catch (Exception e){ return s; }
  }
  // "0"/빈/전체 → null, "1"/"완제품" → 1, "2"/"소모품" → 2, "3"/"원자재" → 3
  private static Integer mapTypeCode(String s){
    if (s == null) return null;
    s = s.trim();
    if (s.isEmpty() || "0".equals(s) || "전체".equals(s)) return null;
    switch (s) {
      case "1": case "완제품": return 1;
      case "2": case "소모품": return 2;
      case "3": case "원자재": return 3;
      // case "4": case "반제품": return 4;
      default: return null;
    }
  }
  private static String mapTypeLabel(Integer code){
    if (code == null) return "전체";
    switch (code) {
      case 1: return "완제품";
      case 2: return "소모품";
      case 3: return "원자재";
      // case 4: return "반제품";
      default: return "전체";
    }
  }
  private static void redirectWithMsg(HttpServletResponse resp, String url, String msg) throws IOException {
    String q = java.net.URLEncoder.encode(msg, java.nio.charset.StandardCharsets.UTF_8.name());
    resp.sendRedirect(resp.encodeRedirectURL(url + (url.contains("?") ? "&" : "?") + "message=" + q));
  }
}
