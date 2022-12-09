package com.xitee.aok.report.cli.command;

import com.xitee.aok.report.cli.service.TemplateService;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.nio.file.Path;

@ShellComponent
public class PdfGenerateCommand {

    private final TemplateService templateService;

    public PdfGenerateCommand(TemplateService templateService) {
        this.templateService = templateService;
    }

    @ShellMethod(key = "gen", value = "Generate PDF report")
    public void generate(@ShellOption(value = "-t", help = "Template ID") String templateId,
                         @ShellOption(value = "-d", help = "Template JSON data file") Path jsonData,
                         @ShellOption(value = "-o", help = "Output PDF file path", defaultValue = "output.pdf") Path outputFile) {

        templateService.generatePdf(templateId, jsonData, outputFile);
    }
}
