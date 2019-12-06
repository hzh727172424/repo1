package com.leyou.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Long total;//总条数
    private Integer totalpage;//总页数
    List<T> items;//数据

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult() {
    }

    public PageResult(Long total, Integer totalpage, List<T> items) {
        this.total = total;
        this.totalpage = totalpage;
        this.items = items;
    }
}
