package Service;

import Dao.ProcessDAO;
import Dto.ProcessDTO;
import java.util.List;

public class ProcessService {
    private ProcessDAO processDAO = new ProcessDAO();

    // Create (등록) =========================
    
    // 새로운 공정 정보를 등록
    public boolean createProcess(ProcessDTO dto) {
        return processDAO.createProcess(dto);
    }
    
    // Read (조회) =========================
    
    // item 테이블에서 item_code 모두 조회
    public List<String> getAllItemCodes() {
        return processDAO.getAllItemCodes();
    }
    
    // 모든 유니크한 품목 코드 목록을 조회
    public List<String> getUniqueItemCodes() {
        return processDAO.getUniqueItemCodes();
    }

    // 모든 유니크한 부서 레벨 목록을 조회 
    public List<String> getUniqueDepartLevels() {
        return processDAO.getUniqueDepartLevels();
    }
    
    // 모든 유니크한 공정명 목록을 조회
    public List<String> getUniqueProcNames() {
        return processDAO.getUniqueProcNames();
    }

    // 품목 코드, 부서, 공정명으로 공정 목록을 검색
    public List<ProcessDTO> getProcessesBySearch(String itemCode, String departLevel, String procName) {
        return processDAO.getProcessesBySearch(itemCode, departLevel, procName);
    }

    // 공정 ID를 사용하여 특정 공정의 상세 정보를 조회
    public ProcessDTO getProcessById(String procId) {
        return processDAO.getProcessById(procId);
    }

    // 품목 코드를 사용하여 특정 공정의 상세 정보를 조회
    public ProcessDTO getProcessByItemCode(String itemCode) {
        return processDAO.getProcessByItemCode(itemCode);
    }
    
    public String getDepartIdByLevel(String departLevel) {
        // DAO의 getDepartIdByLevel 메서드를 호출하여 부서 ID를 반환
        return processDAO.getDepartIdByLevel(departLevel);
    }

    // 특정 품목 코드에 해당하는 부서 목록을 조회
    public List<String> getDepartLevelsByItemCode(String itemCode) {
    	return processDAO.getDepartLevelsByItemCode(itemCode);
    }

    // 특정 부서에 해당하는 유니크한 공정명 목록을 조회
    public List<String> getUniqueProcNamesByDepart(String departLevel) {
        return processDAO.getUniqueProcNamesByDepart(departLevel);
    }
    
    // 특정 품목 코드와 부서에 해당하는 공정명 목록을 조회
    public List<ProcessDTO> getProcNamesByItemAndDepart(String itemCode, String departLevel) {
        return processDAO.getProcNamesByItemAndDepart(itemCode, departLevel);
    }
    
    // proc 테이블에서 모든 공정명 목록을 조회
    public List<String> getUniqueProcNamesFromProcTable() {
        return processDAO.getUniqueProcNamesFromProcTable();
    }
    
    // Update (수정) =========================
 
    // 공정 정보를 수정
    public boolean updateProcess(ProcessDTO dto) {
        return processDAO.updateProcess(dto);
    }

    // Delete (삭제) =========================
    
    // 공정 ID를 사용하여 공정을 삭제
    public boolean deleteProcess(String procId) {
        return processDAO.deleteProcess(procId);
    }
}