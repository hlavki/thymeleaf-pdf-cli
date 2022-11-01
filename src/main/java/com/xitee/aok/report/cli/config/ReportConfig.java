package com.xitee.aok.report.cli.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Configuration
public class ReportConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ReportConfig.class);
    private static final String HTML_SUFFIX = ".html";

    @Value("${report.templates.folder:templates/}")
    String templateFolder;

    @Bean
    public ITemplateResolver templateResolver() {
        LOG.info("Templates folder was configured to {}", Path.of(templateFolder).toAbsolutePath());
        final FileTemplateResolver templateResolver = new FileTemplateResolver();
        String prefix = templateFolder.endsWith("/") ? templateFolder : templateFolder + "/";
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(HTML_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        templateResolver.setCacheable(false);
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
