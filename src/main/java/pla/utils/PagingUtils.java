package pla.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 페이징 처리를 위한 공통 유틸리티 클래스.
 */
public class PagingUtils {
    /**
     * 리스트를 페이징 처리합니다.
     *
     * @param list     페이징 처리할 리스트
     * @param pageable 페이징 정보
     * @return 페이징된 리스트
     */
    public static <T> Page<T> createPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
