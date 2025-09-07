// package Controller;
package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Dto.Login_Dto;
import Service.Login_Service;

@WebServlet("/login") 
public class LoginCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;

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
        System.out.println("Login Ctrl POST");

        String id = request.getParameter("userid");
        String pw = request.getParameter("password");

        System.out.println("userid = " + id);
        System.out.println("password = " + pw);

        Login_Dto user = loginService.login(id, pw);

        if (user != null) {
            // 로그인 성공
            HttpSession session = request.getSession(true);
            session.setAttribute("loginUser", user);
            // (보안) 세션 고정화 방지 - 서블릿 3.1+
            try { request.changeSessionId(); } catch (Throwable ignore) {}

            // 권한 분기 어떻게 할까
            if (user.getWorkerGrade() == 2) {
                response.sendRedirect(request.getContextPath() + "/Html/02_main/mainpage.html");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } else {
            // 실패 시 정적 로그인 페이지로 되돌리기 (JSP 미사용 환경에서도 404 안 남)
            response.sendRedirect(request.getContextPath() + "/Html/01_login/login.html?error=1");
        }
    }
}
