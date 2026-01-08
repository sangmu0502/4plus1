-- -- Indexing 쿼리

-- 장르 코드 검색 최적화
DROP INDEX idx_genres_genre_code ON genres;
CREATE INDEX idx_genres_genre_code ON genres (genre_code);

-- 장르 -> 곡 매핑 탐색 최적화
DROP INDEX idx_song_genre_genre_song ON song_genre;
CREATE INDEX idx_song_genre_genre_song ON song_genre (genre_id, song_id);

-- 최신순 정렬 + 페이징 최적화
DROP INDEX idx_songs_release_date_id ON songs;
CREATE INDEX idx_songs_release_date_id ON songs (release_date DESC, id);