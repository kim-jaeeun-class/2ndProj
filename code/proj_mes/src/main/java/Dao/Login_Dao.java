package Dao;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

/**
 * 로그인에 필요한 최소 컬럼만 조회/갱신하는 DAO.
 * JNDI DataSource 이름은 "java:comp/env/jdbc/oracle" 을 사용합니다.
 */
public class Login_Dao {
    private final DataSource ds;

    public Login_Dao() {
        try {
            this.ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/oracle");
        } catch (NamingException e) {
            throw new RuntimeException("DataSource lookup failed: jdbc/oracle", e);
        }
    }

    /**
     * 아이디로 인증에 필요한 행을 조회합니다.
     * @param workerId 사용자 ID
     * @return 없으면 null
     */
    public AuthRow findAuthById(String workerId) throws SQLException {
        final String sql =
            "SELECT worker_id, worker_grade, worker_pw, worker_cando " +
            "  FROM worker " +
            " WHERE worker_id = ?";
        try (Connection con = ds.getConnection();
        	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, workerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                AuthRow r = new AuthRow();
                r.setWorkerId(rs.getString("worker_id"));
                r.setWorkerGrade(rs.getInt("worker_grade"));
                r.setPwHash(rs.getString("worker_pw"));        // 해시 또는 평문(특정 계정)
                r.setWorkerCando(rs.getString("worker_cando"));
                return r;
            }
        }
    }

    /**
     * 비밀번호 해시를 갱신합니다(정책 업그레이드 시 사용).
     */
    public int updatePasswordHash(String workerId, String newHash) throws SQLException {
        final String sql = "UPDATE worker SET worker_pw = ? WHERE worker_id = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, workerId);
            return ps.executeUpdate();
        }
    }

    /** 조회용 DTO */
    public static class AuthRow {
        private String workerId;
        private int workerGrade;
        private String pwHash;
        private String workerCando;

        public String getWorkerId() { return workerId; }
        public void setWorkerId(String workerId) { this.workerId = workerId; }

        public int getWorkerGrade() { return workerGrade; }
        public void setWorkerGrade(int workerGrade) { this.workerGrade = workerGrade; }

        public String getPwHash() { return pwHash; }
        public void setPwHash(String pwHash) { this.pwHash = pwHash; }

        public String getWorkerCando() { return workerCando; }
        public void setWorkerCando(String workerCando) { this.workerCando = workerCando; }
    }
}
