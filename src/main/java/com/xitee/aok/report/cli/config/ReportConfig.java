package com.xitee.aok.report.cli.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
    public static final String HTML_SUFFIX = ".html";

    @Value("${report.templates.folder}")
    String templateFolder;

    @Bean
    JsonMapper jsonMapper() {
        return JsonMapper.builder()
            .serializationInclusion(JsonInclude.Include.NON_EMPTY)
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRAP_ROOT_VALUE, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.UNWRAP_ROOT_VALUE).build();
    }

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
