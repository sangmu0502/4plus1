package com._plus1.domain.seed.component;

import com._plus1.domain.seed.service.SeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component
// yml 설정.
@Profile("seed")
@RequiredArgsConstructor
public class SeedRunner implements CommandLineRunner {

    // 1. yml에 설정한 경로
    @Value("${seed.dir}")
    private String dir;

    // 2. limit
    @Value("${seed.limit:0}")
    private int limit;

    // 3. 실행용.
    private final SeedService seedService;


    // 4. 실행(CommandLineReader : 상속)
    @Override
    public void run(String... args) throws Exception{
        // 1). dir, limit 콘솔 정보
        log.info("[SEED] dir={}, limit={}", dir, limit);

        // 2). 실제 실행.
        seedService.seedAll(Path.of(dir), limit);

        // 3). 됐는지 체크.
        log.info("[SEED] DONE");

    }
}