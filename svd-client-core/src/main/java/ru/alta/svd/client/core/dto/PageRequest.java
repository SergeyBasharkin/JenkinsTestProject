package ru.alta.svd.client.core.dto;

import lombok.Data;

@Data
public class PageRequest {
    private int page = 0;
    private int size = 20;

    public PageRequest() {
    }

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getOffset(){
        return page * size;
    }
}
