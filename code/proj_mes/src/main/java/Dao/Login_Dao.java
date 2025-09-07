// package Dao;
package Dao;

import java.sql.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.Login_Dto;

public class Login_Dao {

    private Connection getConn() throws Exception {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/oracle");
        return ds.getConnection();
    }

    public Login_Dto findByIdAndPw(String id, String pw) {
        String inId = id == null ? null : id.trim();
        String inPw = pw == null ? null : pw.trim();

        String sqlMain =
            "SELECT WORKER_ID, WORKER_GRADE " +
            "  FROM WORKER " +
            " WHERE WORKER_ID = ? " +
            "   AND WORKER_PW  = ?";

        // 진단용: ID 존재 여부/공백 여부 확인
        String sqlCheckId =
            "SELECT TO_CHAR(WORKER_ID) wid, LENGTH(WORKER_PW) pw_len, " +
            "       CASE WHEN WORKER_PW IS NULL THEN 'Y' ELSE 'N' END pw_is_null " +
            "  FROM WORKER WHERE TO_CHAR(WORKER_ID) = ?";

        // 진단용: 공백/타입 이슈 회피(성능↓이므로 임시)
        String sqlFallback =
            "SELECT WORKER_ID, WORKER_GRADE " +
            "  FROM WORKER " +
            " WHERE TO_CHAR(WORKER_ID) = ? " +
            "   AND TRIM(WORKER_PW)    = ?";

        try (Connection con = getConn()) {

            // 0) 접속 DB/스키마 확인
            try (Statement st = con.createStatement();
                 ResultSet env = st.executeQuery(
                   "SELECT SYS_CONTEXT('USERENV','DB_NAME') db, " +
                   "       SYS_CONTEXT('USERENV','CURRENT_SCHEMA') sch FROM dual")) {
                if (env.next()) {
                    System.out.println("[LOGIN] DB=" + env.getString("db") +
                                       ", SCHEMA=" + env.getString("sch"));
                }
            }

            // 1) 정상 경로 시도
            try (PreparedStatement ps = con.prepareStatement(sqlMain)) {
                ps.setString(1, inId);
                ps.setString(2, inPw);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Login_Dto(rs.getString("WORKER_ID"), rs.getInt("WORKER_GRADE"));
                    }
                }
            }

            // 2) 진단: ID는 있는가?
            boolean idExists = false;
            try (PreparedStatement ps = con.prepareStatement(sqlCheckId)) {
                ps.setString(1, inId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idExists = true;
                        System.out.println("[LOGIN] ID exists: " + rs.getString("wid") +
                                           ", PW length in DB=" + rs.getInt("pw_len") +
                                           ", PW is null? " + rs.getString("pw_is_null"));
                    } else {
                        System.out.println("[LOGIN] ID NOT FOUND by TO_CHAR match: " + inId);
                    }
                }
            }

            // 3) 진단/임시 우회: 타입/공백 이슈 확인
            try (PreparedStatement ps = con.prepareStatement(sqlFallback)) {
                ps.setString(1, inId);
                ps.setString(2, inPw);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("[LOGIN] FALLBACK matched (type/whitespace issue suspected)");
                        return new Login_Dto(rs.getString("WORKER_ID"), rs.getInt("WORKER_GRADE"));
                    }
                }
            }

            if (!idExists) {
                System.out.println("[LOGIN] 결과: 아이디가 DB에 없음");
            } else {
                System.out.println("[LOGIN] 결과: 아이디는 있으나 비밀번호 불일치 (또는 공백/타입 문제)");
            }

        } catch (Exception e) {
            // 운영에선 로거 사용 권장
            e.printStackTrace();
        }
        return null;
    }
}
