package Service;

import java.util.List;

import Dao.ClientDAO;
import Dto.ClientDTO;

public class Client_Service {

    private final ClientDAO clientDAO = new ClientDAO();

    public List<ClientDTO> getAllClient() {
        return clientDAO.selectAll();
    }

    public int insert(ClientDTO d) {
        return clientDAO.insert(d);
    }

    /** 실제 DB에서 여러 건 삭제 */
    public int deleteByIds(List<String> ids) {
        return clientDAO.deleteByIds(ids);
    }

    /** 단건 삭제가 필요하면 이 메서드도 사용 가능 */
    public int deleteById(String id) {
        return clientDAO.deleteById(id);
    }
}
