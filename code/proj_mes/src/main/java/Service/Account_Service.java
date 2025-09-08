package Service;

import Dao.Account_DAO;
import Dto.Account_DTO;
import util.PasswordUtil;
import util.CryptoUtil;

import java.util.*;
import java.sql.SQLException;

public class Account_Service {
    private final Account_DAO dao = new Account_DAO();


    public void create(Account_DTO dto, String rawPw, String rrnPlain) throws Exception {
        // 1) 기본 검증
        if (isBlank(dto.getWorkerId()))   throw new IllegalArgumentException("사번(worker_id)은 필수입니다.");
        if (isBlank(dto.getWorkerName())) throw new IllegalArgumentException("이름(worker_name)은 필수입니다.");
        if (isBlank(rawPw))               throw new IllegalArgumentException("비밀번호는 필수입니다.");

        // 2) 주민번호 암호화(AES-GCM) — 저장할 경우에만
        if (!isBlank(rrnPlain)) {
            String onlyNum = rrnPlain.replaceAll("\\D", "");
            if (!onlyNum.matches("^\\d{13}$"))
                throw new IllegalArgumentException("주민번호는 숫자 13자리여야 합니다.");
            String rrnEnc = CryptoUtil.encrypt(onlyNum, dto.getWorkerId()); // AAD=worker_id
            dto.setWorkerBacode(rrnEnc);
        } else {
            dto.setWorkerBacode(null);
        }

        // 3) 이메일/전화 간단 검증
        if (!isBlank(dto.getWorkerEmail()) &&
            !dto.getWorkerEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");

        if (!isBlank(dto.getWorkerPhone())) {
            String digits = dto.getWorkerPhone().replaceAll("\\D", "");
            if (digits.length() >= 10) {
                if (digits.startsWith("02") && digits.length() == 10)
                    dto.setWorkerPhone(digits.replaceFirst("^(02)(\\d{4})(\\d{4})$", "$1-$2-$3"));
                else
                    dto.setWorkerPhone(digits.replaceFirst("^(\\d{3})(\\d{3,4})(\\d{4})$", "$1-$2-$3"));
            } else {
                dto.setWorkerPhone(digits);
            }
        }

        // 4) 중복 체크
        if (dao.existsById(dto.getWorkerId()))
            throw new IllegalStateException("이미 존재하는 사번입니다.");
        if (!isBlank(dto.getWorkerEmail()) && dao.existsByEmail(dto.getWorkerEmail()))
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");

        // 5) 비밀번호 해시(PBKDF2)
        dto.setWorkerPwHash(PasswordUtil.hash(rawPw));

        // 6) INSERT
        int rows = dao.insert(dto);
        if (rows != 1) throw new IllegalStateException("계정 생성에 실패했습니다.");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }


    public List<Account_DTO> listAll() throws SQLException { return dao.findAll(); }

    public List<Account_DTO> search(String q) throws SQLException {
        return (q == null || q.isBlank()) ? dao.findAll() : dao.search(q);
    }

    public Optional<Account_DTO> get(String workerId) throws SQLException {
        return dao.findById(workerId);
    }

    public void updateDeptAndRole(String workerId, String dapartId2, String workerCando) throws SQLException {
        if (isBlank(workerId))   throw new IllegalArgumentException("사번은 필수입니다.");
        if (isBlank(dapartId2))  throw new IllegalArgumentException("부서ID는 필수입니다.");
        if (isBlank(workerCando))throw new IllegalArgumentException("권한은 필수입니다.");
        int n = dao.updateDeptAndRole(workerId, dapartId2, workerCando);
        if (n != 1) throw new IllegalStateException("수정 대상이 없거나 수정 실패");
    }

    public int updateRoleBulk(List<String> ids, String role) throws SQLException {
        if (ids == null || ids.isEmpty()) return 0;
        if (isBlank(role)) throw new IllegalArgumentException("권한은 필수입니다.");
        return dao.updateRoleBulk(ids, role);
    }

    public int deleteByIds(List<String> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return 0;
        return dao.deleteByIds(ids);
    }
}
