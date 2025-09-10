package Service;

import java.util.List;

import Dao.StockDAO;
import Dto.StockDTO;

public class StockService {

	StockDAO stockDAO = new StockDAO();
	
	public List getAllStock() {
		
		return stockDAO.selectAll();
	}
	
	public StockDTO getOneStock(StockDTO stockDTO) {
		StockDTO dto = stockDAO.selectOneStock(stockDTO);
		return dto; 
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
