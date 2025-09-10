package Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dto.ClientDTO;
import Service.Client_Service;

@WebServlet("/Client")
public class ClientCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final Client_Service clientService = new Client_Service();

    public ClientCtrl() {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ClientDTO> list = clientService.getAllItem();
        System.out.println("[ClientCtrl] clients size = " + (list == null ? "null" : list.size()));
        request.setAttribute("clients", list);
        request.getRequestDispatcher("/Html/04_standard_gijun/04_client.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String op = request.getParameter("op");

        if ("insert".equalsIgnoreCase(op)) {
            ClientDTO d = new ClientDTO();
            d.setClient_name(request.getParameter("client_name"));
            d.setClient_phone(request.getParameter("client_phone"));
            d.setBusiness_number(request.getParameter("business_number"));
            d.setBusiness_item(request.getParameter("business_item"));
            d.setClient_address(request.getParameter("client_address"));   // hidden으로 합쳐진 주소
            // DB 컬럼은 INOUT_DIVISION, DTO는 input_division
            d.setInout_division(Integer.parseInt(request.getParameter("inout_division")));
            d.setWorker_id(request.getParameter("worker_id"));

            int ins = clientService.insert(d);
            System.out.println("[ClientCtrl] insert result = " + ins);

            // PRG 패턴
            response.sendRedirect(request.getContextPath() + "/Client");
            return;
        }

        // 기본: 목록
        doGet(request, response);
    }
}
