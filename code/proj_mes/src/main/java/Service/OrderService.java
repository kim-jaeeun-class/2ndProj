package service;

import java.util.List;

import dao.OrderDAO;

public class OrderService {

	OrderDAO orderDAO = new OrderDAO();
	
	public List getAllOrder() {
		
		return orderDAO.selectAll();
	}
	
}
