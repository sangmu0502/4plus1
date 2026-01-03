package com._plus1.domain.seed.repository;

import com._plus1.common.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    interface IdRow{
        Long getExternalId();
        Long getId();
    }

    @Query("""
		select a.externalId as externalId, a.id as id from Artist a
	""")
    List<IdRow> findIdRows();

    default Map<Long, Long> loadIdMap(){
        List<IdRow> rows = findIdRows();
        Map<Long, Long> map = new HashMap<>((int)(rows.size()/ 0.75f)+1);
        for(IdRow row : rows) map.put(row.getExternalId(), row.getId());
        return map;
    }
}