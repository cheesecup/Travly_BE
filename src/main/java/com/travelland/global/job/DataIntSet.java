package com.travelland.global.job;

public class DataIntSet {
    private Long id;
    private Integer value;
    public DataIntSet(Long id, Integer value){
        this.id = id;
        this.value = value;
    }
    public Long getId(){
        return this.id;
    }
    public Integer getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return "{id=" + id + ", value=" + value + "}";
    }
}
