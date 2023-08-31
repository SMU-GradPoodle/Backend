package smu.poodle.smnavi.common.dto;


import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResult<T> {
    int totalPage;
    Long totalCount;
    List<T> itemList;

    public PageResult(Page<T> data) {
        totalPage = data.getTotalPages();
        totalCount = data.getTotalElements();
        itemList = data.getContent();
    }

    public static <T> PageResult<T> of(Page<T> data) {
        return new PageResult<>(data);
    }
}
