package com._plus1.domain.search.model.dto.query;

public record SearchQuery(
        String raw, // 원문
        String canonical, // 공백 정리 : phrase
        String text, // 분석 : lowercase : multi - match
        String norm // 키 : 공백, 특수문자 제거 + lowercase : term - boost
) {

    // 미응답 공란으로 두기 위한 empty()
    public static SearchQuery empty(){
        return new SearchQuery(null,null,null,null);
    }

    public boolean isEmpty(){
        return text == null || text.isBlank();
    }
}
