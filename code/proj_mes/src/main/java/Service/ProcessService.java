package Service;

import Dao.ProcessDAO;
import Dto.ProcessDTO;
import java.util.List;

public class ProcessService {
    private ProcessDAO processDAO = new ProcessDAO();

    // DAO를 통해 유니크한 부서 목록을 가져오는 메소드
    public List<String> getUniqueDepartLevels() {
        return processDAO.getUniqueDepartLevels();
    }

    // DAO를 통해 특정 부서에 해당하는 공정 목록을 가져오는 메소드
    public List<ProcessDTO> getProcessesByDepartLevel(String departLevel) {
        return processDAO.getProcessesByDepartLevel(departLevel);
    }
    
    // 부서와 공정명으로 필터링하는 메소드
    public List<ProcessDTO> getProcessesByDepartAndName(String departLevel, String procName) {
        return processDAO.getProcessesByDepartAndName(departLevel, procName);
    }
    
    // 모든 공정 데이터를 가져오는 메소드
    public List<ProcessDTO> getAllProcesses() {
        return processDAO.getAllProcesses();
    }
    
    // 부서에 해당하는 유니크한 공정명 목록을 가져오는 메소드
    public List<String> getUniqueProcNamesByDepart(String departLevel) {
        return processDAO.getUniqueProcNamesByDepart(departLevel);
    }
    
    // 모든 유니크한 공정명 목록을 가져오는 메소드
    public List<String> getUniqueProcNames() {
        return processDAO.getUniqueProcNames();
    }
    
    // 특정 공정의 상세 정보를 가져오는 메소드
    public ProcessDTO getProcessByIdAndDepart(String procId, String departLevel) {
        return processDAO.getProcessByIdAndDepart(procId, departLevel);
    }
    
    // 공정 정보를 수정하는 메소드
    public boolean updateProcess(ProcessDTO process) {
        return processDAO.updateProcess(process);
    }

    // 공정 정보를 삭제하는 메소드
    public boolean deleteProcess(String procId) {
        return processDAO.deleteProcess(procId);
    }
    
    // 공정 목록을 부서별로 가져오는 메서드
    public List<String> getProcNamesByDepart(String departLevel) {
        return processDAO.getProcNamesByDepart(departLevel);
    }
    
    // 새로운 공정 등록하는 메소드
    public boolean createProcess(ProcessDTO dto) {
        return processDAO.createProcess(dto);
    }
    
    // DAO를 통해 유니크한 품목 코드 목록을 가져오는 메서드
    public List<String> getUniqueItemCodes() {
        return processDAO.getUniqueItemCodes();
    }
    
    // DAO를 통해 특정 품목 코드에 해당하는 부서 목록을 가져오는 메서드
    public List<String> getDepartLevelsByItemCode(String itemCode) {
        return processDAO.getDepartLevelsByItemCode(itemCode);
    }
    
    // DAO를 통해 특정 품목 코드와 부서에 해당하는 공정명 목록을 가져오는 메서드
    public List<String> getProcNamesByItemAndDepart(String itemCode, String departLevel) {
        return processDAO.getProcNamesByItemAndDepart(itemCode, departLevel);
    }

    // DAO를 통해 특정 조건에 해당하는 공정 목록을 가져오는 메서드 (검색)
    public List<ProcessDTO> getProcessesBySearch(String itemCode, String departLevel, String procName) {
        return processDAO.getProcessesBySearch(itemCode, departLevel, procName);
    }

    // 특정 공정 ID를 기반으로 공정의 모든 정보를 가져오는 메서드 (수정 기능용)
    public ProcessDTO getProcessById(String procId) {
        return processDAO.getProcessById(procId);
    }
    
    // DAO를 통해 특정 품목 코드에 해당하는 공정 정보를 가져오는 메소드
    public ProcessDTO getProcessByItemCode(String itemCode) {
        return processDAO.getProcessByItemCode(itemCode);
    }
}
