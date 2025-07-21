package com.myapp.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
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
            System.out.println("Path = " + personMdPath.toString()); // плохо — может поломаться
            System.out.println("Path = " + new String(personMdPath.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            importService.importCounselors(counselorMdPath);
            importMarkdownFilesRecursively(personMdPath);

            log.info("=== IMPORT completed ===");
        } catch (Exception e) {
            log.error("Ошибка при импорте MD", e);
        }
    }

    private void importMarkdownFilesRecursively(Path root) throws IOException {
        List<Path> mdFiles = findAllMdFiles(root);
        log.info("Найдено {} .md файлов в {}", mdFiles.size(), root);
        for (Path file : mdFiles) {
            try {
                importService.importPersons(file);
            } catch (Exception e) {
                log.warn("Ошибка импорта файла {}", file, e);
            }
        }
    }

    private List<Path> findAllMdFiles(Path root) throws IOException {
        List<Path> result = new ArrayList<>();
        Files.walk(root)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".md"))
                .forEach(result::add);
        return result;
    }

}
