package Dto;

import java.sql.Date;

public class StockDTO {

    private String stock_id;     // 재고ID(=LOT)
    private Date stock_date;     // 등록일
    private int stock_loc;       // 위치
    private int stock_div;       // 구분
    private int stock_number;    // 수량
    private String item_code;    // 품목코드
    private int stock_stat;      // 검사전/후 등 상태

    // 파생 카테고리
    private String bigCategory;
    private String midCategory;
    private String smallCategory;

    // ITEM 테이블
    private String item_name;
    private String item_price;   // (DB가 VARCHAR2 이므로 String 유지)

    // getters/setters
    public String getStock_id() { return stock_id; }
    public void setStock_id(String stock_id) { this.stock_id = stock_id; }
    public Date getStock_date() { return stock_date; }
    public void setStock_date(Date stock_date) { this.stock_date = stock_date; }
    public int getStock_loc() { return stock_loc; }
    public void setStock_loc(int stock_loc) { this.stock_loc = stock_loc; }
    public int getStock_div() { return stock_div; }
    public void setStock_div(int stock_div) { this.stock_div = stock_div; }
    public int getStock_number() { return stock_number; }
    public void setStock_number(int stock_number) { this.stock_number = stock_number; }
    public String getItem_code() { return item_code; }
    public void setItem_code(String item_code) { this.item_code = item_code; }
    public int getStock_stat() { return stock_stat; }
    public void setStock_stat(int stock_stat) { this.stock_stat = stock_stat; }

    public String getBigCategory() { return bigCategory; }
    public void setBigCategory(String bigCategory) { this.bigCategory = bigCategory; }
    public String getMidCategory() { return midCategory; }
    public void setMidCategory(String midCategory) { this.midCategory = midCategory; }
    public String getSmallCategory() { return smallCategory; }
    public void setSmallCategory(String smallCategory) { this.smallCategory = smallCategory; }

    public String getItem_name() { return item_name; }
    public void setItem_name(String item_name) { this.item_name = item_name; }
    public String getItem_price() { return item_price; }
    public void setItem_price(String item_price) { this.item_price = item_price; }

    @Override
    public String toString() {
        return "StockDTO [stock_id=" + stock_id + ", stock_date=" + stock_date + ", stock_loc=" + stock_loc
                + ", stock_div=" + stock_div + ", stock_number=" + stock_number + ", item_code=" + item_code
                + ", stock_stat=" + stock_stat + ", bigCategory=" + bigCategory + ", midCategory=" + midCategory
                + ", smallCategory=" + smallCategory + ", item_name=" + item_name + ", item_price=" + item_price + "]";
    }
}
