package cn.overthinker.common.core.domain.dto;

import lombok.Setter;

@Setter
public class PageQueryDTO {
    private Integer pageSize = 10;
    private Integer pageNum = 1;

    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }

    public Integer getPageNum() {
        return pageNum == null ? 1 : pageNum;
    }
}
