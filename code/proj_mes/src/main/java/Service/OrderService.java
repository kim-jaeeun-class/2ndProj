package Service;

import java.util.List;

import Dao.OrderDAO;
import Dto.OrderDTO;

public class OrderService {

	OrderDAO orderDAO = new OrderDAO();
	
	public List getAllOrder() {
		
		return orderDAO.selectAll();
	}
	
	public OrderDTO getOneOrder(OrderDTO orderDTO) {
		OrderDTO dto = orderDAO.selectOneOrder(orderDTO);
		return dto; 
	}
	
	public int removeOrder(OrderDTO orderDTO) {
		return orderDAO.deleteOrder(orderDTO);
	}
	
	public int addOrder(OrderDTO orderDTO) {
		return orderDAO.insertOrder(orderDTO);
	}
	
	public int editOrder(OrderDTO orderDTO) {
		return orderDAO.updateOrder(orderDTO);
	}
}
