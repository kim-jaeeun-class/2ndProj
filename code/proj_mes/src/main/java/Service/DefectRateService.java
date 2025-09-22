package Service;

import java.util.List;

import Dao.DefectRateDAO;
import Dto.DefectRateDTO;
import Dto.ItemDTO;
import Dto.ProcessDTO;

public class DefectRateService {
    private DefectRateDAO dao = new DefectRateDAO();

    public List<DefectRateDTO> getDefectRateList(String procId, String itemCode, String date) {
        return dao.getDefectRateList(procId, itemCode, date);
    }

    public List<ProcessDTO> getProcessList() {
        return dao.getProcList();
    }

    public List<ItemDTO> getItemList() {
        return dao.getItemList();
    }
}