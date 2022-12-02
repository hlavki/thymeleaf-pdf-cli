package com.xitee.aok.report.cli.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.xitee.aok.report.cli.config.ReportConfig.HTML_SUFFIX;

public final class TemplateUtils {

    private static final Logger log = LoggerFactory.getLogger(TemplateUtils.class);
    private static final char TEMPLATE_ID_SEPARATOR = '.';

    private TemplateUtils() {}

    public static String getTemplate(final String templateId, Path rootPath) throws IOException {

        String firstCandidate = replaceIdSeparator(templateId);
        String secondCandidate = replaceIdSeparator(templateId + ".index");

        String template;
        if (templateExists(firstCandidate, rootPath)) {
            template = firstCandidate;
        } else if (templateExists(secondCandidate, rootPath)) {
            template = secondCandidate;
        } else {
            throw new IOException("Template with ID " + templateId + " does not exists.");
        }

        return template;
    }

    private static boolean templateExists(String template, Path rootPath) throws IOException {
        File templateFile = rootPath.resolve(template + HTML_SUFFIX).toFile();
        log.debug("Checking existence of root template file location: {}", templateFile);
        return rootPath.resolve(template + HTML_SUFFIX).toFile().exists();
    }

    private static String replaceIdSeparator(String templateId) {
        return templateId.replace(TEMPLATE_ID_SEPARATOR, File.separatorChar);
    }

    public static String getParentPath(final String templateId) {
        String parent = templateId;
        int idx = templateId.lastIndexOf(TEMPLATE_ID_SEPARATOR);
        if (idx > -1) {
            parent = templateId.substring(0, idx);
        }
        return parent.replace(TEMPLATE_ID_SEPARATOR, File.separatorChar);
    }
}
