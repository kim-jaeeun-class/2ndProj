package Service;

import Dao.Login_Dao;
import Dto.Login_Dto;
import util.PasswordUtil;

public class Login_Service {
    private final Login_Dao dao = new Login_Dao();

    /** 성공 시 세션에 넣을 Login_Dto 반환, 실패 시 null */
    public Login_Dto login(String id, String rawPw) {
        try {
            Login_Dao.AuthRow row = dao.findAuthById(id);
            if (row == null) return null;

            boolean ok;
            // 특정 계정은 평문 비교
            if ("20181490".equals(id) || "20250905".equals(id)) {
                ok = rawPw != null && rawPw.equals(row.getPwHash());  
            } else {
                ok = PasswordUtil.verify(rawPw, row.getPwHash());     
                if (ok && PasswordUtil.needsRehash(row.getPwHash())) { 
                    try {
                        dao.updatePasswordHash(row.getWorkerId(), PasswordUtil.hash(rawPw)); 
                    } catch (Exception ignore) {}
                }
            }
            if (!ok) return null;

            return new Login_Dto(row.getWorkerId(), row.getWorkerGrade()); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
