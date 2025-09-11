package Service;

import java.util.List;

import Dao.OrderDAO;
import Dto.ItemDTO;
import Dto.OrderDTO;

public class OrderService {

	OrderDAO orderDAO = new OrderDAO();
	
	public List getAllOrder() {
		
		return orderDAO.selectAll();
	}
	
	// 1) 상세 헤더 1건 조회 (조인 포함) — 파라미터: orderDTO.order_number 사용
    public OrderDTO getOneOrder(OrderDTO orderDTO) {
        OrderDTO dto = orderDAO.selectOneOrder(orderDTO);
        return dto;  // 그대로 돌려줌
    }

    // 2) 상세 품목 목록 조회 — 파라미터: orderDTO.order_key 사용
    public List<ItemDTO> getOrderItems(OrderDTO orderDTO) {
        List<ItemDTO> list = orderDAO.selectOrderItems(orderDTO);
        return list;  // 그대로 돌려줌
    }

	
	
		
	public int removeOrder(OrderDTO orderDTO) {
		return orderDAO.deleteOrder(orderDTO);
	}
	
	public int addOrder(OrderDTO orderDTO) {
		return orderDAO.insertOrder(orderDTO);
	}
	
	public int editOrder(String orderKey, int newState) {
		return orderDAO.updateOrder(orderKey, newState);
	}
	
	public List getAllDep() {
		
		return orderDAO.selectAllDep();
	}
}
