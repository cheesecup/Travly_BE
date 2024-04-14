package com.travelland.global.job;

import lombok.ToString;

public class DataSet {
    private Long id;
    private Integer value;
    public DataSet (Long id, Integer value){
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
