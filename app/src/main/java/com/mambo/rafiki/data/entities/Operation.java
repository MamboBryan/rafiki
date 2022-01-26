package com.mambo.rafiki.data.entities;

public class Operation {

    private Object item;
    private String type;

    public Operation(Object item, String type) {
        this.item = item;
        this.type = type;
    }

    public Object getItem() {
        return item;
    }

    public String getType() {
        return type;
    }
}
