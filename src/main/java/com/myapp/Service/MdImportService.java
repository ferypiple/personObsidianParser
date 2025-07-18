package com.myapp.Service;

import com.myapp.dto.CounselorDto;
import com.myapp.dto.PersonDto;
import com.myapp.parser.CounselorMdParser;
import com.myapp.parser.ChildrenParser;
import com.myapp.parser.MinimalChildrenParser;
import com.myapp.sql.entity.*;
import com.myapp.sql.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MdImportService {
    private final ChildrenParser personParser;
    private final CounselorMdParser counselorParser;
    private final PersonRepository personRepo;
    private final CounselorRepository counselorRepo;
    private final boolean useMinimalPersonParser; // из ParserModeConfig
    private final MinimalChildrenParser minimalChildrenParser;
    private final ActivityRepository activityRepository;
    private final PersonActivityRepository personActivityRepository;
    private final PersonCounselorRepository personCounselorRepo;


    //    public void importPersons(Path folder) throws Exception {
//        Files.list(folder)
//                .filter(p -> p.toString().endsWith(".md"))
//                .forEach(md -> {
//                    PersonDto dto = useMinimalPersonParser
//                            ? minimalChildrenParser.parse(md)
//                            : personParser.parse(md);
//                    Person entity = mapPerson(dto);
//                    if (!personRepo.existsByFullNameAndBirthDate(entity.getFullName(), entity.getBirthDate())) {
//                        personRepo.save(entity);
//                    }
//                });
//    }
    public void importPersons(Path mdFile) throws Exception {
        if (!Files.isRegularFile(mdFile) || !mdFile.toString().endsWith(".md")) {
            throw new IllegalArgumentException("Ожидался путь к .md файлу, но получено: " + mdFile);
        }

        PersonDto dto = useMinimalPersonParser
                ? minimalChildrenParser.parse(mdFile)
                : personParser.parse(mdFile);

        Person entity = mapPerson(dto);


        // добавляем номер отряда
        Integer detachment = extractDetachmentNumber(mdFile);
        entity.setDetachmentNumber(detachment);

        if (!personRepo.existsByFullNameAndBirthDate(entity.getFullName(), entity.getBirthDate())) {
            personRepo.save(entity);
        }
    }


    public void importCounselors(Path folder) throws Exception {
        Files.list(folder)
                .filter(p -> p.toString().endsWith(".md"))
                .forEach(md -> {
                    CounselorDto dto = counselorParser.parse(md);
                    Counselor entity = mapCounselor(dto);
                    // проверка на уникальность
                    if (!counselorRepo.existsByFullNameAndBirthDate(entity.getFullName(), entity.getBirthDate())) {
                        counselorRepo.save(entity);
                    } else {
                        log.info(entity.getFullName() + " уже существует");
                    }
                });
    }

    private Person mapPerson(PersonDto dto) {
        Person p = new Person();
        p.setFullName(dto.getFullName());
        p.setBirthDate(dto.getBirthDate());
        p.setParentContacts(dto.getParentContacts());
        p.setChildContacts(dto.getChildContacts());
        p.setAllergies(dto.getAllergies() != null ? String.join(",", dto.getAllergies()) : null);
        p.setDescription(dto.getDescription());
        p.setWorkshopName(dto.getWorkshopName());
        p.setWorkshopRating(dto.getWorkshopRating());
        p.setWorkshopMasterRating(dto.getWorkshopMasterRating());
        //     p.setTags(dto.getTags() != null ? String.join(",", dto.getTags()) : null);
        // p.setCounselors(dto.getCounselors());
        log.info("Оценки вожатых для {}: {}", dto.getFullName(), dto.getCounselorRatings());
        assignCounselorsWithRatings(p, dto.getCounselorRatings());
        assignActivitiesToPerson(p, dto.getActivities());

        return p;
    }

    private Counselor mapCounselor(CounselorDto dto) {
        Counselor c = new Counselor();
        c.setFullName(dto.getFullName());
        c.setBirthDate(dto.getBirthDate());
        c.setContacts(dto.getContacts());
        c.setDescription(dto.getDescription());
        c.setCourseStats(dto.getCourseStats());
        c.setFeedback(dto.getFeedback());
        c.setSquadInfo(dto.getSquadInfo());
        return c;
    }

    private void assignActivitiesToPerson(Person person, List<String> activityNames) {
        if (activityNames == null) return;

        for (String name : activityNames) {
            if (name == null || name.isBlank()) continue;

            Activity activity = activityRepository.findByNameIgnoreCase(name.trim())
                    .orElseGet(() -> {
                        Activity newActivity = new Activity();
                        newActivity.setName(name.trim());
                        return activityRepository.save(newActivity);
                    });


            PersonActivity pa = new PersonActivity();
            pa.setPerson(person);
            pa.setActivity(activity);

            person.getPersonActivities().add(pa);
        }
    }

    private void assignCounselorsWithRatings(Person person, Map<String, List<Integer>> ratings) {
        if (ratings == null) return;

        for (Map.Entry<String, List<Integer>> entry : ratings.entrySet()) {
            String name = entry.getKey();
            List<Integer> values = entry.getValue();

            if (values.isEmpty()) continue;
            double avg = values.stream().mapToInt(i -> i).average().orElse(0.0);

            counselorRepo.findByFullName(name).ifPresent(counselor -> {
                PersonCounselor pc = new PersonCounselor();
                pc.setPerson(person);
                pc.setCounselor(counselor);
                pc.setRating(avg);
                person.getCounselorLinks().add(pc);
            });
        }
    }
    private Integer extractDetachmentNumber(Path mdFile) {
        Path parent = mdFile.getParent();
        while (parent != null) {
            String name = parent.getFileName().toString();
            // Пример: "1 отряд", "отряд 2", "3отряд", "Otryad 4", "5 otryad"
            String cleaned = name.toLowerCase().replaceAll("[^0-9]", "");
            if (!cleaned.isEmpty()) {
                try {
                    return Integer.parseInt(cleaned);
                } catch (NumberFormatException e) {
                    // skip invalid
                }
            }
            parent = parent.getParent();
        }
        return null; // не найдено
    }



}
