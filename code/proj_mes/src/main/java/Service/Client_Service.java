package Service;

import java.util.List;
import java.util.Objects;

import Dao.ClientDAO;
import Dto.ClientDTO;

public class Client_Service {

    private final ClientDAO clientDAO = new ClientDAO();

    /** 전체 조회 */
    public List<ClientDTO> getAllClient() {
        return clientDAO.selectAll();
    }

    /** 신규 등록 */
    public int insert(ClientDTO d) {
        Objects.requireNonNull(d, "ClientDTO must not be null");
        return clientDAO.insert(d);
    }

    /** 수정 (client_id 기준 업데이트) */
    public int update(ClientDTO d) {
        Objects.requireNonNull(d, "ClientDTO must not be null");
        if (d.getClient_id() == null || d.getClient_id().trim().isEmpty()) {
            // client_id 없으면 업데이트할 수 없음
            return 0;
        }
        return clientDAO.update(d); // ← ClientDAO에 update(...)가 구현되어 있어야 합니다.
    }

    /** 여러 건 삭제 */
    public int deleteByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return clientDAO.deleteByIds(ids);
    }

    /** 단건 삭제 */
    public int deleteById(String id) {
        if (id == null || id.trim().isEmpty()) return 0;
        return clientDAO.deleteById(id);
    }
}
