package Service;

import java.util.List;

import Dao.LotTrackingDAO;
import Dto.LotTrackingDTO;
import Dto.ItemDTO;
import Dto.ProcessDTO;

public class LotTrackingService {
    private LotTrackingDAO dao = new LotTrackingDAO();

    // ================= Create =================
    public int addLotTracking(LotTrackingDTO dto) {
        return dao.insertLotTracking(dto);
    }

    // ================= Read =================
    /** 메인 LOT 목록 */
    public List<LotTrackingDTO> getLotTrackingList(String itemCode, String date) {
        return dao.selectLotTrackingList(itemCode, date);
    }

    /** LOT 공정 이력 */
    public List<LotTrackingDTO> getProcessHistoryByLot(String lotId) {
        return dao.selectProcessHistoryByLot(lotId);
    }

    /** LOT 검사 결과 */
    public List<LotTrackingDTO> getInspectionResultByLot(String lotId) {
        return dao.selectInspectionResultByLot(lotId);
    }

    /** 품목 목록 */
    public List<ItemDTO> getItemList() {
        return dao.selectItemList();
    }

    /** 전체 공정 목록 */
    public List<ProcessDTO> getAllProcessList() {
        return dao.selectAllProcessList();
    }

    // ================= Update =================
    public int updateLotTracking(LotTrackingDTO dto) {
        return dao.updateLotTracking(dto);
    }

    // ================= Delete =================
    public int deleteLotTracking(String lotId) {
        return dao.deleteLotTracking(lotId);
    }
}