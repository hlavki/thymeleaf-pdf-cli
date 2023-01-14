package com.xitee.aok.report.cli;

import com.xitee.aok.report.cli.service.TemplateService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PdfGeneratorTest {

    @Autowired TemplateService templateService;

    @Test
    void test() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        templateService.generatePdf("sample-template",
            Path.of("src/test/templates/sample-template.json"), out);

        assertThat(out.toByteArray()).isNotNull().hasSizeGreaterThan(100 * 1024);
    }
}
