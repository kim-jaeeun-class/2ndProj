package Service;

import java.util.List;

import Dao.OrderListDAO;
import Dto.OrderListDTO;

public class OrderListService {

	OrderListDAO orderListDAO = new OrderListDAO();
	
	public List getAllList() {
		
		return orderListDAO.selectAll();
	}
	
//	public OrderListDTO getOneOrder(OrderListDTO orderListDTO) {
//		OrderListDTO dto = orderListDAO.selectOneOrderList(orderListDTO);
//		return dto; 
//	}
	
//	public int removeOrder(OrderListDTO orderListDTO) {
//		return orderListDAO.deleteOrderList(orderListDTO);
//	}
//	
//	public int addOrder(OrderListDTO orderListDTO) {
//		return orderListDAO.insertOrderList(orderListDTO);
//	}
//	
//	public int editOrder(OrderListDTO orderListDTO) {
//		return orderListDAO.updateOrderList(orderListDTO);
//	}
	
}
