package Dto;

public class ItemDTO {

	private String item_code;
	private String item_name;
	private String item_bigo;
	private int item_type;
	private String item_price;
	
	
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getItem_bigo() {
		return item_bigo;
	}
	public void setItem_bigo(String item_bigo) {
		this.item_bigo = item_bigo;
	}
	public int getItem_type() {
		return item_type;
	}
	public void setItem_type(int item_type) {
		this.item_type = item_type;
	}
	public String getItem_price() {
		return item_price;
	}
	public void setItem_price(String item_price) {
		this.item_price = item_price;
	}
	
	
	@Override
	public String toString() {
		return "ItemDTO [item_code=" + item_code + ", item_name=" + item_name + ", item_bigo=" + item_bigo
				+ ", item_type=" + item_type + ", item_price=" + item_price + "]";
	}
	
	
	
	
}
