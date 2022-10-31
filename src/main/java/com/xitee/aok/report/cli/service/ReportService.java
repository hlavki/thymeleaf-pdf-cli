package com.xitee.aok.report.cli.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

@Service
public class ReportService {

    @Value("${report.templates.folder:.}")
    String templateFolder;

    private final ISpringTemplateEngine templateEngine;
    ReportService(ISpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void generatePdf(Path templatePath, Path outputFile, Context context) {
        Path contextPath = Path.of(templateFolder);
        String templateName = templatePath.getFileName().toString();
        String html = templateEngine.process(templateName, context);
        try (OutputStream outputStream = new FileOutputStream(outputFile.toFile())) {
            final PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            String xhtml = convertToXhtml(html);
            builder.withHtmlContent(xhtml, contextPath.toUri().toString());
            builder.toStream(outputStream);
            builder.run();

        } catch (IOException e) {
            throw new RuntimeException("Cannot generate PDF from template " + templatePath, e);
        }
    }

    public String convertToXhtml(String html) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().charset("UTF-8");
        return document.html();
    }

//    private ITemplateResolver templateResolver(Path contextPath) {
//        final FileTemplateResolver templateResolver = new FileTemplateResolver();
//        templateResolver.setPrefix(contextPath.toString() + "/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
//        templateResolver.setCacheable(true);
//        return templateResolver;
//    }
//
//    private ISpringTemplateEngine templateEngine(Path contextPath) {
//        // TODO: Redirect htmltopdf logs
//        // https://stackoverflow.com/questions/9729147/turning-on-flying-saucer-java-util-logging-output
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.setTemplateResolver(templateResolver(contextPath));
//        return templateEngine;
//    }
}
