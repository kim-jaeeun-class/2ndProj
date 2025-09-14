package Service;

import Dao.InspectionDAO;
import Dto.InspectionDTO;
import Dto.ProcessDTO;
import Dto.StockDTO;
import Dto.WorkerDTO;

import java.util.List;

public class InspectionService {

    private InspectionDAO dao;

    public InspectionService() {
        // 서비스 생성 시 DAO 객체 초기화
        this.dao = new InspectionDAO();
    }

    // Create 메소드 ----------------------------
    
    // 새로운 검사 결과를 등록하는 메소드
    public int createInspection(InspectionDTO dto) {
        return dao.insertInspectionResult(dto);
    }
    
    // 컨트롤러에서 호출하는 메서드로, 공정 이름과 작업자 이름을 포함한 결과를 반환
    public List<InspectionDTO> getInspectionResultsWithNames() {
        return dao.getInspectionResults();
    }
    
    // 특정 ID에 해당하는 검사 결과를 조회하는 메소드
    public InspectionDTO getInspectionById(String irId) {
        return dao.getInspectionResultById(irId);
    }
    
    // 모든 LOT 번호를 조회하는 메소드
    public List<StockDTO> getLotNumbers() {
        return dao.getLotNumbers();
    }
    
    // 새로운 검사 ID를 생성하는 메소드
    public String generateInspectionId() {
        return dao.generateInspectionId();
    }

    // 모든 작업자 목록을 조회하는 메소드
    public List<WorkerDTO> getWorkers() {
        return dao.getWorkers();
    }
    
    // 특정 품목 코드를 가진 공정 목록을 조회하는 메소드
    public List<ProcessDTO> getProcessNames(String itemCode) {
        return dao.getProcessNames(itemCode);
    }
    
    // LOT ID로 공정 목록을 가져오는 메소드
    public List<ProcessDTO> getProcessesByLotId(String lotId) {
        return dao.getProcessesByLotId(lotId);
    }

    // LOT ID로 검사 결과를 가져오는 메소드
    public List<InspectionDTO> getResultsByLotId(String lotId) {
        return dao.getResultsByLotId(lotId);
    }
    
    // Update 메소드 --------------------------  
    
    // 검사 결과를 수정하는 메소드
    public int updateInspection(InspectionDTO dto) {
        return dao.updateInspectionResult(dto);
    }
    
    // Delete 메소드 -----------------  
    
    // 특정 ID에 해당하는 검사 결과를 삭제하는 메소드
    public int deleteInspection(String irId) {
        return dao.deleteInspectionResult(irId);
    }
}