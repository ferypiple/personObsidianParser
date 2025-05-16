package com.myapp.Service;

import com.myapp.dto.CounselorDto;
import com.myapp.dto.PersonDto;
import com.myapp.parser.CounselorMdParser;
import com.myapp.parser.MarkdownParser;
import com.myapp.sql.entity.PersonTag;
import com.myapp.sql.entity.Tag;
import com.myapp.sql.repository.PersonTagRepository;
import com.myapp.sql.repository.TagRepository;
import lombok.RequiredArgsConstructor;
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
public class MdImportService {
    private final MarkdownParser personParser;
    private final CounselorMdParser counselorParser;
    private final PersonRepository personRepo;
    private final CounselorRepository counselorRepo;
    private final TagRepository tagRepository;
    private final PersonTagRepository personTagRepository;


    public void importPersons(Path folder) throws Exception {
        Files.list(folder)
                .filter(p -> p.toString().endsWith(".md"))
                .forEach(md -> {
                    PersonDto dto = personParser.parse(md);
                    Person entity = mapPerson(dto);
                    personRepo.save(entity);
                });
    }

    public void importCounselors(Path folder) throws Exception {
        Files.list(folder)
                .filter(p -> p.toString().endsWith(".md"))
                .forEach(md -> {
                    CounselorDto dto = counselorParser.parse(md);
                    Counselor entity = mapCounselor(dto);
                    counselorRepo.save(entity);
                });
    }


    private Person mapPerson(PersonDto dto) {
        Person p = new Person();
        p.setFullName(dto.getFullName());
        p.setBirthDate(dto.getBirthDate());
        p.setParentContacts(dto.getParentContacts());
        p.setChildContacts(dto.getChildContacts());
        p.setAllergies(String.join(",", dto.getAllergies()));
        p.setDescription(dto.getDescription());
        p.setTags(String.join(",", dto.getTags()));
        p.setCounselors(dto.getCounselors());
        assignTagsToPerson(p, dto.getTags()); // здесь применяем новую логику
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
    private void assignTagsToPerson(Person person, List<String> tagNames) {
        for (String tagName : tagNames) {
            if (tagName == null || tagName.isBlank()) continue;

            Tag tag = tagRepository.findByName(tagName.trim())
                    .orElseGet(() -> tagRepository.save(new Tag(tagName.trim())));

            PersonTag personTag = new PersonTag();
            personTag.setPerson(person);
            personTag.setTag(tag);

            person.getPersonTags().add(personTag); // если есть метод getPersonTags()
        }
    }

}
