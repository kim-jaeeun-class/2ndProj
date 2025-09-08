package Service;

import java.util.List;

import Dao.OrderDAO;

public class OrderService {

	OrderDAO orderDAO = new OrderDAO();
	
	public List getAllOrder() {
		
		return orderDAO.selectAll();
	}
	
}
