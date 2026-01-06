package com._plus1.domain.search.component;



import com._plus1.common.exception.CustomException;
import com._plus1.domain.search.model.dto.SearchSort;
import com._plus1.domain.search.model.dto.cache.SearchKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class SearchKeyFactoryTest {

    private SearchKeyFactory searchKeyFactory;

    @BeforeEach
    void setUp() {
        searchKeyFactory = new SearchKeyFactory();
    }

    // 1. 검색어 정규화
    @Test
    void create_shouldNormalizeQuery_whitespacesAndLowercase(){
        // 1). given
        SearchKey key = searchKeyFactory.create(
                " hELlO WoRLd    ", null, null, SearchSort.LATEST,0, 50);

        // 2). when and then
        assertThat(key.q()).isEqualTo("hello world");
        assertThat(key.page()).isEqualTo(0);
        assertThat(key.size()).isEqualTo(50);
        assertThat(key.sort()).isEqualTo(SearchSort.LATEST);
    }

    // 2. Sort 정규화
    @Test
    void create_whenSortNull_shouldDefaultToLatest(){
        // 1). given : Sort null
        SearchKey key = searchKeyFactory.create(
                "hello", null, null, SearchSort.LATEST,0, 50);

        // 2). when and then : sort 검증
        assertThat(key.sort()).isEqualTo(SearchSort.LATEST);
    }

    // 3. null, 음수 페이지 : 0으로 고정.
    @Test
    void create_whenPageNullOrNegative_shouldClampToZero(){
        // 1). given : page null, -42
        SearchKey keyOne = searchKeyFactory.create(
                "hello", null, null, SearchSort.LATEST,null, 50);

        SearchKey keyTwo = searchKeyFactory.create(
                "hello", null, null, SearchSort.LATEST,-42, 50);

        // 2). when and then : page 값 검증
        assertThat(keyOne.page()).isEqualTo(0);
        assertThat(keyTwo.page()).isEqualTo(0);
    }

    // 4. null, 0, 음수 페이지 사이즈 : Max
    @Test
    void create_whenPageSizeNullOrZeroOrNegative_shouldClampToMax(){
        // 1). given : size null, 0, -42
        SearchKey keyOne = searchKeyFactory.create(
                "hello", null, null, SearchSort.LATEST,0, null);
        SearchKey keyTwo = searchKeyFactory.create(
                "hello", null, null, SearchSort.LATEST,0, 0);
        SearchKey keyThree = searchKeyFactory.create(
                "hello", null, null, SearchSort.LATEST,0, -42);

        // 2). when and then : DEFAULT_PAGE_SIZE=50;
        assertThat(keyOne.size()).isEqualTo(50);
        assertThat(keyTwo.size()).isEqualTo(50);
        assertThat(keyThree.size()).isEqualTo(50);
    }

    // 5. 너무 큰 정수 Size
    @Test
    void create_whenPageSizeTooLarge_shouldClampToMax(){
        // 1). given : (int) Math.pow(2,32)-1 : 2의 32-1 승.
        SearchKey key = searchKeyFactory.create(
                "hello", null, null, SearchSort.LATEST,0, (int) Math.pow(2,32)-1);

        // 2). when and then : DEFAULT_MAX_SIZE=100;
        assertThat(key.size()).isEqualTo(100);
    }

    // 6. 공백 검색어
    @Test
    void create_whenQueryBlank_shouldThrow() {
        assertThatThrownBy(()-> searchKeyFactory.create(
                " ", null, null, SearchSort.LATEST,0, 50))
                .isInstanceOf(CustomException.class).hasMessage("검색어를 입력해주세요.");
    }

    // 7. 날짜 범위
    @Test
    void create_whenDateRangeInvalid_shouldThrow(){
        LocalDate from = LocalDate.parse("2020-01-02");
        LocalDate to = LocalDate.parse("2020-01-01");
        assertThatThrownBy(() -> searchKeyFactory.create("hello", from, to, SearchSort.LATEST, 0, 50))
                .isInstanceOf(CustomException.class).hasMessage("잘못된 날짜 범위입니다.");
    }

}
