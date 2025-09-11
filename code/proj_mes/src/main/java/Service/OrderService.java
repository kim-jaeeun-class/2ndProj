package Service;

import java.util.List;

import Dao.OrderDAO;
import Dto.OrderDTO;
import Dto.OrderDetDTO;

public class OrderService {
  private final OrderDAO orderDAO = new OrderDAO();

  // 목록(합계 포함)
  public List<OrderDTO> getAllOrder() throws Exception {
    return orderDAO.selectAllWithSummary();
  }

  // 헤더 + 디테일 동시 저장
  public String addOrderDet(OrderDTO orderDTO, List<OrderDetDTO> details) throws Exception {
    if (orderDTO == null) throw new IllegalArgumentException("OrderDTO 필요");
    if (details == null || details.isEmpty()) throw new IllegalArgumentException("품목 1개 이상 필요");
    if (isBlank(orderDTO.getClient_id()))  throw new IllegalArgumentException("client_id 필요");
    if (isBlank(orderDTO.getWorker_id()))  throw new IllegalArgumentException("worker_id 필요");
    if (isBlank(orderDTO.getDapart_ID2())) throw new IllegalArgumentException("dapart_ID2 필요");
    return orderDAO.insertOrderDet(orderDTO, details);
  }

  // 상세 교체 저장
  public void replaceOrderDetails(String orderKey, List<OrderDetDTO> details) throws Exception {
    if (isBlank(orderKey)) throw new IllegalArgumentException("order_key 필요");
    orderDAO.replaceOrderDetails(orderKey.trim(), details == null ? java.util.Collections.emptyList() : details);
  }

  // 삭제(디테일 → 헤더)
  public void removeOrdersCascade(List<String> orderKeys) throws Exception {
    if (orderKeys == null || orderKeys.isEmpty()) return;
    orderDAO.deleteOrdersCascade(orderKeys);
  }

  // 상세 조회
  public OrderDTO getOrderByKey(String orderKey) throws Exception {
    return orderDAO.selectOneByKey(orderKey);
  }
  public List<OrderDetDTO> getOrderItemsByKey(String orderKey) throws Exception {
    return orderDAO.selectOrderItemsByKey(orderKey);
  }

  // 부서 목록 (기존 DAO 메서드 그대로 사용)
  public List getAllDep() {
    return orderDAO.selectAllDep();
  }

  private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
