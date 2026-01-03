package com._plus1.domain.seed.repository;

import com._plus1.common.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    interface IdRow{
        Long getExternalId();
        Long getId();
    }

    @Query("""
		select g.externalId as externalId, g.id as id from Genre g
	""")
    List<IdRow> findIdRows();

    default Map<Long, Long> loadIdMap(){
        List<IdRow> rows = findIdRows();
        // HashMap : 초기 수용량(capacity)을 미리 크게 잡아서 리사이즈(재해시) 비용을 줄이는 최적화
        // HashMap은 기본적으로 load factor(기본 0.75) 를 넘으면 내부 배열을 키우면서(보통 2배) 재해시
        Map<Long, Long> map = new HashMap<>((int)(rows.size() / 0.75f) + 1);
        for(IdRow row : rows) map.put(row.getExternalId(), row.getId());
        return map;
    }
}