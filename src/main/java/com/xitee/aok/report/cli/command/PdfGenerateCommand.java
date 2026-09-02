package com.xitee.aok.report.cli.command;

import com.xitee.aok.report.cli.service.TemplateService;

import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PdfGenerateCommand {

    private final TemplateService templateService;

    public PdfGenerateCommand(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Command(name = "gen", description = "Generate PDF report")
    public void generate(@Option(shortName = 't', longName = "template", description = "Template ID",
                                 required = true) String templateId,
                         @Option(shortName = 'd', longName = "data", description = "Template JSON data file",
                                 required = true) Path jsonData,
                         @Option(shortName = 'o', longName = "output", description = "Output PDF file path",
                                 defaultValue = "output.pdf") Path outputFile) {

        templateService.generatePdf(templateId, jsonData, outputFile);
    }
}
