package Controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet("/logout")
public class Logout extends HttpServlet {
    private static final Logger log = LogManager.getLogger(LoginCtrl.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

    }
    
    
    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        System.out.println(req.getContextPath());
        String id = req.getParameter("userid");
        log.info("[LOGOUT] logout id={}", id);


        try {
            req.logout(); 
        } catch (ServletException ignored) {}

        // 세션 무효화 (새 세션 생성 방지를 위해 false)
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // JSESSIONID 쿠키 제거
        Cookie kill = new Cookie("JSESSIONID", "");
        kill.setMaxAge(0);
        String ctxPath = req.getContextPath();
        kill.setPath((ctxPath == null || ctxPath.isEmpty()) ? "/" : ctxPath);
        kill.setHttpOnly(true);
        if (req.isSecure()) kill.setSecure(true);
        resp.addCookie(kill);


        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        //  로그인 페이지로 이동
        resp.sendRedirect(req.getContextPath() + "/login.html");
  
    }    
}