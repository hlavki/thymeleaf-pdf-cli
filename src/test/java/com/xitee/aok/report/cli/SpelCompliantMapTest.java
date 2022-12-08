package com.xitee.aok.report.cli;

import com.xitee.aok.report.cli.util.SpelCompliantMap;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SpelCompliantMapTest {

    @Test
    void test() {
        SpelCompliantMap<String, Object> map = new SpelCompliantMap<>();
        assertThat(map).containsKey(UUID.randomUUID().toString());
    }
}