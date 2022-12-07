package com.xitee.aok.report.cli.util;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.springframework.expression.EvaluationContext;

import java.util.LinkedHashMap;

/**
 * Implementation of {@link java.util.Map} that return everytime true for the {@link #containsKey(Object)} call. It is because SPEL can
 * handle null value from map properly but in case SPEL is used with Thymeleaf's
 * {@link org.springframework.context.expression.MapAccessor#read(EvaluationContext, Object, String)} it start to be problem because null
 * is acceptable value only if it is explicitly present keyed. To keep rendering template exception proof, this map hush SPEL and necessity
 * of non-null value can be handled in a template
 *
 * @param <K>
 * @param <V>
 * @author mbabicky-ext
 */
@JsonDeserialize(converter = SpelCompliantMapConverter.class)
public class SpelCompliantMap<K, V> extends LinkedHashMap<K, V> {

    @Override
    public boolean containsKey(Object key) {
        return true;
    }

}
