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

    // ✅ Logger 선언
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
        System.out.println("Login Ctrl POST");

        String id = request.getParameter("userid");
        String pw = request.getParameter("password");

        System.out.println("userid = " + id);
        System.out.println("password = " + pw);

        Login_Dto user = loginService.login(id, pw);

//        // ✅ 로깅 테스트
        log.info("✅ Log4j2 TXT 파일 테스트 - INFO");
        log.warn("⚠️ 경고 로그 - WARN");
//        log.error("❌ 에러 로그 - ERROR", new RuntimeException("boom"));
//        일부로 에러를 발생시키는 방법

        // (참고) 아래처럼 바디에 쓰면 이후 sendRedirect와 충돌할 수 있으니 테스트 후 지우세요.
        // response.setContentType("text/plain; charset=UTF-8");
        // response.getWriter().println("로그를 남겼어요. Tomcat logs/app.txt 확인!");

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("loginUser", user);
            try { request.changeSessionId(); } catch (Throwable ignore) {}

            if (user.getWorkerGrade() == 2) {
                response.sendRedirect(request.getContextPath() + "/Html/02_main/mainpage.html");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/Html/01_login/login.html?error=1");
        }
    }
}
