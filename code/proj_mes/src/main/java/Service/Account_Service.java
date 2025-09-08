package Service;

import Dao.Account_DAO;
import Dto.Account_DTO;
import util.PasswordUtil;
import util.CryptoUtil;

public class Account_Service {
    private final Account_DAO dao = new Account_DAO();

    /**
     * 계정 생성
     * @param dto      worker_id, worker_name, worker_email, worker_phone, worker_grade, worker_cando, dapart_ID2 세팅
     * @param rawPw    평문 비밀번호
     * @param rrnPlain 주민번호 평문(6+7자리 합친 13자리, 없으면 null/빈문자)
     */
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
}
