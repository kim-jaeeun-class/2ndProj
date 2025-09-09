package Dao;

import Dto.Account_DTO;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class Account_DAO {
    private final DataSource ds;

    public Account_DAO() {
        try {
            this.ds = (DataSource) new InitialContext()
                .lookup("java:comp/env/jdbc/oracle"); 
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
            ps.setString(i++, d.getWorkerCando());
            ps.setString(i++, d.getWorkerPwHash());  // 해시 저장
            ps.setString(i++, d.getDapartId2());
            return ps.executeUpdate();
        }
    }

    // ====== 목록/검색/조회/수정/삭제 ======
    public List<Account_DTO> findAll() throws SQLException {
        final String sql =
            "SELECT worker_id, worker_name, worker_grade, worker_email, worker_phone, " +
            "       worker_bacode, worker_cando, worker_pw, dapart_ID2 " +
            "  FROM worker ORDER BY worker_id";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Account_DTO> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    public List<Account_DTO> search(String q) throws SQLException {
        if (q == null) q = "";
        final String like = "%" + q.toLowerCase(Locale.ROOT) + "%";
        final String sql =
            "SELECT worker_id, worker_name, worker_grade, worker_email, worker_phone, " +
            "       worker_bacode, worker_cando, worker_pw, dapart_ID2 " +
            "  FROM worker " +
            " WHERE LOWER(worker_id)   LIKE ? " +
            "    OR LOWER(worker_name) LIKE ? " +
            "    OR LOWER(dapart_ID2)  LIKE ? " +
            "    OR LOWER(TO_CHAR(worker_grade)) LIKE ? " +   // ★ 직급 포함
            " ORDER BY worker_id";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                List<Account_DTO> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }


    public Optional<Account_DTO> findById(String workerId) throws SQLException {
        final String sql =
            "SELECT worker_id, worker_name, worker_grade, worker_email, worker_phone, " +
            "       worker_bacode, worker_cando, worker_pw, dapart_ID2 " +
            "  FROM worker WHERE worker_id = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, workerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        }
    }

    public int update(Account_DTO d) throws SQLException {
        final String sql =
            "UPDATE worker " +
            "   SET worker_name=?, worker_grade=?, worker_email=?, worker_phone=?, " +
            "       worker_bacode=?, worker_cando=?, worker_pw=?, dapart_ID2=? " +
            " WHERE worker_id=?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, d.getWorkerName());
            ps.setInt(i++,    d.getWorkerGrade());
            ps.setString(i++, d.getWorkerEmail());
            ps.setString(i++, d.getWorkerPhone());
            ps.setString(i++, d.getWorkerBacode());
            ps.setString(i++, d.getWorkerCando());
            ps.setString(i++, d.getWorkerPwHash());
            ps.setString(i++, d.getDapartId2());
            ps.setString(i++, d.getWorkerId());
            return ps.executeUpdate();
        }
    }

    public int updateDeptAndRole(String workerId, String dapartId2, String workerCando) throws SQLException {
        final String sql = "UPDATE worker SET dapart_ID2=?, worker_cando=? WHERE worker_id=?";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dapartId2);
            ps.setString(2, workerCando);
            ps.setString(3, workerId);
            return ps.executeUpdate();
        }
    }

    public int updateRoleBulk(List<String> ids, String role) throws SQLException {
        if (ids == null || ids.isEmpty()) return 0;
        final String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        final String sql = "UPDATE worker SET worker_cando=? WHERE worker_id IN (" + placeholders + ")";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1; ps.setString(idx++, role);
            for (String id : ids) ps.setString(idx++, id);
            return ps.executeUpdate();
        }
    }

    public int deleteByIds(List<String> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return 0;
        final String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        final String sql = "DELETE FROM worker WHERE worker_id IN (" + placeholders + ")";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int idx = 1;
            for (String id : ids) ps.setString(idx++, id);
            return ps.executeUpdate();
        }
    }


    private Account_DTO map(ResultSet rs) throws SQLException {
        Account_DTO w = new Account_DTO();
        w.setWorkerId(rs.getString("worker_id"));
        w.setWorkerName(rs.getString("worker_name"));
        w.setWorkerGrade(rs.getInt("worker_grade")); 
        w.setWorkerEmail(rs.getString("worker_email"));
        w.setWorkerPhone(rs.getString("worker_phone"));
        w.setWorkerBacode(rs.getString("worker_bacode"));
        w.setWorkerCando(rs.getString("worker_cando"));
        w.setWorkerPwHash(rs.getString("worker_pw"));
        w.setDapartId2(rs.getString("dapart_ID2"));
        return w;
    }
}
