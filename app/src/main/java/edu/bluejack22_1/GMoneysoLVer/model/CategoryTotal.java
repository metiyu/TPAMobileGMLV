package edu.bluejack22_1.GMoneysoLVer.model;

public class CategoryTotal {
    private Integer total;
    private String id, name, type;
    public CategoryTotal(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CategoryTotal(String id, String name, String type, Integer total) {
        this.total = total;
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
