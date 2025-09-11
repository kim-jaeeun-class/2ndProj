package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/items/new")
public class ItemNewCtrl extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/Html/11_Item/items_form.jsp").forward(req, resp);
  }



  
  
  
  
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
