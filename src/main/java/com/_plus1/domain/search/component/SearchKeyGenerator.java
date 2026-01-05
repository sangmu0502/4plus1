package com._plus1.domain.search.component;

import com._plus1.domain.search.model.dto.SearchSort;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;

@Component("searchKeyGenerator")
public class SearchKeyGenerator implements KeyGenerator {

    // query, from, to, sort, pageable.
    // 호출 순서 의존 + 추론
    @Override
    public Object generate(Object target, Method method, Object... params){
        String q = null;
        LocalDate from = null;
        LocalDate to = null;
        SearchSort sort = null;
        Pageable pageable = null;
        for(Object param : params){
            // 1. query
            if(param instanceof String s) q = s;
            // 2. date
            else if(param instanceof LocalDate date){
                // 1). from이 빈 경우
                if(from==null) from = date;
                // 2). 그 외
                else to = date;
            }
            // 3. sort
            else if(param instanceof SearchSort searchSort){
                sort = searchSort;
            }
            // 4. pageable
            else if(param instanceof Pageable p){
                pageable = p;
            }
        }

        // 5. 정규화
        int page = (pageable == null) ? 0 : pageable.getPageNumber();
        int size = (pageable == null) ? 0 : pageable.getPageSize();

        // 6. return
        return "q=" + q
                + "|from=" + from
                + "|to=" + to
                + "|sort=" + (sort == null ? "null" : sort.name())
                + "|page=" + page
                + "|pageSize=" + size;

        // 기타 : 페이지 결과를 그대로 캐시 vs 페이지 = 0 (사람들이 자주 보는 페이지)
    }
}
