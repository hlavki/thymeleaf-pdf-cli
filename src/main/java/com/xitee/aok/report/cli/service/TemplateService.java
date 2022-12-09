package com.xitee.aok.report.cli.service;

import com.xitee.aok.report.cli.util.SpelCompliantMap;
import com.xitee.aok.report.cli.util.TemplateUtils;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    public static final Pattern DATE_PATTERN = Pattern.compile("^([0-9]{4}).(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
    public static final Pattern DATE_TIME_PATTERN = Pattern.compile(
        "^(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d+)|(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d)|(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d)$");

    @Value("${report.templates.folder}")
    Path templateRootFolder;

    private final ISpringTemplateEngine templateEngine;

    private final JsonMapper jsonMapper;

    TemplateService(ISpringTemplateEngine templateEngine, JsonMapper jsonMapper) {
        this.templateEngine = templateEngine;
        this.jsonMapper = jsonMapper;
    }

    public void generatePdf(String templateId, Path jsonData, Path outputFile) {
        try {
            Map<String, Object> dataMap = jsonMapper.readValue(jsonData.toFile(), SpelCompliantMap.class);
            convertDates(dataMap);

            Context context = new Context();
            context.setVariables(dataMap);

            generatePdf(templateId, context, outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot generate PDF from template " + templateId, e);
        }
    }

    public void generatePdf(String templateId, Context context, Path outputFile) {
        try (OutputStream outputStream = new FileOutputStream(outputFile.toFile())) {
            String template = TemplateUtils.getTemplate(templateId, templateRootFolder);
            String html = templateEngine.process(template, context);
            final PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            String xhtml = convertToXhtml(html);
            builder.withHtmlContent(xhtml, getContextPath(templateId));
            builder.toStream(outputStream);
            builder.run();
        } catch (IOException e) {
            throw new RuntimeException("Cannot generate PDF from template " + templateId, e);
        }
        log.info("PDF report was written to {}", outputFile.toAbsolutePath());
    }

    public String convertToXhtml(String html) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().charset("UTF-8");
        return document.html();
    }

    private String getContextPath(String templateId) throws IOException {
        String contextPath = templateRootFolder.resolve(TemplateUtils.getParentPath(templateId)).toUri() + "";
        contextPath = !contextPath.endsWith("/") ? contextPath + "/" : contextPath;
        return contextPath;
    }

    void convertDates(Map<String, Object> dataMap) {
        for (Map.Entry<String, ?> entry : dataMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String strValue = (String) value;
                dataMap.put(entry.getKey(), parseDateOrTime(strValue));
            } else if (value instanceof Map) {
                convertDates((Map<String, Object>) value);
            } else if (value instanceof List) {
                convertDates((List<Object>) value);
            }
        }
    }

    private void convertDates(List<Object> data) {
        int idx = 0;
        for (Object value : data) {
            if (value instanceof String) {
                String strValue = (String) value;
                data.set(idx, parseDateOrTime(strValue));
            } else if (value instanceof Map) {
                convertDates((Map<String, Object>) value);
            } else if (value instanceof List<?>) {
                convertDates((List<Object>) value);
            }
            idx++;
        }
    }

    private Object parseDateOrTime(String strValue) {
        Object result = strValue;
        if (DATE_PATTERN.matcher(strValue).matches()) {
            log.debug("Converting value {} to LocalDate", strValue);
            LocalDate date = LocalDate.parse(strValue, DateTimeFormatter.ISO_DATE);
            result = date;
        } else if (DATE_TIME_PATTERN.matcher(strValue).matches()) {
            log.debug("Converting value {} to LocalDateTime", strValue);
            LocalDateTime dateTime = LocalDateTime.parse(strValue, DateTimeFormatter.ISO_DATE_TIME);
            result = dateTime;
        }
        return result;
    }
}
