package Service;

import Dao.ProcessDAO;
import Dto.ProcessDTO;
import java.util.List;

public class ProcessService {
    private ProcessDAO processDAO = new ProcessDAO();

    // DAO를 통해 유니크한 부서 목록을 가져오는 메서드
    public List<String> getUniqueDepartLevels() {
        return processDAO.getUniqueDepartLevels();
    }

    // DAO를 통해 특정 부서에 해당하는 공정 목록을 가져오는 메서드
    public List<ProcessDTO> getProcessesByDepartLevel(String departLevel) {
        return processDAO.getProcessesByDepartLevel(departLevel);
    }
}
