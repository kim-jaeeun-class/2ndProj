package Service;

import java.util.List;

import Dao.WorkOrderDAO;
import Dto.WorkOrderDTO;

public class WorkOrderService {
	// CONTROLLER에서 호출될 내용들 작성.
	// DAO - CONTROLLER 사이의 중간 계층. CONTROLLER가 DAO에 직접 접근하지 않도록 함.
	// = SERVLET이 DB를 알 필요가 없음!
    private WorkOrderDAO dao = new WorkOrderDAO();
    
	// 전체 조회 : 문제점 - 거래처!!!! -> 해결함
	public List<WorkOrderDTO> getAllOrders() {
	    return dao.selectAllWO();
	}
	
	// 필터링 조회 -작업 지시일 기준 : 지금 () 안 값이 이게 아닌 것 같은데
    public List<WorkOrderDTO> getFilteredOrders(WorkOrderDTO dto) {
        return dao.selectOrderWO(dto);
    }
    
    // 품목 조회
    public List<WorkOrderDTO> getItemsWO(WorkOrderDTO dto) {
    	return dao.selectItemWO();
    }
    
    // 등록
    public int addWorkOrder(WorkOrderDTO dto) {
        return dao.insertWO(dto);
    }
    
    // 삭제
    public int removeWorkOrder(WorkOrderDTO dto) {
        return dao.deleteWO(dto);
    }
    
    // 전체 수정
    public int editAll(WorkOrderDTO dto) {
        return dao.updateAllWO(dto);
    }
    
    // 실제 생산량 수정
    public int editAQ(WorkOrderDTO dto) {
    	return dao.updateAQWO(dto);
    }
    
	// 특정 품목의 BOM 정보 조회
    public WorkOrderDTO getBOMInfo(String itemCode) {
        WorkOrderDTO dto = new WorkOrderDTO();
        dto.setItem_code(itemCode);

        List<WorkOrderDTO> list = dao.selectBOM(dto);
        if (list.isEmpty()) {
            dto.setBom_id("0"); // 없는 경우 0 처리
        } else {
            dto.setBom_id(list.get(0).getBom_id());
        }

        return dto;
    }

    // 특정 품목의 공정 정보 조회
    public WorkOrderDTO getProcInfo(String itemCode) {
        WorkOrderDTO dto = new WorkOrderDTO();
        dto.setItem_code(itemCode);

        List<WorkOrderDTO> list = dao.selectProc(dto);
        if (list.isEmpty()) {
            dto.setProc_id("0"); // 없는 경우 0 처리
            dto.setProc_name("0");
        } else {
            dto.setProc_id(list.get(0).getProc_id());
            dto.setProc_name(list.get(0).getProc_name());
        }

        return dto;
    }

}
