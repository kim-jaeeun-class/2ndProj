package Dto;

public class ClientDTO {
    private String client_id;
    private String client_name;
    private String client_phone;
    private String business_number;
    private String business_item;
    private String client_address;
    private String inout_division; 
    private String worker_id;

    public String getClient_id() { return client_id; }
    public void setClient_id(String client_id) { this.client_id = client_id; }

    public String getClient_name() { return client_name; }
    public void setClient_name(String client_name) { this.client_name = client_name; }

    public String getClient_phone() { return client_phone; }
    public void setClient_phone(String client_phone) { this.client_phone = client_phone; }

    public String getBusiness_number() { return business_number; }
    public void setBusiness_number(String business_number) { this.business_number = business_number; }

    public String getBusiness_item() { return business_item; }
    public void setBusiness_item(String business_item) { this.business_item = business_item; }

    public String getClient_address() { return client_address; }
    public void setClient_address(String client_address) { this.client_address = client_address; }

    public String getInout_division() { return inout_division; }
    public void setInout_division(String inout_division) { this.inout_division = inout_division; }

    public String getWorker_id() { return worker_id; }
    public void setWorker_id(String worker_id) { this.worker_id = worker_id; }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "client_id='" + client_id + '\'' +
                ", client_name='" + client_name + '\'' +
                ", client_phone='" + client_phone + '\'' +
                ", business_number='" + business_number + '\'' +
                ", business_item='" + business_item + '\'' +
                ", client_address='" + client_address + '\'' +
                ", inout_division='" + inout_division + '\'' +
                ", worker_id='" + worker_id + '\'' +
                '}';
    }
}
