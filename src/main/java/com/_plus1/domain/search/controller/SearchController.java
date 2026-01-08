package com._plus1.domain.search.controller;


import com._plus1.common.dto.CommonResponse;

import com._plus1.domain.search.model.dto.PopularKeywordDto;
import com._plus1.domain.search.model.dto.SearchSort;

import com._plus1.domain.search.model.dto.response.SearchResponse;
import com._plus1.domain.search.model.dto.response.SearchSliceResponse;
import com._plus1.domain.search.service.SearchCacheEvictService;
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
    private final SearchCacheEvictService searchCacheEvictService;

    // 1. Page
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
        return ResponseEntity.ok().body(CommonResponse.success(data, "통합 검색 결과 조회를 성공하였습니다."));
    }

    // 2. PageCache
    @GetMapping("/cache")
    public ResponseEntity<CommonResponse<SearchResponse>> searchPageCache(
            @RequestParam("q") String q,
            @RequestParam(value="from", required=false) LocalDate from,
            @RequestParam(value="to", required=false) LocalDate to,
            @RequestParam(value="sort", required=false, defaultValue="LATEST") SearchSort sort,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value="size", defaultValue = "50") Integer size
    ){
        SearchResponse data = searchService.searchVersionTwo(q, from, to, sort, page, size);
        return ResponseEntity.ok().body(CommonResponse.success(data, "통합 검색 결과 조회를 성공하였습니다."));
    }

    // 3. Slice
    @GetMapping("/slice")
    public ResponseEntity<CommonResponse<SearchSliceResponse>> searchSlice(
            @RequestParam("q") String q,
            @RequestParam(value="from", required=false) LocalDate from,
            @RequestParam(value="to", required=false) LocalDate to,
            @RequestParam(value="sort", required=false, defaultValue="LATEST") SearchSort sort,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value="size", defaultValue = "50") Integer size
    ){
        SearchSliceResponse data = searchService.searchVersionThree(q, from, to, sort, page, size);
        return ResponseEntity.ok().body(CommonResponse.success(data, "통합 검색 결과 조회를 성공하였습니다."));
    }

    // 4. SliceCache
    @GetMapping("/slice/cache")
    public ResponseEntity<CommonResponse<SearchSliceResponse>> searchSliceCache(
            @RequestParam("q") String q,
            @RequestParam(value="from", required=false) LocalDate from,
            @RequestParam(value="to", required=false) LocalDate to,
            @RequestParam(value="sort", required=false, defaultValue="LATEST") SearchSort sort,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value="size", defaultValue = "50") Integer size
    ){
        SearchSliceResponse data = searchService.searchVersionFour(q, from, to, sort, page, size);
        return ResponseEntity.ok().body(CommonResponse.success(data, "통합 검색 결과 조회를 성공하였습니다."));
    }

    // 5. ES
    @GetMapping("/es")
    public ResponseEntity<CommonResponse<SearchSliceResponse>> searchEs(
            @RequestParam("q") String q,
            @RequestParam(value="from", required=false) LocalDate from,
            @RequestParam(value="to", required=false) LocalDate to,
            @RequestParam(value="sort", required=false, defaultValue="LATEST") SearchSort sort,
            @RequestParam(value="page", defaultValue = "0") Integer page,
            @RequestParam(value="size", defaultValue = "50") Integer size
    ){
        SearchSliceResponse data = searchService.searchVersionFive(q, from, to, sort, page, size);
        return ResponseEntity.ok().body(CommonResponse.success(data, "통합 검색 결과 조회를 성공하였습니다."));
    }

    @GetMapping("/popular")
    public ResponseEntity<CommonResponse<List<PopularKeywordDto>>> popular(
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        List<PopularKeywordDto> data = searchService.popular(limit);
        return ResponseEntity.ok(CommonResponse.success(data, "ok"));
    }

    @GetMapping("/clear")
    public ResponseEntity<CommonResponse<Void>> clearAll(){
        searchCacheEvictService.clearAll();
        return ResponseEntity.ok().body(CommonResponse.success(null, "ok"));
    }
}
