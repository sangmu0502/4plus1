package com._plus1.domain.search.repository;

import com._plus1.common.entity.PopularSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PopularSearchRepository extends JpaRepository<PopularSearch, Long> {
    // 1. Top N : Pageable
    @Query("select p from PopularSearch p order by p.count desc, p.id desc")
    List<PopularSearch> findTop(Pageable pageable);


    // 2. Upsert
    // DB nativeQuery = true : MySQL 그대로 실행.
    // INSERT INTO popular_searches(keyword, count) VALUES(:keyword, 1) : keyword 처음 등장 시, 행 없으므로, 새로 만든 다음 count = 1로 시작.
    // ON DUPLICATE KEY UPDATE count = count + 1 : 이미 같은 키로 행이 존재하여 Unique - PK 충돌 시, insert 대신 update로 전환하여 count를 1 증가시킨다.
    // 두 스레드가 동시에 없음 -> 둘 다 INSERT 시도 : 동시성에서 레이스 컨디션 발생.
    // Modifying : 쓰기 쿼리 명시. 원자적 업데이트.
    @Modifying(flushAutomatically = true)
    @Query(value = """
    INSERT INTO popular_searches(keyword, count)
    VALUES (:keyword, 1)
    ON DUPLICATE KEY UPDATE count = count + 1
    """, nativeQuery = true)
    void upsertIncrement(@Param("keyword")String keyword);
}
