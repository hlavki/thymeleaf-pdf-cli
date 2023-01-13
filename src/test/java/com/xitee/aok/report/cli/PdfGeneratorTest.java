package com.xitee.aok.report.cli;

import com.xitee.aok.report.cli.service.TemplateService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

@SpringBootTest
public class PdfGeneratorTest {

    @Autowired TemplateService templateService;

    @Test
    void test() {
        templateService.generatePdf("sample-template",
            Path.of("src/test/templates/sample-template.json"), Path.of("build/out.pdf"));
    }
}
