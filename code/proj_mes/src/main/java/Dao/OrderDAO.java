package Dao;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.OrderDTO;
import Dto.OrderDetDTO;

public class OrderDAO {

  // ===== 1) 커넥션 (JNDI) =====
  private Connection getConn() throws SQLException {
    try {
      Context ctx = new InitialContext();
      DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");
      return ds.getConnection();
    } catch (Exception e) {
      throw new SQLException("DB 커넥션 획득 실패", e);
    }
  }

  // ===== 유틸 =====
  private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
  private static String safeStr(String s){ return s==null ? "" : s.trim(); }
  private static String enforceLen(String s, int max){
    if (s == null) return null;
    String v = s.trim();
    return v.length() <= max ? v : v.substring(0, max);
  }


/** 목록(총수량/총금액 포함) — item_price는 문자열이라 숫자 변환 */
public List<OrderDTO> selectAllWithSummary() throws Exception {
  String sql =
    "SELECT o.order_key, o.order_number, o.order_date, o.order_state, " +
    "       d.depart_level, w.worker_name, " +
    "       COUNT(od.item_code) AS totalQty, " +
    "       NVL(SUM( NVL(od.quantity,0) * NVL(TO_NUMBER(REGEXP_REPLACE(od.item_price,'[^0-9.]','')),0) ), 0) AS totalAmt " +
    "FROM orders o " +
    "LEFT JOIN department d ON o.dapart_ID2 = d.dapart_ID2 " +
    "LEFT JOIN worker     w ON o.worker_id   = w.worker_id " +
    "LEFT JOIN order_detail od ON TRIM(od.order_key) = TRIM(o.order_key) " +
    "GROUP BY o.order_key, o.order_number, o.order_date, o.order_state, d.depart_level, w.worker_name " +
    "ORDER BY o.order_date DESC, o.order_key DESC";

  List<OrderDTO> list = new ArrayList<>();
  try (Connection con = getConn();
       PreparedStatement ps = con.prepareStatement(sql);
       ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {
      OrderDTO dto = new OrderDTO();
      dto.setOrder_key(rs.getString("order_key"));
      dto.setOrder_number(rs.getString("order_number"));
      dto.setOrder_date(rs.getDate("order_date"));
      dto.setOrder_state(rs.getInt("order_state"));
      dto.setDepart_level(rs.getString("depart_level"));
      dto.setWorker_name(rs.getString("worker_name"));
      dto.setTotalQty(String.valueOf(rs.getLong("totalQty")));
      dto.setTotalAmt(String.valueOf(rs.getLong("totalAmt")));
      list.add(dto);
    }
  }
  return list;
}
/** 상세 헤더 1건 — order_key 기준 */
public OrderDTO selectOneByKey(String orderKey) throws Exception {
  String sql =
    "SELECT o.order_key, o.order_number, o.order_date, o.order_pay, o.order_state, o.bigo, " +
    "       o.client_id, c.client_name, c.business_number, c.client_phone, " +
    "       o.dapart_ID2, d.depart_level, o.worker_id, w.worker_name " +
    "FROM orders o " +
    "LEFT JOIN department d ON o.dapart_ID2 = d.dapart_ID2 " +
    "LEFT JOIN worker     w ON o.worker_id   = w.worker_id " +
    "LEFT JOIN client     c ON o.client_id   = c.client_id " +
    "WHERE TRIM(o.order_key) = TRIM(?)";

  try (Connection con = getConn();
       PreparedStatement ps = con.prepareStatement(sql)) {
    ps.setString(1, orderKey);
    try (ResultSet rs = ps.executeQuery()) {
    	
      if (!rs.next()) return null;
      OrderDTO dto = new OrderDTO();
      dto.setOrder_key(rs.getString("order_key"));
      dto.setOrder_number(rs.getString("order_number"));
      dto.setOrder_date(rs.getDate("order_date"));
      dto.setOrder_pay(rs.getDate("order_pay"));         
      dto.setOrder_state(rs.getInt("order_state"));
      dto.setBigo(rs.getString("bigo"));
      dto.setClient_id(rs.getString("client_id"));
      dto.setClient_name(rs.getString("client_name"));
      dto.setBusiness_number(rs.getString("business_number"));
      dto.setClient_phone(rs.getString("client_phone"));
      dto.setDapart_ID2(rs.getString("dapart_ID2"));
      dto.setDepart_level(rs.getString("depart_level"));
      dto.setWorker_id(rs.getString("worker_id"));
      dto.setWorker_name(rs.getString("worker_name"));
      
      return dto;
    }
  }
}
/** 상세 품목 리스트 — order_key 기준 */
public List<OrderDetDTO> selectOrderItemsByKey(String orderKey) throws Exception {
  String sql =
    "SELECT item_code, item_price, quantity " +
    "FROM order_detail " +
    "WHERE TRIM(order_key) = TRIM(?) " +
    "ORDER BY rowid";

  List<OrderDetDTO> list = new ArrayList<>();
  try (Connection con = getConn();
       PreparedStatement ps = con.prepareStatement(sql)) {
    ps.setString(1, orderKey);
    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        OrderDetDTO d = new OrderDetDTO();
        d.setItem_code(rs.getString("item_code"));
        d.setItem_price(rs.getString("item_price")); // 문자열 그대로 저장
        d.setQuantity(rs.getInt("quantity"));
        list.add(d);
      }
    }
  }
  return list;
}
/** 신규 등록: 헤더 + 상세 일괄 저장 (트랜잭션) — 반환: 생성된 order_key */
public String insertOrderDet(OrderDTO order, List<OrderDetDTO> details) throws Exception {
  String seqSql = "SELECT order_seq.NEXTVAL FROM dual";
  String insOrderSql =
    "INSERT INTO orders " +
    " (order_key, order_number, order_date, order_pay, order_state, client_id, worker_id, dapart_ID2, bigo) " +
    " VALUES (?, ?, SYSDATE, ?, ?, ?, ?, ?, ?)";
  String insDtlSql =
    "INSERT INTO order_detail (order_key, item_code, item_price, quantity) VALUES (?, ?, ?, ?)";

  try (Connection conn = getConn()) {
    conn.setAutoCommit(false);

    long nextVal;
    try (PreparedStatement psSeq = conn.prepareStatement(seqSql);
         ResultSet rs = psSeq.executeQuery()) {
      if (!rs.next()) throw new SQLException("시퀀스 조회 실패");
      nextVal = rs.getLong(1);
    }

    String today = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    String seq2  = String.format("%02d", nextVal % 100);
    String orderKey    = "A" + today + seq2; // PK
    String orderNumber = "B" + today + seq2; // 발주번호

    // 헤더 저장
    try (PreparedStatement ps = conn.prepareStatement(insOrderSql)) {
      ps.setString(1, orderKey);
      ps.setString(2, orderNumber);
      ps.setDate  (3, order.getOrder_pay());   
      ps.setInt   (4, order.getOrder_state());
      ps.setString(5, order.getClient_id());
      ps.setString(6, order.getWorker_id());
      ps.setString(7, order.getDapart_ID2());
      ps.setString(8, safeStr(order.getBigo()));
      ps.executeUpdate();
    }

    // 상세 저장 (배치)
    if (details != null && !details.isEmpty()) {
      try (PreparedStatement ps = conn.prepareStatement(insDtlSql)) {
        for (OrderDetDTO d : details) {
          if (d == null || isBlank(d.getItem_code())) continue;
          ps.setString(1, orderKey);
          ps.setString(2, enforceLen(d.getItem_code(), 20));       // 방어(필요 시)
          ps.setString(3, safeStr(d.getItem_price()));             // "100,000"
          ps.setInt   (4, d.getQuantity());
          ps.addBatch();
        }
        ps.executeBatch();
      }
    }

    conn.commit();
    return orderKey;
  } catch (Exception e) {
    throw e;
  }
}


public void deleteOrdersCascade(List<String> orderKeys) throws Exception {
 String delDetail = "DELETE FROM order_detail WHERE order_key = ?";
 String delHeader = "DELETE FROM orders       WHERE order_key = ?";

 try (Connection conn = getConn();
      PreparedStatement psDet = conn.prepareStatement(delDetail);
      PreparedStatement psHdr = conn.prepareStatement(delHeader)) {

     conn.setAutoCommit(false);

     for (String k : orderKeys) {
         if (k == null || k.trim().isEmpty()) continue;
         String key = k.trim();

         // 상세 먼저
         psDet.setString(1, key);
         psDet.addBatch();

         // 헤더 나중
         psHdr.setString(1, key);
         psHdr.addBatch();
     }

     psDet.executeBatch();
     psHdr.executeBatch();
     conn.commit();
 }
}

/** 헤더 + 상세 전체 업데이트(트랜잭션) */
public void updateOrderFull(OrderDTO order, java.util.List<OrderDetDTO> details) throws Exception {
  String updHdr =
      "UPDATE orders SET " +
      "  order_pay = ?, order_state = ?, client_id = ?, worker_id = ?, dapart_ID2 = ?, bigo = ? " +
      "WHERE order_key = ?";
  String delDtl = "DELETE FROM order_detail WHERE order_key = ?";
  String insDtl = "INSERT INTO order_detail (order_key, item_code, item_price, quantity) VALUES (?, ?, ?, ?)";

  try (Connection conn = getConn();
       PreparedStatement psU = conn.prepareStatement(updHdr);
       PreparedStatement psD = conn.prepareStatement(delDtl);
       PreparedStatement psI = conn.prepareStatement(insDtl)) {

    conn.setAutoCommit(false);

    // 1) 헤더 업데이트
    psU.setDate  (1, order.getOrder_pay());                    
    psU.setInt   (2, order.getOrder_state());
    psU.setString(3, order.getClient_id());
    psU.setString(4, order.getWorker_id());
    psU.setString(5, order.getDapart_ID2());
    psU.setString(6, order.getBigo());
    psU.setString(7, order.getOrder_key());
    psU.executeUpdate();

    // 2) 상세 교체
    psD.setString(1, order.getOrder_key());
    psD.executeUpdate();

    if (details != null && !details.isEmpty()) {
      for (OrderDetDTO d : details) {
        if (d == null || d.getItem_code()==null || d.getItem_code().trim().isEmpty()) continue;
        psI.setString(1, order.getOrder_key());
        psI.setString(2, d.getItem_code().trim());
        psI.setString(3, d.getItem_price()==null ? "0" : d.getItem_price().trim());
        psI.setInt   (4, d.getQuantity());
        psI.addBatch();
      }
      psI.executeBatch();
    }

    conn.commit();
  }
}


/** 상세 전체 교체 — 기존 삭제 후 새로 삽입 (트랜잭션) */
public void replaceOrderDetails(String orderKey, List<OrderDetDTO> details) throws Exception {
  String delSql = "DELETE FROM order_detail WHERE order_key = ?";
  String insSql = "INSERT INTO order_detail (order_key, item_code, item_price, quantity) VALUES (?, ?, ?, ?)";

  try (Connection conn = getConn();
       PreparedStatement psDel = conn.prepareStatement(delSql);
       PreparedStatement psIns = conn.prepareStatement(insSql)) {

    conn.setAutoCommit(false);

    // 1) 기존 삭제
    psDel.setString(1, orderKey);
    psDel.executeUpdate();

    // 2) 신규 삽입(0건이면 ‘전부 삭제’ 상태가 됨)
    if (details != null) {
      for (OrderDetDTO d : details) {
        if (d == null || isBlank(d.getItem_code())) continue;
        psIns.setString(1, orderKey);
        psIns.setString(2, enforceLen(d.getItem_code(), 20));
        psIns.setString(3, safeStr(d.getItem_price()));
        psIns.setInt   (4, d.getQuantity());
        psIns.addBatch();
      }
      psIns.executeBatch();
    }

    conn.commit();
  }
}
/** 진행상태 변경 */
public int updateOrder(String orderKey, int newState) throws Exception {
  String sql = "UPDATE orders SET order_state = ? WHERE order_key = ?";
  try (Connection conn = getConn();
       PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setInt(1, newState);
    ps.setString(2, orderKey);
    return ps.executeUpdate();
  }
}
/** 단건 삭제 (상세→헤더) */
public int deleteOrder(String orderKey) throws Exception {
  String delDtl = "DELETE FROM order_detail WHERE order_key = ?";
  String delHdr = "DELETE FROM orders       WHERE order_key = ?";
  try (Connection conn = getConn();
       PreparedStatement psD = conn.prepareStatement(delDtl);
       PreparedStatement psH = conn.prepareStatement(delHdr)) {
    conn.setAutoCommit(false);

    psD.setString(1, orderKey);
    psD.executeUpdate();

    psH.setString(1, orderKey);
    int n = psH.executeUpdate();

    conn.commit();
    return n;
  }
}
/** 부서/담당자 모달 목록 */
public List<OrderDTO> selectAllDep() {
  String sql =
    "SELECT w.worker_id, w.worker_name, w.dapart_ID2, d.depart_level " +
    "FROM worker w " +
    "JOIN department d ON w.dapart_ID2 = d.dapart_ID2 " +
    "ORDER BY d.depart_level, w.worker_name";

  List<OrderDTO> list = new ArrayList<>();
  try (Connection con = getConn();
       PreparedStatement ps = con.prepareStatement(sql);
       ResultSet rs = ps.executeQuery()) {
    while (rs.next()) {
      OrderDTO dto = new OrderDTO();
      dto.setWorker_id(rs.getString("worker_id"));
      dto.setWorker_name(rs.getString("worker_name"));
      dto.setDapart_ID2(rs.getString("dapart_ID2"));
      dto.setDepart_level(rs.getString("depart_level"));
      list.add(dto);
    }
  } catch (Exception e) {
    e.printStackTrace();
  }
  return list;
}

}
