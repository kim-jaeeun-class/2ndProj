package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.Login_Dto;
import Service.Login_Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/login")
public class LoginCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(LoginCtrl.class);
    private final Login_Service loginService = new Login_Service();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Html/01_login/login.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("userid");
        String pw = request.getParameter("password");
        log.info("[LOGIN] try id={}", id);

        Login_Dto user = loginService.login(id, pw);

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("loginUser", user);
            try { request.changeSessionId(); } catch (Throwable ignore) {}
            log.info("[LOGIN] success id={}, grade={}", user.getWorkerId(), user.getWorkerGrade());

            //첫 화면
            response.sendRedirect(request.getContextPath() + "/mainpage");
        } else {
            log.info("[LOGIN] fail id={}", id);
            response.sendRedirect(request.getContextPath() + "/Html/01_login/login.html?error=1");
        }
//        로그를 어떤 형식오로 저장해야 할까??
    }
}
