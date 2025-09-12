package Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.StockDTO;
import Service.StockService;

@WebServlet("/Stock")
public class StockCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 인스턴스 서비스
    private final StockService stockService = new StockService();

    public StockCtrl() { super(); }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String cmd = nvl(request.getParameter("cmd"), "list");

        switch (cmd) {
            case "list":
            case "search": {
                String big   = norm(request.getParameter("big"));
                String mid   = norm(request.getParameter("mid"));
                String small = norm(request.getParameter("small"));
                java.sql.Date from = parseSqlDate(request.getParameter("from")); // yyyy-MM-dd
                java.sql.Date to   = parseSqlDate(request.getParameter("to"));

                List<StockDTO> list = stockService.searchStocks(big, mid, small, from, to);

                long sumQty = 0, sumPrice = 0;
                for (StockDTO s : list) {
                    sumQty += s.getStock_number();
                    try {
                        if (s.getItem_price() != null && !s.getItem_price().isEmpty()) {
                            sumPrice += Long.parseLong(s.getItem_price().replaceAll("[^0-9]", ""));
                        }
                    } catch (Exception ignore) {}
                }

                request.setAttribute("list", list);
                request.setAttribute("sumQty", sumQty);
                request.setAttribute("sumPrice", sumPrice);
                request.setAttribute("big", big);
                request.setAttribute("mid", mid);
                request.setAttribute("small", small);
                request.setAttribute("from", request.getParameter("from"));
                request.setAttribute("to", request.getParameter("to"));

                // 목록 화면
                request.getRequestDispatcher("/08_stockList.jsp").forward(request, response);
                break;
            }

            case "addForm": {
                // 등록 폼 화면
                request.getRequestDispatcher("/08_stockRegistration.jsp").forward(request, response);
                break;
            }

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown cmd: " + cmd);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String cmd = nvl(request.getParameter("cmd"), "");

        switch (cmd) {
            case "add": {
                StockDTO dto = bind(request);
                stockService.addStock(dto);
                response.sendRedirect(request.getContextPath() + "/Stock?cmd=list");
                break;
            }
            case "edit": {
                StockDTO dto = bind(request);
                stockService.editStock(dto);
                response.sendRedirect(request.getContextPath() + "/Stock?cmd=list");
                break;
            }
            case "remove": {
                String id = request.getParameter("stock_id");
                StockDTO dto = new StockDTO();
                dto.setStock_id(id);
                stockService.removeStock(dto);
                response.sendRedirect(request.getContextPath() + "/Stock?cmd=list");
                break;
            }
            default:
                doGet(request, response);
        }
    }

    // ---- 유틸 ----
    private static String nvl(String s, String d) { return (s == null) ? d : s; }
    private static java.sql.Date parseSqlDate(String s) {
        try { return (s == null || s.isEmpty()) ? null : java.sql.Date.valueOf(s); }
        catch (Exception e) { return null; }
    }
    /** "ALL"/"전체" → "" */
    private static String norm(String s) {
        if (s == null) return "";
        s = s.trim();
        if ("ALL".equalsIgnoreCase(s) || "전체".equals(s)) return "";
        return s;
    }
    /** form 바인딩 */
    private static StockDTO bind(HttpServletRequest req) {
        StockDTO dto = new StockDTO();
        dto.setStock_id(nvl(req.getParameter("stock_id"), ""));
        dto.setStock_date(parseSqlDate(req.getParameter("stock_date")));
        dto.setStock_loc(parseInt(req.getParameter("stock_loc")));
        dto.setStock_div(parseInt(req.getParameter("stock_div")));
        dto.setStock_stat(parseInt(req.getParameter("stock_stat")));
        dto.setStock_number(parseInt(req.getParameter("stock_number")));
        dto.setItem_code(nvl(req.getParameter("item_code"), ""));
        return dto;
    }
    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
}
