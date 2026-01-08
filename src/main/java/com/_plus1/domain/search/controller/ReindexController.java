package com._plus1.domain.search.controller;

import com._plus1.domain.search.service.index.ReindexService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reindex")
public class ReindexController {

    private final ReindexService reindexService;

    @PostMapping("/songs")
    public String songs(@RequestParam(defaultValue="1000") int batch) throws Exception {
        reindexService.reindexSongs(batch);
        return "ok";
    }

    @PostMapping("/albums")
    public String albums(@RequestParam(defaultValue="1000") int batch) throws Exception {
        reindexService.reindexAlbums(batch);
        return "ok";
    }

    @PostMapping("/artists")
    public String artists(@RequestParam(defaultValue="1000") int batch) throws Exception {
        reindexService.reindexArtists(batch);
        return "ok";
    }
}
