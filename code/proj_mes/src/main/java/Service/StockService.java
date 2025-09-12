package Service;

import java.util.List;

import Dao.StockDAO;
import Dto.StockDTO;

public class StockService {

    private final StockDAO stockDAO = new StockDAO();

    public List<StockDTO> getAllStock() {
        return stockDAO.selectAll();
    }

    public List<StockDTO> getStockListWithItem() {
        return stockDAO.stock_list_menu();
    }

    public List<StockDTO> findStocks(String big, String mid, String small) {
        return stockDAO.findStocks(big, mid, small);
    }

    public List<StockDTO> searchStocks(String big, String mid, String small,
                                       java.sql.Date from, java.sql.Date to) {
        return stockDAO.searchStocks(big, mid, small, from, to);
    }

    public StockDTO getOneStock(StockDTO stockDTO) {
        return stockDAO.selectOneStock(stockDTO);
    }

    public int removeStock(StockDTO stockDTO) {
        return stockDAO.deleteStock(stockDTO);
    }

    public int addStock(StockDTO stockDTO) {
        return stockDAO.insertStock(stockDTO);
    }

    public int editStock(StockDTO stockDTO) {
        return stockDAO.updateStock(stockDTO);
    }
}
