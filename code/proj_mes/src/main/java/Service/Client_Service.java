package Service;

import java.util.List;

import Dao.ClientDAO;
import Dto.ClientDTO;

public class Client_Service {

    private final ClientDAO clientDAO = new ClientDAO();

    public List<ClientDTO> getAllItem() {
        return clientDAO.selectAll();
    }

    public int insert(ClientDTO d) {
        return clientDAO.insert(d);
    }
}
