package Service;

import java.util.List;

import Dao.ItemDAO;
import Dto.ItemDTO;

public class ItemService {

	ItemDAO itemDAO = new ItemDAO();
	
	public List getAllItem() {
		
		return itemDAO.selectAll();
	}
	
	public ItemDTO getOneItem(ItemDTO itemDTO) {
		ItemDTO dto = itemDAO.selectOneItem(itemDTO);
		return dto; 
	}
	
	public int removeItem(ItemDTO itemDTO) {
		return itemDAO.deleteItem(itemDTO);
	}
	
	public int addItem(ItemDTO itemDTO) {
		return itemDAO.insertItem(itemDTO);
	}
	
	public int editItem(ItemDTO itemDTO) {
		return itemDAO.updateItem(itemDTO);
	}
}
