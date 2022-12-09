package com.xitee.aok.report.cli.service;

import com.xitee.aok.report.cli.util.SpelCompliantMap;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConvertDateAndTimeTest {

    @Autowired TemplateService templateService;

    @Autowired JsonMapper jsonMapper;

    @Test
    void parseDateAndTimeBeforeTemplateIsRendered() throws Exception {

        String jsonData = Files.readString(Path.of("src/test/resources/data-date-time.json"));
        Map<String, Object> dataMap = jsonMapper.readValue(jsonData, SpelCompliantMap.class);

        templateService.convertDates(dataMap);

        Condition<Object> map = new Condition<>(v -> (v instanceof Map), "%s is map", "map");
        Condition<Object> list = new Condition<>(v -> (v instanceof List<?>), "%s is list", "list");

        assertThat(dataMap).containsKey("user").hasValueSatisfying(map);
        Map<String, Object> user = (Map<String, Object>) dataMap.get("user");
        assertThat(user).extracting("firstName").isInstanceOf(String.class);
        assertThat(user).extracting("creationDate").isInstanceOf(LocalDate.class);
        assertThat(user).extracting("creationTime").isInstanceOf(LocalDateTime.class);

        assertThat(dataMap).containsKey("footer").hasValueSatisfying(map);
        Map<String, Object> footer = (Map<String, Object>) dataMap.get("footer");
        assertThat(footer).extracting("bubbleGum").isInstanceOf(String.class);

        assertThat(dataMap).containsKey("listOfObjects").hasValueSatisfying(list);
        List<Object> listOfObjects = (List<Object>) dataMap.get("listOfObjects");
        assertThat(listOfObjects).hasSize(2).first().satisfies(map)
            .extracting("from").isInstanceOf(LocalDate.class);

        assertThat(dataMap).containsKey("listOfValues").hasValueSatisfying(list);
        List<Object> listOfValues = (List<Object>) dataMap.get("listOfValues");
        assertThat(listOfValues).hasSize(2).first().isInstanceOf(LocalDate.class);
    }
}
