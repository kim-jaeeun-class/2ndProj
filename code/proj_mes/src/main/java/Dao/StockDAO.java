package Dao;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.StockDTO;

public class StockDAO {

    // JNDI 연결
    private Connection getConn() throws Exception {
        Context ctx = new InitialContext();
        DataSource dataFactory = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
        return dataFactory.getConnection();
    }

    /** 목록 + ITEM 조인 + 파생 카테고리 */
    public List<StockDTO> stock_list_menu() {
        List<StockDTO> list = new ArrayList<>();
        final String sql =
            "SELECT s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, " +
            "       i.ITEM_CODE, i.ITEM_NAME, i.ITEM_PRICE, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=2 THEN SUBSTR(i.ITEM_CODE,1,2) END AS BIG_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=4 THEN SUBSTR(i.ITEM_CODE,3,2) END AS MID_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=6 THEN SUBSTR(i.ITEM_CODE,5,2) END AS SMALL_CATEGORY " +
            "  FROM STOCK s JOIN ITEM i ON s.ITEM_CODE = i.ITEM_CODE";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** 대/중/소로만 검색 */
    public List<StockDTO> findStocks(String big, String mid, String small) {
        return searchStocks(big, mid, small, null, null);
    }

    /** 대/중/소 + 기간 검색 */
    public List<StockDTO> searchStocks(String big, String mid, String small, Date from, Date to) {
        List<StockDTO> list = new ArrayList<>();

        String base =
            "SELECT s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, " +
            "       i.ITEM_CODE, i.ITEM_NAME, i.ITEM_PRICE, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=2 THEN SUBSTR(i.ITEM_CODE,1,2) END AS BIG_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=4 THEN SUBSTR(i.ITEM_CODE,3,2) END AS MID_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=6 THEN SUBSTR(i.ITEM_CODE,5,2) END AS SMALL_CATEGORY " +
            "  FROM STOCK s JOIN ITEM i ON s.ITEM_CODE = i.ITEM_CODE ";

        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (nz(big))   { where.append(" AND SUBSTR(i.ITEM_CODE,1,2) = ? "); params.add(big); }
        if (nz(mid))   { where.append(" AND SUBSTR(i.ITEM_CODE,3,2) = ? "); params.add(mid); }
        if (nz(small)) { where.append(" AND SUBSTR(i.ITEM_CODE,5,2) = ? "); params.add(small); }

        if (from != null && to != null) {
            where.append(" AND TRUNC(s.STOCK_DATE) BETWEEN ? AND ? ");
            params.add(from); params.add(to);
        } else if (from != null) {
            where.append(" AND TRUNC(s.STOCK_DATE) >= ? ");
            params.add(from);
        } else if (to != null) {
            where.append(" AND TRUNC(s.STOCK_DATE) <= ? ");
            params.add(to);
        }

        String sql = base + where + " ORDER BY s.STOCK_DATE DESC, s.STOCK_ID DESC";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = 1;
            for (Object p : params) {
                if (p instanceof Date) ps.setDate(idx++, (Date) p);
                else                   ps.setString(idx++, p.toString());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** 전건 조회 */
    public List<StockDTO> selectAll() {
        List<StockDTO> list = new ArrayList<>();
        final String sql =
            "SELECT s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, " +
            "       i.ITEM_CODE, i.ITEM_NAME, i.ITEM_PRICE, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=2 THEN SUBSTR(i.ITEM_CODE,1,2) END AS BIG_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=4 THEN SUBSTR(i.ITEM_CODE,3,2) END AS MID_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=6 THEN SUBSTR(i.ITEM_CODE,5,2) END AS SMALL_CATEGORY " +
            "  FROM STOCK s JOIN ITEM i ON s.ITEM_CODE = i.ITEM_CODE";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** 상세 */
    public StockDTO selectOneStock(StockDTO stockDTO) {
        StockDTO resultDTO = null;
        final String sql =
            "SELECT s.STOCK_ID, s.STOCK_DATE, s.STOCK_LOC, s.STOCK_DIV, s.STOCK_STAT, s.STOCK_NUMBER, " +
            "       i.ITEM_CODE, i.ITEM_NAME, i.ITEM_PRICE, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=2 THEN SUBSTR(i.ITEM_CODE,1,2) END AS BIG_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=4 THEN SUBSTR(i.ITEM_CODE,3,2) END AS MID_CATEGORY, " +
            "       CASE WHEN LENGTH(i.ITEM_CODE)>=6 THEN SUBSTR(i.ITEM_CODE,5,2) END AS SMALL_CATEGORY " +
            "  FROM STOCK s JOIN ITEM i ON s.ITEM_CODE = i.ITEM_CODE " +
            " WHERE s.STOCK_ID = ?";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, stockDTO.getStock_id());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) resultDTO = mapRow(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return resultDTO;
    }

    /** 삭제 */
    public int deleteStock(StockDTO stockDTO) {
        final String sql = "DELETE FROM STOCK WHERE STOCK_ID = ?";
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, stockDTO.getStock_id());
            return ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** 등록 */
    public int insertStock(StockDTO stockDTO) {
        final String sql =
            "INSERT INTO STOCK (STOCK_ID, STOCK_DATE, STOCK_LOC, STOCK_DIV, STOCK_STAT, STOCK_NUMBER, ITEM_CODE) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, stockDTO.getStock_id());
            ps.setDate(2, stockDTO.getStock_date());
            ps.setInt(3, stockDTO.getStock_loc());
            ps.setInt(4, stockDTO.getStock_div());
            ps.setInt(5, stockDTO.getStock_stat());
            ps.setInt(6, stockDTO.getStock_number());
            ps.setString(7, stockDTO.getItem_code());
            return ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** 수정 */
    public int updateStock(StockDTO stockDTO) {
        final String sql =
            "UPDATE STOCK " +
            "   SET STOCK_DATE = ?, STOCK_LOC = ?, STOCK_DIV = ?, STOCK_STAT = ?, " +
            "       STOCK_NUMBER = ?, ITEM_CODE = ? " +
            " WHERE STOCK_ID = ?";
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, stockDTO.getStock_date());
            ps.setInt(2, stockDTO.getStock_loc());
            ps.setInt(3, stockDTO.getStock_div());
            ps.setInt(4, stockDTO.getStock_stat());
            ps.setInt(5, stockDTO.getStock_number());
            ps.setString(6, stockDTO.getItem_code());
            ps.setString(7, stockDTO.getStock_id());
            return ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // ---- 내부 유틸 ----
    private static boolean nz(String s) { return s != null && !s.isEmpty(); }

    private static StockDTO mapRow(ResultSet rs) throws SQLException {
        StockDTO dto = new StockDTO();
        dto.setStock_id(rs.getString("STOCK_ID"));
        dto.setStock_date(rs.getDate("STOCK_DATE"));
        dto.setStock_loc(rs.getInt("STOCK_LOC"));
        dto.setStock_div(rs.getInt("STOCK_DIV"));
        dto.setStock_stat(rs.getInt("STOCK_STAT"));
        dto.setStock_number(rs.getInt("STOCK_NUMBER"));
        dto.setItem_code(rs.getString("ITEM_CODE"));
        dto.setItem_name(rs.getString("ITEM_NAME"));
        dto.setItem_price(rs.getString("ITEM_PRICE"));
        dto.setBigCategory(rs.getString("BIG_CATEGORY"));
        dto.setMidCategory(rs.getString("MID_CATEGORY"));
        dto.setSmallCategory(rs.getString("SMALL_CATEGORY"));
        return dto;
    }
}
