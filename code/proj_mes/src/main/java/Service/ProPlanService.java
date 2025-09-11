package Service;

import java.util.List;

import Dao.ProPlanDAO;
import Dto.ProPlanDTO;


public class ProPlanService {
	private ProPlanDAO dao = new ProPlanDAO();
	
	// 계획 전체 조회
	public List<ProPlanDTO> getAllPlans() {
		return dao.selectAllPP();
	}
	
	// 계획 필터링 조회 1 - 오름차순 조회
	public List<ProPlanDTO> getDateUpPlans(ProPlanDTO dto) {
		return dao.selectPPDateUp(dto);
	}
	
	// 계획 필터링 조회 2 - 내림차순 조회
	public List<ProPlanDTO> getDateDownPlans(ProPlanDTO dto) {
		return dao.selectPPDateDown(dto);
	}
	
	// 작성
	public int addPlan(ProPlanDTO dto) {
		return dao.insertPP(dto);
	}
	
	// 삭제
	public int removePlan(ProPlanDTO dto) {
		return dao.deletePP(dto);
	}
	
	// 수정
	public int editPlan(ProPlanDTO dto) {
		return dao.updatePP(dto);
	}
}
