package Service;

import java.util.List;

import Dao.StockDAO;
import Dto.StockDTO;

public class StockService {

    private final StockDAO stockDAO = new StockDAO();

    /** 재고 전체 목록 (items 조인으로 item_name 포함) */
    public List<StockDTO> getAllStock() {
        try {
            return stockDAO.selectAll();
        } catch (Exception e) {
            throw new RuntimeException("재고 목록 조회 실패", e);
        }
    }

    /** (프로젝트에 있다면) items 조인된 목록 제공 — 현재는 selectAll과 동일하게 위임 */
    public List<StockDTO> getStockListWithItem() {
        return getAllStock();
    }

    /** 단건 조회: dto.stock_id 사용 */
    public StockDTO getOneStock(String stockId) throws Exception {
        return stockDAO.selectOneStock(stockId);
    }


    /**
     * 등록: DAO에서 stock_id = item_code + 4자리 자동 생성.
     * 성공 시 생성된 stock_id를 반환(컨트롤러에서 반환값을 사용하지 않아도 무방).
     */
    public String addStock(StockDTO dto) {
        if (dto == null) throw new IllegalArgumentException("StockDTO 필요");
        if (isBlank(dto.getItem_code())) throw new IllegalArgumentException("item_code 필요");
        try {
            int r = stockDAO.insertStock(dto);
            if (r <= 0) throw new RuntimeException("등록 실패(영향 행 0)");
            return dto.getStock_id();  // DAO에서 setStock_id 해 둠
        } catch (Exception e) {
            throw new RuntimeException("재고 등록 실패", e);
        }
    }

    /** 수정: dto.stock_id 기준으로 업데이트 */
    public int editStock(StockDTO dto) {
        if (dto == null) throw new IllegalArgumentException("StockDTO 필요");
        if (isBlank(dto.getStock_id()))  throw new IllegalArgumentException("stock_id 필요");
        if (isBlank(dto.getItem_code())) throw new IllegalArgumentException("item_code 필요");
        try {
            return stockDAO.updateStock(dto);
        } catch (Exception e) {
            throw new RuntimeException("재고 수정 실패", e);
        }
    }

    /** 삭제: dto.stock_id 기준으로 삭제 */
    public int removeStock(StockDTO dto) {
        if (dto == null || isBlank(dto.getStock_id())) {
            throw new IllegalArgumentException("stock_id 필요");
        }
        try {
            return stockDAO.deleteStock(dto);
        } catch (Exception e) {
            throw new RuntimeException("재고 삭제 실패", e);
        }
    }

    // ===== util =====
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
