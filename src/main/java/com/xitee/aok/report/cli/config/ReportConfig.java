package com.xitee.aok.report.cli.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class ReportConfig {

    private static final String HTML_SUFFIX = ".html";

    @Value("${report.templates.folder:.}")
    String templateFolder;

    @Bean
    public ITemplateResolver templateResolver() {
        System.out.println("AAA: " + templateFolder);
        final FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix(templateFolder);
        templateResolver.setSuffix(HTML_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    @Bean
    public ISpringTemplateEngine templateEngine() {
        // TODO: Redirect htmltopdf logs
        // https://stackoverflow.com/questions/9729147/turning-on-flying-saucer-java-util-logging-output
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }
}
