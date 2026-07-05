package uah.es.client.paginator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

public class PageUtils {
    public static <T> Page<T> toPage(List<T> list, Pageable pageable) {
        if (list == null || list.isEmpty()) {
            return Page.empty(pageable);
        }

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = pageSize * currentPage;

        List<T> subList;
        if (list.size() < startItem) {
            subList = Collections.emptyList();
        } else {
            int index = Math.min(startItem + pageSize, list.size());
            subList = list.subList(startItem, index);
        }
        return new PageImpl<>(subList, PageRequest.of(currentPage, pageSize), list.size());
    }
}
