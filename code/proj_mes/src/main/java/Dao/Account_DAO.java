package Dao;

import Dto.Account_DTO;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class Account_DAO {
    private final DataSource ds;

    public Account_DAO() {
        try {
            this.ds = (DataSource) new InitialContext()
                .lookup("java:comp/env/jdbc/oracle");  // ← 여기!
        } catch (NamingException e) {
            throw new RuntimeException("DataSource lookup failed: jdbc/oracle", e);
        }
    }
    public boolean existsById(String workerId) throws SQLException {
        final String sql = "SELECT 1 FROM worker WHERE worker_id = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, workerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        final String sql = "SELECT 1 FROM worker WHERE worker_email = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int insert(Account_DTO d) throws SQLException {
        final String sql =
            "INSERT INTO worker " +
            "(worker_id, worker_name, worker_grade, worker_email, worker_phone, " +
            " worker_bacode, worker_cando, worker_pw, dapart_ID2) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, d.getWorkerId());
            ps.setString(i++, d.getWorkerName());
            ps.setInt(i++,    d.getWorkerGrade());
            ps.setString(i++, d.getWorkerEmail());
            ps.setString(i++, d.getWorkerPhone());
            ps.setString(i++, d.getWorkerBacode());
            ps.setString(i++,    d.getWorkerCando());  
            ps.setString(i++, d.getWorkerPwHash());  // 해시 저장
            ps.setString(i++, d.getDapartId2());
            return ps.executeUpdate();
        }
    }
}
