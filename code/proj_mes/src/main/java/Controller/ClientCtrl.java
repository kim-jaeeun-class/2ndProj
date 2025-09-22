package Controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.ClientDTO;
import Service.Client_Service;

@WebServlet("/Client")
public class ClientCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Client_Service clientService = new Client_Service();

    // 폼에서 온 "출고/공통/발주" 또는 "-1/0/1"을 정수 코드(-1/0/1)로 변환
    private Integer resolveDivision(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        s = s.trim();
        switch (s) {
            case "출고": return -1;
            case "공통": return 0;
            case "발주": return 1;
            default:
                try { return Integer.valueOf(s); } catch (NumberFormatException e) { return null; }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//        List<ClientDTO> list = clientService.getAllItem();
//        req.setAttribute("clients", list);
        req.getRequestDispatcher("/Html/04_standard_gijun/04_client.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String op = req.getParameter("op");

        if ("insert".equalsIgnoreCase(op)) {
            ClientDTO d = new ClientDTO();
            d.setClient_name(req.getParameter("client_name"));
            d.setClient_phone(req.getParameter("client_phone"));
            d.setBusiness_number(req.getParameter("business_number"));
            d.setBusiness_item(req.getParameter("business_item"));
            d.setClient_address(req.getParameter("client_address"));

            Integer div = resolveDivision(req.getParameter("inout_division")); // 출고=-1, 공통=0, 발주=1
            d.setInout_division(div == null ? null : String.valueOf(div));
            d.setWorker_id(req.getParameter("worker_id"));

            int ins = clientService.insert(d);
            System.out.println("[ClientCtrl] insert result = " + ins);
            resp.sendRedirect(req.getContextPath() + "/Client");
            return;
        }

        if ("delete".equalsIgnoreCase(op)) {
            // 체크박스로 넘어온 client_id 배열(name="ids")
            String[] ids = req.getParameterValues("ids");
            int deleted = 0;
            if (ids != null && ids.length > 0) {
                deleted = clientService.deleteByIds(Arrays.asList(ids));
            }
            System.out.println("[ClientCtrl] deleted count = " + deleted);
            resp.sendRedirect(req.getContextPath() + "/Client");
            return;
        }

        // 기본: 목록
        doGet(req, resp);
    }
}
