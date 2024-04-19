
package com.travelland.global.job;

public class DataStrSet {
    private Long id;
    private String value;
    public DataStrSet(Long id, String value){
        this.id = id;
        this.value = value;
    }
    public Long getId(){
        return this.id;
    }
    public String getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return "{id=" + id + ", value=" + value + "}";
    }
}
