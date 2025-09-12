package Dao;

import java.sql.*;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.StockDTO;

public class StockDAO {

    // ========= DB 연결 =========
    private Connection getConn() throws SQLException {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
            return ds.getConnection();
        } catch (Exception e) {
            throw new SQLException("DB 커넥션 실패", e);
        }
    }

    // ========= 목록 =========
    // Service.getAllStock() 에서 호출
    public List<StockDTO> selectAll() throws Exception {
        String sql =
            "SELECT s.stock_id, s.stock_date, s.stock_loc, s.stock_div, s.stock_stat, " +
            "       s.stock_number, s.item_code, NVL(i.item_name, s.item_code) AS item_name " +
            "FROM stock s " +
            "LEFT JOIN item i ON i.item_code = s.item_code " +
            "ORDER BY s.stock_date DESC, s.stock_id DESC";

        List<StockDTO> list = new ArrayList<>();
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StockDTO d = new StockDTO();
                d.setStock_id(rs.getString("stock_id"));
                d.setStock_date(rs.getDate("stock_date"));
                d.setStock_loc(rs.getInt("stock_loc"));
                d.setStock_div(rs.getInt("stock_div"));
                d.setStock_stat(rs.getInt("stock_stat"));
                d.setStock_number(rs.getInt("stock_number"));
                d.setItem_code(rs.getString("item_code"));
                // 파생표시용
                try { d.setItem_name(rs.getString("item_name")); } catch (Throwable ignore) {}
                list.add(d);
            }
        }
        return list;
    }

    // ========= 단건 =========
    // Service.getOneStock(dto) 에서 호출 (dto.stock_id 사용)
 // StockDAO.java
    public StockDTO selectOneStock(String stockId) throws Exception {
        String sql =
            "SELECT " +
            "  s.stock_id, s.stock_date, s.stock_loc, s.stock_div, s.stock_number, s.item_code, " +
            "  i.item_name, i.item_price " +            // ← 단가/품명은 ITEM 테이블에서
            "FROM stock s " +
            "LEFT JOIN ITEM i ON i.item_code = s.item_code " +  // ← 실제 테이블명이 'ITEMS'라면 ITEMS로 바꾸세요
            "WHERE TRIM(s.stock_id) = TRIM(?)";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                StockDTO dto = new StockDTO();
                dto.setStock_id(rs.getString("stock_id"));
                dto.setStock_date(rs.getDate("stock_date"));
                dto.setStock_loc(rs.getInt("stock_loc"));
                dto.setStock_div(rs.getInt("stock_div"));
                dto.setStock_number(rs.getInt("stock_number"));
                dto.setItem_code(rs.getString("item_code"));

                // ← ITEM 테이블에서 가져온 값들
                dto.setItem_name(rs.getString("item_name"));
                // item_price 컬럼이 NUMBER면 getInt/Long, VARCHAR2면 문자열 파싱하세요
                dto.setItem_price(rs.getString("item_price"));

                return dto;
            }
        }
    }


    // ========= 등록 =========
    // Service.addStock(dto) 에서 호출
    // 요구사항: stock_id = item_code + 4자리 (시퀀스 기반), stock_date = SYSDATE
    public int insertStock(StockDTO dto) throws Exception {
        if (dto == null || isBlank(dto.getItem_code())) {
            throw new IllegalArgumentException("item_code 필요");
        }

        // 4자리 suffix 생성 (0000~9999)
        final String seqSql = "SELECT LPAD(MOD(stock_seq.NEXTVAL, 10000), 4, '0') AS suf FROM dual";

        final String insSql =
            "INSERT INTO stock " +
            " (stock_id, stock_date, stock_loc, stock_div, stock_stat, stock_number, item_code) " +
            "VALUES (?, SYSDATE, ?, ?, ?, ?, ?)";

        try (Connection con = getConn();
             PreparedStatement psSeq = con.prepareStatement(seqSql);
             ResultSet rs = psSeq.executeQuery()) {

            if (!rs.next()) throw new SQLException("시퀀스 조회 실패");
            String suffix = rs.getString("suf"); // "0042" 같은 4자리
            String stockId = dto.getItem_code().trim() + suffix;

            try (PreparedStatement ps = con.prepareStatement(insSql)) {
                ps.setString(1, stockId);
                ps.setInt(2, nz(dto.getStock_loc()));
                ps.setInt(3, nz(dto.getStock_div()));
                ps.setInt(4, nz(dto.getStock_stat()));
                ps.setInt(5, nz(dto.getStock_number()));
                ps.setString(6, dto.getItem_code().trim());
                int r = ps.executeUpdate();
                // 호출자도 새 ID를 알 수 있게 DTO에 되돌려둠
                try { dto.setStock_id(stockId); } catch (Throwable ignore) {}
                return r;
            }
        }
    }

    // ========= 수정 =========
    // Service.editStock(dto) 에서 호출 (dto.stock_id 필요)
    public int updateStock(StockDTO dto) throws Exception {
        String sql =
            "UPDATE stock SET " +
            "  stock_loc = ?, stock_div = ?, stock_stat = ?, stock_number = ?, item_code = ? " +
            "WHERE TRIM(stock_id) = TRIM(?)";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nz(dto.getStock_loc()));
            ps.setInt(2, nz(dto.getStock_div()));
            ps.setInt(3, nz(dto.getStock_stat()));
            ps.setInt(4, nz(dto.getStock_number()));
            ps.setString(5, nvl(dto.getItem_code()));
            ps.setString(6, nvl(dto.getStock_id()));
            return ps.executeUpdate();
        }
    }

    // ========= 삭제 =========
    // Service.removeStock(dto) 에서 호출 (dto.stock_id 사용)
    public int deleteStock(StockDTO dto) throws Exception {
        String sql = "DELETE FROM stock WHERE TRIM(stock_id) = TRIM(?)";
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dto.getStock_id());
            return ps.executeUpdate();
        }
    }

    // ========= (선택) 분류/검색용 메서드들 =========
    // 네 파일에 이미 있다면 그대로 두세요. 필요 시 여기 유지/추가.

    // ========= 유틸 =========
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
    private static String nvl(String s){ return s == null ? "" : s.trim(); }
    private static int nz(Integer i){ return i == null ? 0 : i; }
}
