package Dto;

public class CategoryDTO {
    private String code;
    private String name;
    private String group; // 소분류에서만 사용(SM_GROUP)

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
}
