package com.xitee.aok.report.cli.command;

import com.xitee.aok.report.cli.service.TemplateService;
import com.xitee.aok.report.cli.util.SpelCompliantMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

@ShellComponent
public class PdfGenerateCommand {
    private static final Logger log = LoggerFactory.getLogger(PdfGenerateCommand.class);
    public static final Pattern DATE_PATTERN = Pattern.compile("^([0-9]{4}).(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
    public static final Pattern DATE_TIME_PATTERN = Pattern.compile(
        "^(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d+)|(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d)|(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d)$");

    private final TemplateService templateService;

    public PdfGenerateCommand(TemplateService templateService) {
        this.templateService = templateService;
    }

    @ShellMethod(key = "gen", value = "Generate PDF report")
    public void generate(@ShellOption(value = "-t", help = "Template ID") String templateId,
                         @ShellOption(value = "-d", help = "Template JSON data file") Path jsonData,
                         @ShellOption(value = "-o", help = "Output PDF file path", defaultValue = "output.pdf") Path outputFile) {

        try {
            Context context = prepareContext(jsonData.toFile());
            templateService.generatePdf(templateId, outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Context prepareContext(File jsonFile) throws IOException {
        JsonMapper jsonMapper = JsonMapper.builder()
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRAP_ROOT_VALUE, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.UNWRAP_ROOT_VALUE).build();

        Map<String, Object> dataMap = jsonMapper.readValue(jsonFile, SpelCompliantMap.class);
        convertDates(dataMap);

        Context ctx = new Context();
        ctx.setVariables(dataMap);

        return ctx;
    }

    void convertDates(Map<String, Object> dataMap) {
        for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                convertDates((Map) value);
            } else if (value instanceof String) {
                String strValue = (String) value;
                if (DATE_PATTERN.matcher(strValue).matches()) {
                    log.debug("Converting {} with value {} to LocalDate", entry.getKey(), strValue);
                    LocalDate date = LocalDate.parse(strValue, DateTimeFormatter.ISO_DATE);
                    dataMap.put(entry.getKey(), date);
                } else if (DATE_TIME_PATTERN.matcher(strValue).matches()) {
                    log.debug("Converting {} with value {} to LocalDateTime", entry.getKey(), strValue);
                    LocalDateTime dateTime = LocalDateTime.parse(strValue, DateTimeFormatter.ISO_DATE_TIME);
                    dataMap.put(entry.getKey(), dateTime);
                }
            }
        }
    }
}
