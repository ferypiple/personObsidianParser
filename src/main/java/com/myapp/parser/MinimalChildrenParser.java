package com.myapp.parser;

import com.myapp.dto.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MinimalChildrenParser extends AbstractMdParser<PersonDto> {

    private static final Pattern NAME_PATTERN = Pattern.compile("fio:\\s*(.+)");
    private static final Pattern DOB_PATTERN = Pattern.compile("dob:\\s*(\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern PHONE_PATTERN = Pattern.compile("phone:\\s*(\\S+)?");
    private static final Pattern NOTES_PATTERN = Pattern.compile("notes:\\s*(.+)");

    private static final Pattern WORKSHOP_PATTERN =
            Pattern.compile("Оценка мастерской\\s+#([А-Яа-яA-Za-z0-9_]+)\\s*/(\\d+)");
    private static final Pattern HASH_TAG_PATTERN = Pattern.compile("#+([А-Яа-яA-Za-z0-9_]+)");

    private static final Pattern COUNSELOR_PATTERN = Pattern.compile("\\[\\[(.+?)\\]\\]");

    private static final Pattern PARENT_CONTACT_PATTERN =
            Pattern.compile("(?m)^\\|\\s*(.+?)\\s*\\|\\s*(\\d{5,})\\s*\\|");

    private static final Pattern ALLERGY_SECTION_PATTERN =
            Pattern.compile("### Аллергии\\s*([\\s\\S]+?)(?=\\n#|\\z)", Pattern.MULTILINE);

    private static final Pattern ACTIVITY_SECTION_PATTERN =
            Pattern.compile("\\*\\*Мероприятия, которые понравились\\?\\*\\*\\s*\\n([#А-Яа-яA-Za-z0-9_ \\t]+)", Pattern.DOTALL);
    private static final Pattern COUNSELOR_RATING_PATTERN =
            Pattern.compile("\\*\\*оценка\\*\\* \\[\\[(.+?)]]\\s*/(\\d+)", Pattern.CASE_INSENSITIVE);

    @Override
    public PersonDto parse(Path mdFile) {
        PersonDto dto = new PersonDto();

        try {
            String content = Files.readString(mdFile);

            // ФИО
            Matcher nameMatcher = NAME_PATTERN.matcher(content);
            if (nameMatcher.find()) dto.setFullName(nameMatcher.group(1));

            // Дата рождения
            Matcher dobMatcher = DOB_PATTERN.matcher(content);
            if (dobMatcher.find()) dto.setBirthDate(dobMatcher.group(1));

            // Телефон (ребенка)
            Matcher phoneMatcher = PHONE_PATTERN.matcher(content);
            if (phoneMatcher.find()) dto.setChildContacts(phoneMatcher.group(1));


            //Мастерские
            Matcher wsMatcher = WORKSHOP_PATTERN.matcher(content);
            if (wsMatcher.find()) {
                dto.setWorkshopName(wsMatcher.group(1));
                dto.setWorkshopRating(Integer.parseInt(wsMatcher.group(2)));
            }
            // Notes → description
            Matcher notesMatcher = NOTES_PATTERN.matcher(content);
            if (notesMatcher.find()) dto.setDescription(notesMatcher.group(1).trim());

            // Вожатые (уникальные)
            Set<String> counselors = new LinkedHashSet<>();
            Matcher counselorMatcher = COUNSELOR_PATTERN.matcher(content);
            while (counselorMatcher.find()) {
                counselors.add(counselorMatcher.group(1));
            }
            dto.setCounselors(new ArrayList<>(counselors));

            // Контакты родителей (из таблицы)
            Matcher parentMatcher = PARENT_CONTACT_PATTERN.matcher(content);
            StringBuilder parentContacts = new StringBuilder();
            while (parentMatcher.find()) {
                parentContacts.append(parentMatcher.group(1)).append(": ").append(parentMatcher.group(2)).append("\n");
            }
            dto.setParentContacts(parentContacts.toString().trim());

            // Аллергии
            Matcher allergyMatcher = ALLERGY_SECTION_PATTERN.matcher(content);
            if (allergyMatcher.find()) {
                String block = allergyMatcher.group(1).trim();
                List<String> allergyList = List.of(block.split("\\r?\\n"));
                dto.setAllergies(allergyList);
            }

            // Мероприятия (из блока с **Мероприятия, которые понравились?**)
            Matcher actMatcher = ACTIVITY_SECTION_PATTERN.matcher(content);
            List<String> activities = new ArrayList<>();
            if (actMatcher.find()) {
                String tagBlock = actMatcher.group(1);
                Matcher tagMatcher = HASH_TAG_PATTERN.matcher(tagBlock);
                while (tagMatcher.find()) {
                    activities.add(tagMatcher.group(1));
                }
            }
            dto.setActivities(activities);

            //Оценки(Среднее арифметическое)
            Map<String, List<Integer>> ratings = new HashMap<>();
            Matcher ratingMatcher = COUNSELOR_RATING_PATTERN.matcher(content);
            while (ratingMatcher.find()) {
                String name = ratingMatcher.group(1).trim();
                int value = Integer.parseInt(ratingMatcher.group(2));

                ratings.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            }
            dto.setCounselorRatings(ratings);


        } catch (IOException e) {
            log.error("Ошибка при разборе markdown файла: {}", mdFile, e);
            throw new RuntimeException(e);
        }

        return dto;
    }
}
