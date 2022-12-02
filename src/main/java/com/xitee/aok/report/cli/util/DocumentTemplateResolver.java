package com.xitee.aok.report.cli.util;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.util.ContentTypeUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

import java.io.File;
import java.util.Map;

public class DocumentTemplateResolver extends FileTemplateResolver {

    @Override
    protected String computeResourceName(
        final IEngineConfiguration configuration, final String ownerTemplate, final String template,
        final String prefix, final String suffix, final boolean forceSuffix,
        final Map<String, String> templateAliases, final Map<String, Object> templateResolutionAttributes) {

        Validate.notNull(template, "Template name cannot be null");

        String unaliasedName = templateAliases.get(template);
        if (unaliasedName == null) {
            unaliasedName = template;
        }

        final boolean hasPrefix = !StringUtils.isEmptyOrWhitespace(prefix);
        final boolean hasSuffix = !StringUtils.isEmptyOrWhitespace(suffix);

        final boolean shouldApplySuffix =
            hasSuffix && (forceSuffix || !ContentTypeUtils.hasRecognizedFileExtension(unaliasedName));

        String customPrefix = prefix;
        boolean hasCustomPrefix = false;
        if (ownerTemplate != null) {
            int idx = ownerTemplate.lastIndexOf(File.separatorChar);
            if (idx > -1) {
                String templatePath = ownerTemplate.substring(0, ownerTemplate.lastIndexOf(File.separatorChar));
                customPrefix = prefix + templatePath + File.separator;
                hasCustomPrefix = true;
            }
        }

        String resourceName;
        if (!hasPrefix && !shouldApplySuffix) {
            resourceName = unaliasedName;
        } else if (!hasPrefix) { // shouldApplySuffix
            resourceName = unaliasedName + suffix;
        } else if (!shouldApplySuffix) { // hasPrefix
            resourceName = (hasCustomPrefix ? customPrefix : prefix) + unaliasedName;
        } else {
            // hasPrefix && shouldApplySuffix
            resourceName = (hasCustomPrefix ? customPrefix : prefix) + unaliasedName + suffix;
        }

        return resourceName;
    }
}
