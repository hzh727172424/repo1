package com.leyou.search.pojo;

import java.util.Map;

public class SearchRequest {
    private String key;
    private Integer page;
    private String sortBy;//排序字段
    private Boolean descending;//是否降序
    private Map<String,String> filter;
    private  static  final int DEFAULT_PAGE=1;
    private static final int DEFAULT_SIZE=20;

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }


    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page==null){
            return DEFAULT_PAGE;
        }
        return Math.max(page, DEFAULT_PAGE);
    }

    public void setPage(Integer page) {
        this.page = page;
    }
    public Integer getSize(){
        return DEFAULT_SIZE;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }
}
