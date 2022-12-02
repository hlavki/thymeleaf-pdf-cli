package com.xitee.aok.report.cli.service;

import com.xitee.aok.report.cli.util.TemplateUtils;
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

@Service
public class ReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

    @Value("${report.templates.folder}")
    Path templateRootFolder;

    private final ISpringTemplateEngine templateEngine;

    ReportService(ISpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void generatePdf(String templateId, Path outputFile, Context context) {
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
        LOG.info("PDF report was written to {}", outputFile.toAbsolutePath());
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
}
