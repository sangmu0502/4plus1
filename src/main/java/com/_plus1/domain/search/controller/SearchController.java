package com._plus1.domain.search.controller;


import com._plus1.common.dto.CommonResponse;
import com._plus1.domain.search.model.dto.PopularKeywordDto;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    @GetMapping
    public ResponseEntity<CommonResponse<SearchResponse>> search(
            @RequestParam("q") String q,
            @RequestParam(value="from", required=false) LocalDate from,
            @RequestParam(value="to", required=false) LocalDate to,
            @RequestParam(value="sort", required=false, defaultValue="LATEST") SearchSort sort,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value="size", defaultValue = "50") Integer size
    ){
        SearchResponse data = searchService.search(q, from, to, sort, page, size);
        return ResponseEntity.ok().body(CommonResponse.success(data, "ok"));
    }

    // cache
    @GetMapping("/ver2")
    public ResponseEntity<CommonResponse<SearchResponse>> searchVersionTwo(
            @RequestParam("q") String q,
            @RequestParam(value="from", required=false) LocalDate from,
            @RequestParam(value="to", required=false) LocalDate to,
            @RequestParam(value="sort", required=false, defaultValue="LATEST") SearchSort sort,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value="size", defaultValue = "50") Integer size
    ){
        SearchResponse data = searchService.searchVersionTwo(q, from, to, sort, page, size);
        return ResponseEntity.ok().body(CommonResponse.success(data, "ok"));
    }

    @GetMapping("/popular")
    public ResponseEntity<CommonResponse<List<PopularKeywordDto>>> popular(
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        List<PopularKeywordDto> data = searchService.popular(limit);
        return ResponseEntity.ok(CommonResponse.success(data, "ok"));
    }
}
