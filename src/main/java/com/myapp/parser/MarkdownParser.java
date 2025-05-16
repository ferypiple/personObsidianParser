package com.myapp.parser;

import com.myapp.dto.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MarkdownParser extends AbstractMdParser<PersonDto> {
    private static final Pattern COUNSELORS_PATTERN =
            Pattern.compile("### Вожатые\\s*(?:\\[\\[(.+?)\\]\\].*?)+", Pattern.DOTALL);
    private static final Pattern NAME_PATTERN = Pattern.compile("^(.+?)\\.md$");
    private static final Pattern BIRTH_PATTERN = Pattern.compile("### Дата рождения\\s*([0-3]?\\d\\.[01]?\\d\\.\\d{4})", Pattern.MULTILINE);
    private static final Pattern TAG_PATTERN = Pattern.compile("#([A-Za-zА-Яа-я0-9_]+)");

    @Override
    public PersonDto parse(Path mdFile) {
        PersonDto dto = new PersonDto();
        try {
            String content = Files.readString(mdFile);
            // Full name from file name
            Matcher m = NAME_PATTERN.matcher(mdFile.getFileName().toString());
            if (m.find()) dto.setFullName(m.group(1).replace('_', ' '));

            // Birth date
            Matcher b = BIRTH_PATTERN.matcher(content);
            if (b.find()) dto.setBirthDate(b.group(1));

            // Other fields: naive split by headings
            dto.setParentContacts(extractSection(content, "### Контакты родителей"));
            dto.setChildContacts(extractSection(content, "### Контакты ребенка"));
            dto.setAllergies(extractList(content, "### Аллергии"));
            dto.setDescription(extractSection(content, "# Описание"));
            dto.setTags(extractTags(content));
            List<String> counselors = new ArrayList<>();
            Matcher matcherCounselor = Pattern.compile("\\[\\[(.+?)\\]\\]").matcher(
                    content.split("### Вожатые")[1]
            );
            while (matcherCounselor.find()) {
                counselors.add(matcherCounselor.group(1));
            }
            dto.setCounselors(counselors);
        } catch (IOException e) {
            log.error("Ошибка чтения {}", mdFile, e);
            throw new RuntimeException(e);
        }

        return dto;
    }

    private String extractSection(String content, String header) {
        String[] parts = content.split(header);
        if (parts.length < 2) return null;
        String after = parts[1];
        return after.split("### |# ")[0].trim();
    }

    private List<String> extractList(String content, String header) {
        String section = extractSection(content, header);
        if (section == null) return List.of();
        return List.of(section.split("\r?\n"));
    }



    private List<String> extractTags(String content) {
        Matcher matcher = TAG_PATTERN.matcher(content);
        List<String> tags = new ArrayList<>();
        while (matcher.find()) {
            tags.add(matcher.group(1));  // без ведущего #
        }
        return tags;
    }

}