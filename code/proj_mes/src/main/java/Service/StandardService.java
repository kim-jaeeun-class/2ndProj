package Service;

import java.util.*;
import Dao.OrderDAO;
import Dto.OrderDTO;

/**
 * 선택된 분류(category)에 맞춰
 * - thead 컬럼 메타(List<Column>)
 * - tbody 데이터(List<Map<String,Object>>)
 * 를 한 번에 만들어주는 통합 서비스.
 */
public class StandardService {

    /** 화면용 컬럼 메타 */
    public static class Column {
        private final String key;   // row Map에서 읽을 키(= DTO/DAO 필드명/컬럼명과 1:1)
        private final String title; // 헤더에 표시할 이름
        public Column(String key, String title) { this.key = key; this.title = title; }
        public String getKey() { return key; }
        public String getTitle() { return title; }
    }

    /** 결과 DTO */
    public static class TableResult {
        private final String category;
        private final List<Column> columns;
        private final List<Map<String,Object>> data;
        public TableResult(String category, List<Column> columns, List<Map<String,Object>> data) {
            this.category = category;
            this.columns = columns;
            this.data = data;
        }
        public String getCategory() { return category; }
        public List<Column> getColumns() { return columns; }
        public List<Map<String,Object>> getData() { return data; }
    }

    /** 카테고리별 제공자 인터페이스 */
    public interface TableProvider {
        List<Column> columns();
        List<Map<String,Object>> data();
    }

    /** 카테고리 → Provider 매핑 */
    private final Map<String, TableProvider> providers = new HashMap<>();

    public StandardService() {
        // 기본 등록: 발주(orders)
        providers.put("발주", new OrderProvider());

        // 추후 공정/BOM/재고/생산/품질도 같은 패턴으로 providers.put("공정", new ProcessProvider()); 식으로 추가

    }

    /** 확장용 수동 등록 API(원하면 외부에서 주입) */
    public void register(String category, TableProvider provider) {
        providers.put(category, provider);
    }

    /** 메인 엔트리: 카테고리 입력 → 테이블 결과 반환 */
    public TableResult buildTable(String category) {
        TableProvider p = providers.get(category);
        if (p == null) {
            // 미선택/미등록 카테고리는 빈 결과
            return new TableResult(category, Collections.emptyList(), Collections.emptyList());
        }
        return new TableResult(category, p.columns(), p.data());
    }

    /* =========================
       Provider 구현: 발주(orders)
       ========================= */
    private static class OrderProvider implements TableProvider {
        private final OrderDAO dao = new OrderDAO(); // 주어진 DAO 그대로 사용

        @Override
        public List<Column> columns() {
            List<Column> cols = new ArrayList<>();
            cols.add(new Column("order_key",   "발주키"));
            cols.add(new Column("order_number","발주번호"));
            cols.add(new Column("order_date",  "발주일"));
            cols.add(new Column("order_pay",   "결제일"));
            cols.add(new Column("order_state", "상태"));
            cols.add(new Column("client_id",   "거래처ID"));
            cols.add(new Column("worker_id",   "사번"));
            cols.add(new Column("dapart_ID2",  "부서ID")); // DTO/DAO 필드명 그대로
            return cols;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<Map<String, Object>> data() {
            List<Map<String,Object>> rows = new ArrayList<>();
            List<?> list = dao.selectAll(); // 원 DAO 시그니처 유지(raw List)
            for (Object o : list) {
                OrderDTO d = (OrderDTO) o;
                Map<String,Object> row = new LinkedHashMap<>();
                row.put("order_key",    nz(d.getOrder_key()));
                row.put("order_number", nz(d.getOrder_number()));
                row.put("order_date",   d.getOrder_date()); // java.sql.Date 그대로
                row.put("order_pay",    d.getOrder_pay());
                row.put("order_state",  d.getOrder_state());
                row.put("client_id",    nz(d.getClient_id()));
                row.put("worker_id",    nz(d.getWorker_id()));
                row.put("dapart_ID2",   nz(d.getDapart_ID2()));
                rows.add(row);
            }
            return rows;
        }

        private static String nz(String s) { return s == null ? "" : s; }
    }
    
    
    
    


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
