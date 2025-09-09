package Controller;

import Service.Account_Service;
import Dto.Account_DTO;
import Dto.Login_Dto;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/AccountCreate")
public class AccountCreateCtrl extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final Account_Service service = new Account_Service();

    // 계정 생성 폼 보기 (관리자만)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        // 정적 HTML로 forward (CSS를 절대경로로 써놨으면 OK)
        request.getRequestDispatcher("/Html/03_admin/03_account_create.html")
               .forward(request, response);
    }

    // 폼 제출 처리 (POST) → 성공 시 PRG로 리스트/관리 페이지 이동
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        request.setCharacterEncoding("UTF-8");

        // 1) 파라미터 수집
        String workerId    = p(request, "worker_id");
        String workerName  = p(request, "worker_name");
        String rrn1        = p(request, "rrn1");
        String rrn2        = p(request, "rrn2");
        String rrnPlain    = (rrn1 + rrn2).replaceAll("\\D", ""); // 13자리 기대

        String phone = String.join("-",
                nz(request.getParameter("phone1")),
                nz(request.getParameter("phone2")),
                nz(request.getParameter("phone3"))
        ).replaceAll("-+$", "");

        String emailId     = p(request, "email_id");
        String emailDomain = p(request, "email_domain");
        String customDom   = p(request, "custom_domain");
        String domain      = "custom".equals(emailDomain) ? customDom : emailDomain;
        String email       = (!isBlank(emailId) && !isBlank(domain)) ? (emailId + "@" + domain) : "";

        String rawPw       = p(request, "worker_pw");
        String pw2         = p(request, "password_confirm");

        int workerGrade    = parseIntOrDefault(request.getParameter("worker_grade"), 0);
        String workerCando = request.getParameter("worker_cando");
//        int workerCando    = parseIntOrDefault(request.getParameter("worker_cando"), 0);
        String dapartId2   = p(request, "dapart_ID2");

        // 2) 1차 검증 (간단)
        if (!rawPw.equals(pw2)) {
            redirectWithError(response, request, "비밀번호가 일치하지 않습니다.");
            return;
        }

        // 3) DTO 구성 (주민번호/비번은 서비스에서 암호화/해시)
        Account_DTO dto = new Account_DTO();
        dto.setWorkerId(workerId);
        dto.setWorkerName(workerName);
        dto.setWorkerEmail(email);
        dto.setWorkerPhone(phone);
        dto.setWorkerGrade(workerGrade);
        dto.setWorkerCando(workerCando);
        dto.setDapartId2(dapartId2);

        // 4) 서비스 호출
        try {
            service.create(dto, rawPw, rrnPlain);
            // 성공: 계정관리로 이동(서블릿 경유 권장)
            response.sendRedirect(request.getContextPath() + "/AccountManage?created=1");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 유효성/중복 등 사용자 오류
            redirectWithError(response, request, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithError(response, request, "서버 오류가 발생했습니다.");
        }
    }

    // ---------- helpers ----------
    private static String p(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
    private static String nz(String s) { return s == null ? "" : s.trim(); }
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
    private static void redirectWithError(HttpServletResponse resp, HttpServletRequest req, String msg) throws IOException {
        String q = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());
        // PRG: 에러 메시지를 쿼리로 넘겨 폼으로 복귀 (정적 HTML이면 JS로 location.search 읽어 표시)
        resp.sendRedirect(req.getContextPath() + "/AccountCreate?error=" + q);
    }



    /** 세션의 로그인 계정이 관리자 등급인지 확인 */
    private static boolean isAdmin(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s == null) return false;
        Object obj = s.getAttribute("loginUser");
        if (!(obj instanceof Login_Dto)) return false;
        Login_Dto u = (Login_Dto) obj;
        System.out.println("[AccountCreate] id=" + u.getWorkerId() + ", grade=" + u.getWorkerGrade());
        return u.getWorkerGrade() == 0; // 필요 시 == 2 로 변경
    }
}
