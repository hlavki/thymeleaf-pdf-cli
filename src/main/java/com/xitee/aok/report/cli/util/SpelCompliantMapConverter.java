package com.xitee.aok.report.cli.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Converter that is able to convert any Map to {@link SpelCompliantMap}. It is safe event for using on Object types because it returns
 * original value if it is not a Map type. Converter goes in the depth so if any value of the given map is a map, it is converted also.
 *
 * @author mbabicky-ext
 */
public class SpelCompliantMapConverter extends StdConverter<Object, Object> {

    @Override
    public Object convert(Object value) {
        if (value instanceof Collection) {
            convertItems((Collection<Object>) value);
        }
        if (value instanceof Map) {
            return convertToSpelCompliantMap((Map<?, ?>) value);
        }
        return value;
    }

    private void convertItems(Collection<Object> collection) {
        List<Object> storage = new ArrayList<>(collection.size());
        collection.forEach(i -> storage.add(convert(i)));
        collection.clear();
        collection.addAll(storage);
    }

    private Map<?, ?> convertToSpelCompliantMap(Map<?, ?> original) {
        if (!(original instanceof SpelCompliantMap)) {
            SpelCompliantMap<Object, Object> spelCompliantMap = new SpelCompliantMap<>();
            original.forEach((k, v) -> spelCompliantMap.put(k, convert(v)));
            return spelCompliantMap;
        }
        return original;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructSimpleType(Object.class, null);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructSimpleType(Object.class, null);
    }
}
