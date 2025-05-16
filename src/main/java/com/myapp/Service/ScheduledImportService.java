package com.myapp.Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component               // бин должен создаваться
@RequiredArgsConstructor
@Slf4j
public class ScheduledImportService {
    private final MdImportService importService;
    private final Path personMdPath;
    private final Path counselorMdPath;

    @PostConstruct
    public void checkPaths() {
        log.info("Person MD path = {}, exists = {}", personMdPath, Files.exists(personMdPath));
        log.info("Counselor MD path = {}, exists = {}", counselorMdPath, Files.exists(counselorMdPath));
    }

    @Scheduled(fixedDelayString = "${md.import.interval-ms:600000}", initialDelayString = "10000")
    public void doImport() {
        log.info("=== START import ===");
        try {
            importService.importPersons(personMdPath);
            importService.importCounselors(counselorMdPath);
            log.info("=== IMPORT completed ===");
        } catch (Exception e) {
            log.error("Ошибка при импорте MD", e);
        }
    }
}