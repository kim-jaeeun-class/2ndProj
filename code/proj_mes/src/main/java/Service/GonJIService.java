package Service;

import java.util.List;

import Dao.GonjiDAO;

public class GonJIService {
	GonjiDAO GonjiDAO = new GonjiDAO();
	
	public List getAllOrder() {
		
		return GonjiDAO.selectAll();
	}
}
