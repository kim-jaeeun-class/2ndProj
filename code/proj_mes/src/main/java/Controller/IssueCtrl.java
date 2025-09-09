package Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dto.Account_DTO;
import Dto.Login_Dto;
import Service.Account_Service;

/**
 * Servlet implementation class IssueCtrl
 */
@WebServlet("/IssueCtrl")
public class IssueCtrl extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IssueCtrl() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    private final Account_Service service = new Account_Service();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

      HttpSession s = request.getSession(false);
      Login_Dto loginUser = (s != null) ? (Login_Dto) s.getAttribute("loginUser") : null;

      if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
      }
      boolean isAdmin = (loginUser.getWorkerGrade() == 0);
      if (!isAdmin) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      try {
        String q = request.getParameter("q");
        List<Account_DTO> accounts = (q == null || q.trim().isEmpty())
            ? service.listAll()
            : service.search(q.trim());
        request.setAttribute("accounts", accounts);
      } catch (Exception e) {
        throw new ServletException(e);
      }


      request.getRequestDispatcher("/Html/03_admin/03_issue.html")
             .forward(request, response);
    }
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
