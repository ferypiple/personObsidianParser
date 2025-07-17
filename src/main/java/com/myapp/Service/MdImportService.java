package com.myapp.Service;

import com.myapp.dto.CounselorDto;
import com.myapp.dto.PersonDto;
import com.myapp.parser.CounselorMdParser;
import com.myapp.parser.ChildrenParser;
import com.myapp.parser.MinimalChildrenParser;
import com.myapp.sql.entity.Activity;
import com.myapp.sql.entity.PersonActivity;
import com.myapp.sql.repository.ActivityRepository;
import com.myapp.sql.repository.PersonActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.myapp.sql.entity.Counselor;
import com.myapp.sql.entity.Person;
import com.myapp.sql.repository.CounselorRepository;
import com.myapp.sql.repository.PersonRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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


    public void importPersons(Path folder) throws Exception {
        Files.list(folder)
                .filter(p -> p.toString().endsWith(".md"))
                .forEach(md -> {
                    PersonDto dto = useMinimalPersonParser
                            ? minimalChildrenParser.parse(md)
                            : personParser.parse(md);
                    Person entity = mapPerson(dto);
                    if (!personRepo.existsByFullNameAndBirthDate(entity.getFullName(), entity.getBirthDate())) {
                        personRepo.save(entity);
                    }
                });
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
                    }else{
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
        //     p.setTags(dto.getTags() != null ? String.join(",", dto.getTags()) : null);
        p.setCounselors(dto.getCounselors());

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

            Activity activity = activityRepository.findByName(name.trim())
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

}
