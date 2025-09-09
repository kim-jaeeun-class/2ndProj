package Service;

import java.util.List;

import Dao.StockDAO;
import Dto.StockDTO;

public class StockService {

	StockDAO stockDAO = new StockDAO();
	
	public List getAllStock() {
		
		return stockDAO.selectAll();
	}
	
	public StockDTO getOneEmp(StockDTO stockDTO) {
		StockDTO dto = stockDAO.selectOneStock(stockDTO);
		return dto; 
	}
	
	public int removeEmp(StockDTO stockDTO) {
		return stockDAO.deleteStock(stockDTO);
	}
	
	public int addEmp(StockDTO stockDTO) {
		return stockDAO.insertStock(stockDTO);
	}
	
	public int editEmp(StockDTO stockDTO) {
		return stockDAO.updateStock(stockDTO);
	}
}
