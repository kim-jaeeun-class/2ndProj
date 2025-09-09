package Service;

import java.util.List;

import Dao.WorkOrderDAO;
import Dto.WorkOrderDTO;

public class WorkOrderService {
	// CONTROLLER에서 호출될 내용들 작성.
	// DAO - CONTROLLER 사이의 중간 계층. CONTROLLER가 DAO에 직접 접근하지 않도록 함.
	// = SERVLET이 DB를 알 필요가 없음!
	
	// 그냥 전체 : 문제점 - 거래처!!!!
	
	public List<WorkOrderDTO> getAllOrders() {
	    return dao.selectAllWO();
	}
	
	// 필터링
    private WorkOrderDAO dao = new WorkOrderDAO();

    public List<WorkOrderDTO> getFilteredOrders(String stat) {
        return dao.selectOrderWO(stat);
    }
    
    // 등록
    
    public int addWorkOrder(WorkOrderDTO dto) {
        return dao.insertEmp(dto);
    }
    
    // 삭제

    public int removeWorkOrder(WorkOrderDTO dto) {
        return dao.deleteWO(dto);
    }
    
    // 품목 조회
    
    public List<WorkOrderDTO> getAllItems() {
        return dao.selectItemWO();
    }
}
