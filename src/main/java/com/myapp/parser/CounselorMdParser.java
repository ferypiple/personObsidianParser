package com.myapp.parser;

import com.myapp.dto.CounselorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CounselorMdParser extends AbstractMdParser<CounselorDto> {
    private static final Pattern BIRTH_PATTERN = Pattern.compile("### Дата рождения\\s*([0-3]?\\d\\.[01]?\\d\\.\\d{4})");
    private static final Pattern CONTACTS_PATTERN = Pattern.compile("### Контакты\\s*(.+?)(?:###|##|#|$)", Pattern.DOTALL);
    private static final Pattern SQUAD_PATTERN   = Pattern.compile("# Работа с отрядом\\s*(.+?)(?:###|##|#|$)", Pattern.DOTALL);
    @Override
    public CounselorDto parse(Path mdFile) {
        CounselorDto dto = new CounselorDto();
        try {
            String content = Files.readString(mdFile);
            dto.setFullName(mdFile.getFileName().toString().replace(".md", "").replace('_',' '));
            // Дата рождения
            Matcher mb = BIRTH_PATTERN.matcher(content);
            if (mb.find()) dto.setBirthDate(mb.group(1));
            // Контакты
            Matcher mc = CONTACTS_PATTERN.matcher(content);
            if (mc.find()) dto.setContacts(mc.group(1).trim());
            // Работа с отрядом
            Matcher ms = SQUAD_PATTERN.matcher(content);
            if (ms.find()) dto.setSquadInfo(ms.group(1).trim());
            dto.setDescription(extractSection(content, "# Описание"));
            dto.setCourseStats(extractSection(content, "### Курсы"));
            dto.setFeedback(extractSection(content, "## Сработка в конце"));
        } catch (IOException e) {
            log.error("Ошибка чтения {}", mdFile, e);
            throw new RuntimeException(e);
        }
        return dto;
    }

    private String extractSection(String content, String header) {
        String[] parts = content.split(header);
        if (parts.length < 2) return null;
        return parts[1].split("### |## |# ")[0].trim();
    }
}
